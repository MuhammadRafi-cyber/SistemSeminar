package service;

import dao.AuditLogDAO;
import dao.PembayaranDAO;
import dao.PendaftaranDAO;
import dao.SeminarDAO;
import enums.StatusPembayaran;
import enums.StatusPendaftaran;
import exception.*;
import model.DetailPendaftaran;
import model.Pendaftaran;
import model.Seminar;
import util.KodeGenerator;
import util.Validator;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * PendaftaranService — business logic pendaftaran seminar.
 * DB v4: id_pemesan, kode_transaksi, total, detail_pendaftaran (1-4 tiket per transaksi).
 */
public class PendaftaranService {
    private final PendaftaranDAO pendaftaranDAO;
    private final SeminarDAO     seminarDAO;
    private final PembayaranDAO  pembayaranDAO;
    private final AuditLogDAO    auditLogDAO;

    public PendaftaranService(PendaftaranDAO p, SeminarDAO s, PembayaranDAO b, AuditLogDAO a) {
        this.pendaftaranDAO = p;
        this.seminarDAO     = s;
        this.pembayaranDAO  = b;
        this.auditLogDAO    = a;
    }

    /**
     * Daftar seminar dengan 1–4 tiket.
     * Alur:
     *   1. Validasi jumlah tiket
     *   2. Cek seminar ada dan status DIBUKA
     *   3. Cek sisa kuota mencukupi
     *   4. Generate kode transaksi + kode booking per tiket
     *   5. Insert header + tiket atomik (rollback otomatis jika gagal)
     *   6. Update kuota_terisi
     *   7. Insert pembayaran PENDING
     *   8. Simulasi proses pembayaran
     *
     * @param tiketList   daftar peserta (nama, email, no_telepon per tiket)
     * @param metodeBayar metode pembayaran (Transfer Bank / E-Wallet / QRIS)
     * @return Pendaftaran yang sudah diproses
     */
    public Pendaftaran daftar(int idPemesan, int idSeminar,
                               List<DetailPendaftaran> tiketList, String metodeBayar)
            throws JumlahTiketTidakValidException, InputKosongException,
                   DataTidakDitemukanException, KuotaPenuhException, SQLException {

        // 1. Validasi jumlah tiket
        if (tiketList == null || tiketList.isEmpty())
            throw new JumlahTiketTidakValidException();
        Validator.cekJumlahTiket(tiketList.size());
        Validator.cekTidakKosong(metodeBayar, "Metode Bayar");

        // 2. Validasi tiket — setiap peserta harus punya nama dan email
        for (int i = 0; i < tiketList.size(); i++) {
            DetailPendaftaran d = tiketList.get(i);
            Validator.cekTidakKosong(d.getNamaPeserta(),  "Nama peserta ke-" + (i + 1));
            Validator.cekTidakKosong(d.getEmailPeserta(), "Email peserta ke-" + (i + 1));
        }

        // 3. Cek seminar ada dan masih DIBUKA
        Seminar seminar = seminarDAO.getById(idSeminar);  // throw DataTidakDitemukanException jika tidak ada
        if (seminar.getStatus() != enums.StatusSeminar.DIBUKA)
            throw new DataTidakDitemukanException("Seminar DIBUKA", idSeminar);

        // 4. Cek sisa kuota
        if (seminar.getSisaKuota() < tiketList.size())
            throw new KuotaPenuhException(seminar.getSisaKuota());

        // 5. Hitung total dan generate kode
        double total         = seminar.getHarga() * tiketList.size();
        String kodeTransaksi = KodeGenerator.generateKodeTransaksi();

        // 6. Buat header pendaftaran dan tiket dengan kode booking unik
        Pendaftaran pendaftaran = new Pendaftaran(idPemesan, idSeminar, kodeTransaksi, total);
        for (DetailPendaftaran d : tiketList) {
            String kodeBooking = KodeGenerator.generateKodeBooking();
            String qrData      = KodeGenerator.generateQrData(kodeBooking, d.getEmailPeserta());
            pendaftaran.tambahDetail(new DetailPendaftaran(
                0, d.getNamaPeserta(), d.getEmailPeserta(),
                d.getNoTelepon(), kodeBooking, qrData
            ));
        }

        // 7. Insert atomik (header + semua tiket, rollback jika gagal)
        int idPendaftaran = pendaftaranDAO.insertDenganDetail(pendaftaran);

        // 8. Update kuota_terisi
        seminarDAO.tambahKuotaTerisi(idSeminar, tiketList.size());

        // 9. Insert pembayaran PENDING
        pembayaranDAO.insert(idPendaftaran, metodeBayar, total);

        // 10. Simulasi pembayaran — gratis langsung CONFIRMED, berbayar 95% berhasil
        StatusPembayaran statusBayar;
        if (total <= 0) {
            statusBayar = StatusPembayaran.BERHASIL;
        } else {
            statusBayar = Math.random() < 0.95
                ? StatusPembayaran.BERHASIL
                : StatusPembayaran.GAGAL;
        }

        if (statusBayar == StatusPembayaran.BERHASIL) {
            pembayaranDAO.updateStatus(idPendaftaran, StatusPembayaran.BERHASIL);
            pendaftaranDAO.updateStatus(idPendaftaran, StatusPendaftaran.CONFIRMED);
            pendaftaran.setStatus(StatusPendaftaran.CONFIRMED);
        } else {
            pembayaranDAO.updateStatus(idPendaftaran, StatusPembayaran.GAGAL);
            // Kembalikan kuota jika pembayaran gagal
            seminarDAO.kurangiKuotaTerisi(idSeminar, tiketList.size());
            pendaftaran.setStatus(StatusPendaftaran.PENDING);
        }

        auditLogDAO.log(idPemesan, "DAFTAR_SEMINAR", "pendaftaran");
        return pendaftaran;
    }

    /**
     * Batalkan pendaftaran.
     * Kuota dikembalikan, pembayaran di-refund (simulasi: ubah status jadi GAGAL).
     */
    public boolean batalkan(int idPendaftaran)
            throws DataTidakDitemukanException, AksesDitolakException, SQLException {

        Pendaftaran p = pendaftaranDAO.getById(idPendaftaran);

        if (p.getStatus() == StatusPendaftaran.CANCELLED)
            throw new AksesDitolakException("Pendaftaran #" + idPendaftaran + " sudah dibatalkan sebelumnya.");

        int jumlahTiket = pendaftaranDAO.hitungDetail(idPendaftaran);

        // Update status pendaftaran
        pendaftaranDAO.updateStatus(idPendaftaran, StatusPendaftaran.CANCELLED);

        // Kembalikan kuota
        seminarDAO.kurangiKuotaTerisi(p.getIdSeminar(), jumlahTiket);

        // Simulasi refund — set pembayaran GAGAL
        pembayaranDAO.updateStatus(idPendaftaran, StatusPembayaran.GAGAL);

        auditLogDAO.log(p.getIdPemesan(), "BATAL_PENDAFTARAN", "pendaftaran");
        return true;
    }

    /** Riwayat pendaftaran pemesan (C5 / F1 join). */
    public List<Object[]> getRiwayat(int idPemesan) throws SQLException {
        return pendaftaranDAO.getRiwayatPemesan(idPemesan);
    }

    /** Daftar peserta seminar (C4, untuk Panitia). */
    public List<Object[]> getPesertaSeminar(int idSeminar) throws SQLException {
        return pendaftaranDAO.getPesertaBySeminar(idSeminar);
    }

    /** Detail tiket dalam satu transaksi. */
    public List<DetailPendaftaran> getDetailTiket(int idPendaftaran) throws SQLException {
        return pendaftaranDAO.getDetailByPendaftaran(idPendaftaran);
    }

    /** Cari tiket via kode_booking. */
    public DetailPendaftaran cariTiket(String kodeBooking)
            throws DataTidakDitemukanException, InputKosongException, SQLException {
        Validator.cekTidakKosong(kodeBooking, "Kode Booking");
        return pendaftaranDAO.getDetailByKodeBooking(kodeBooking.trim().toUpperCase());
    }
}
