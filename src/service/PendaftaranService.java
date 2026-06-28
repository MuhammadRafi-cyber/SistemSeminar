package service;

import dao.*;
import enums.*;
import exception.*;
import model.*;
import util.KodeGenerator;
import util.Validator;
import java.sql.SQLException;
import java.util.List;

/**
 * PendaftaranService — business logic pendaftaran + pembayaran manual + refund.
 *
 * ALUR PEMBAYARAN v5:
 *   1. daftar()        → Pendaftaran PENDING, Pembayaran PENDING, kuota BELUM berkurang
 *   2. konfirmasiBayar()→ User pilih metode & konfirmasi → PaymentService dijalankan
 *                         BERHASIL → Pendaftaran CONFIRMED, Pembayaran BERHASIL, kuota berkurang
 *                         GAGAL    → Pendaftaran tetap PENDING, Pembayaran GAGAL
 *   3. retryBayar()    → User coba bayar ulang jika GAGAL (BR-10, UC-03)
 *   4. batalkan()      → Pendaftaran CANCELLED
 *                         Jika sudah CONFIRMED → kuota dikembalikan, status_refund = DIMINTA → SELESAI
 *                         Jika masih PENDING   → tidak ada refund (belum bayar)
 */
public class PendaftaranService {
    private final PendaftaranDAO pendaftaranDAO;
    private final SeminarDAO     seminarDAO;
    private final PembayaranDAO  pembayaranDAO;
    private final AuditLogDAO    auditLogDAO;
    private final PaymentService paymentService;

    public PendaftaranService(PendaftaranDAO p, SeminarDAO s, PembayaranDAO b,
                               AuditLogDAO a, PaymentService ps) {
        this.pendaftaranDAO = p;
        this.seminarDAO     = s;
        this.pembayaranDAO  = b;
        this.auditLogDAO    = a;
        this.paymentService = ps;
    }

    /**
     * STEP 1: Daftar seminar — buat transaksi PENDING, tiket siap, BELUM bayar.
     * Kuota BELUM dikurangi di sini.
     */
    public Pendaftaran daftar(int idPemesan, int idSeminar,
                               List<DetailPendaftaran> tiketList, String metodeBayar)
            throws JumlahTiketTidakValidException, InputKosongException,
                   DataTidakDitemukanException, KuotaPenuhException, SQLException {

        // Validasi jumlah tiket
        if (tiketList == null || tiketList.isEmpty()) throw new JumlahTiketTidakValidException();
        Validator.cekJumlahTiket(tiketList.size());
        Validator.cekTidakKosong(metodeBayar, "Metode Bayar");

        // Validasi data tiket
        for (int i = 0; i < tiketList.size(); i++) {
            DetailPendaftaran d = tiketList.get(i);
            Validator.cekTidakKosong(d.getNamaPeserta(),  "Nama peserta ke-" + (i + 1));
            Validator.cekTidakKosong(d.getEmailPeserta(), "Email peserta ke-" + (i + 1));
        }

        // Cek seminar ada dan DIBUKA
        Seminar seminar = seminarDAO.getById(idSeminar);
        if (seminar.getStatus() != StatusSeminar.DIBUKA)
            throw new DataTidakDitemukanException("Seminar dengan status DIBUKA", idSeminar);

        // Cek kuota
        if (seminar.getSisaKuota() < tiketList.size())
            throw new KuotaPenuhException(seminar.getSisaKuota());

        // Hitung total dan generate kode
        double total         = seminar.getHarga() * tiketList.size();
        String kodeTransaksi = KodeGenerator.generateKodeTransaksi();

        // Buat header pendaftaran + tiket
        Pendaftaran pendaftaran = new Pendaftaran(idPemesan, idSeminar, kodeTransaksi, total);
        for (DetailPendaftaran d : tiketList) {
            String kodeBooking = KodeGenerator.generateKodeBooking();
            String qrData      = KodeGenerator.generateQrData(kodeBooking, d.getEmailPeserta());
            pendaftaran.tambahDetail(new DetailPendaftaran(
                0, d.getNamaPeserta(), d.getEmailPeserta(),
                d.getNoTelepon(), kodeBooking, qrData
            ));
        }

        // Insert atomik (header + tiket), status PENDING
        int idPendaftaran = pendaftaranDAO.insertDenganDetail(pendaftaran);

        // Insert record pembayaran PENDING
        pembayaranDAO.insert(idPendaftaran, metodeBayar, total);

        // Jika gratis → langsung konfirmasi otomatis
        if (seminar.isGratis()) {
            pendaftaranDAO.updateStatus(idPendaftaran, StatusPendaftaran.CONFIRMED);
            pembayaranDAO.updateStatus(idPendaftaran, StatusPembayaran.BERHASIL);
            seminarDAO.tambahKuotaTerisi(idSeminar, tiketList.size());
            pendaftaran.setStatus(StatusPendaftaran.CONFIRMED);
            auditLogDAO.log(idPemesan, "DAFTAR_GRATIS", "pendaftaran",
                idPendaftaran, "Seminar gratis, otomatis CONFIRMED: " + kodeTransaksi);
        } else {
            auditLogDAO.log(idPemesan, "DAFTAR_SEMINAR", "pendaftaran",
                idPendaftaran, "Pendaftaran PENDING menunggu bayar: " + kodeTransaksi);
        }

        return pendaftaran;
    }

    /**
     * STEP 2: Konfirmasi pembayaran — user secara eksplisit menekan "Bayar Sekarang".
     * Hanya bisa dilakukan jika status pendaftaran masih PENDING.
     */
    public Pendaftaran konfirmasiBayar(String kodeTransaksi, String metodeBayar)
            throws DataTidakDitemukanException, AksesDitolakException,
                   PembayaranGagalException, InputKosongException, SQLException {

        Validator.cekTidakKosong(kodeTransaksi, "Kode Transaksi");
        Validator.cekTidakKosong(metodeBayar,   "Metode Bayar");

        Pendaftaran p = pendaftaranDAO.getByKodeTransaksi(kodeTransaksi.trim().toUpperCase());

        // Validasi status harus PENDING
        if (p.getStatus() == StatusPendaftaran.CONFIRMED)
            throw new AksesDitolakException("Transaksi " + kodeTransaksi + " sudah dibayar (CONFIRMED).");
        if (p.getStatus() == StatusPendaftaran.CANCELLED)
            throw new AksesDitolakException("Transaksi " + kodeTransaksi + " sudah dibatalkan.");

        // Update metode jika user ganti metode saat konfirmasi
        pembayaranDAO.updateMetode(p.getIdPendaftaran(), metodeBayar);

        // Proses pembayaran via PaymentService
        // Jika gagal → throw PembayaranGagalException (status GAGAL dicatat di DummyPaymentService)
        StatusPembayaran statusBayar = paymentService.prosesPembayaran(
            p.getIdPendaftaran(), p.getTotal(), metodeBayar
        );

        // Pembayaran BERHASIL → CONFIRMED + kurangi kuota
        if (statusBayar == StatusPembayaran.BERHASIL) {
            pendaftaranDAO.updateStatus(p.getIdPendaftaran(), StatusPendaftaran.CONFIRMED);
            int jumlahTiket = pendaftaranDAO.hitungDetail(p.getIdPendaftaran());
            seminarDAO.tambahKuotaTerisi(p.getIdSeminar(), jumlahTiket);
            p.setStatus(StatusPendaftaran.CONFIRMED);
            auditLogDAO.log(p.getIdPemesan(), "BAYAR_BERHASIL", "pembayaran",
                p.getIdPendaftaran(), "Pembayaran BERHASIL: " + kodeTransaksi);
        }

        return p;
    }

    /**
     * STEP 3: Retry bayar — coba lagi setelah pembayaran GAGAL (BR-10, UC-03).
     * Delegasikan ke konfirmasiBayar dengan metode yang (mungkin berbeda).
     */
    public Pendaftaran retryBayar(String kodeTransaksi, String metodeBayar)
            throws DataTidakDitemukanException, AksesDitolakException,
                   PembayaranGagalException, InputKosongException, SQLException {

        Validator.cekTidakKosong(kodeTransaksi, "Kode Transaksi");
        Pendaftaran p = pendaftaranDAO.getByKodeTransaksi(kodeTransaksi.trim().toUpperCase());

        // Cek pembayaran sebelumnya memang GAGAL
        Pembayaran bayar = pembayaranDAO.getByPendaftaran(p.getIdPendaftaran());
        if (bayar != null && bayar.getStatus() != StatusPembayaran.GAGAL
                && bayar.getStatus() != StatusPembayaran.PENDING)
            throw new AksesDitolakException("Pembayaran untuk transaksi ini sudah berstatus "
                + bayar.getStatus() + ". Tidak perlu retry.");

        // Reset status pembayaran ke PENDING sebelum coba ulang
        pembayaranDAO.updateStatus(p.getIdPendaftaran(), StatusPembayaran.PENDING);
        auditLogDAO.log(p.getIdPemesan(), "RETRY_BAYAR", "pembayaran",
            p.getIdPendaftaran(), "Retry pembayaran: " + kodeTransaksi);

        return konfirmasiBayar(kodeTransaksi, metodeBayar);
    }

    /**
     * STEP 4: Batalkan pendaftaran.
     * - Jika CONFIRMED: kuota dikembalikan + proses refund (status_refund: DIMINTA→SELESAI)
     * - Jika PENDING: batalkan saja, tidak ada refund (belum bayar)
     */
    public boolean batalkan(int idPendaftaran)
            throws DataTidakDitemukanException, AksesDitolakException, SQLException {

        Pendaftaran p = pendaftaranDAO.getById(idPendaftaran);

        if (p.getStatus() == StatusPendaftaran.CANCELLED)
            throw new AksesDitolakException("Pendaftaran #" + idPendaftaran + " sudah dibatalkan.");

        int jumlahTiket = pendaftaranDAO.hitungDetail(idPendaftaran);

        if (p.getStatus() == StatusPendaftaran.CONFIRMED) {
            // Kembalikan kuota
            seminarDAO.kurangiKuotaTerisi(p.getIdSeminar(), jumlahTiket);
            // Proses refund dummy: DIMINTA → DIPROSES → SELESAI
            pembayaranDAO.updateStatusRefund(idPendaftaran, StatusRefund.DIMINTA);
            StatusRefund statusRefund = paymentService.prosesRefund(idPendaftaran);
            auditLogDAO.log(p.getIdPemesan(), "BATAL_DAN_REFUND", "pendaftaran",
                idPendaftaran, "Batal CONFIRMED → refund " + statusRefund);
        } else {
            // Masih PENDING → tidak ada refund
            auditLogDAO.log(p.getIdPemesan(), "BATAL_PENDING", "pendaftaran",
                idPendaftaran, "Batal pendaftaran yang belum dibayar");
        }

        pendaftaranDAO.updateStatus(idPendaftaran, StatusPendaftaran.CANCELLED);
        return true;
    }

    // Getter methods
    public List<Object[]>          getRiwayat(int idPemesan)       throws SQLException { return pendaftaranDAO.getRiwayatPemesan(idPemesan); }
    public List<Object[]>          getPesertaSeminar(int idSeminar) throws SQLException { return pendaftaranDAO.getPesertaBySeminar(idSeminar); }
    public List<DetailPendaftaran> getDetailByKodeTransaksi(String kode)
            throws DataTidakDitemukanException, SQLException {
        Pendaftaran p = pendaftaranDAO.getByKodeTransaksi(kode.trim().toUpperCase());
        return pendaftaranDAO.getDetailByPendaftaran(p.getIdPendaftaran());
    }
    public DetailPendaftaran cariTiket(String kodeBooking)
            throws DataTidakDitemukanException, InputKosongException, SQLException {
        Validator.cekTidakKosong(kodeBooking, "Kode Booking");
        return pendaftaranDAO.getDetailByKodeBooking(kodeBooking.trim().toUpperCase());
    }
    public Pembayaran getStatusPembayaran(String kodeTransaksi)
            throws DataTidakDitemukanException, InputKosongException, SQLException {
        Validator.cekTidakKosong(kodeTransaksi, "Kode Transaksi");
        Pendaftaran p = pendaftaranDAO.getByKodeTransaksi(kodeTransaksi.trim().toUpperCase());
        return pembayaranDAO.getByPendaftaran(p.getIdPendaftaran());
    }
}
