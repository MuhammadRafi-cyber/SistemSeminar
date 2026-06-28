import controller.*;
import dao.*;
import enums.ModeSeminar;
import model.*;
import service.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Main — Entry point Eventix v5.
 *
 * DB v5 (10 tabel): institusi, kategori, user, seminar, pendaftaran,
 *   detail_pendaftaran, presensi, sertifikat, pembayaran, audit_log
 *
 * Alur pembayaran manual:
 *   Daftar → PENDING → Konfirmasi Bayar → CONFIRMED (atau GAGAL → Retry)
 *
 * Layer: View (Main) → Controller → Service → DAO → MySQL
 */
public class Main {

    // ======================== WIRING ========================
    static final UserDAO        userDAO        = new UserDAO();
    static final SeminarDAO     seminarDAO     = new SeminarDAO();
    static final PendaftaranDAO pendaftaranDAO = new PendaftaranDAO();
    static final PresensiDAO    presensiDAO    = new PresensiDAO();
    static final SertifikatDAO  sertifikatDAO  = new SertifikatDAO();
    static final PembayaranDAO  pembayaranDAO  = new PembayaranDAO();
    static final InstitusiDAO   institusiDAO   = new InstitusiDAO();
    static final KategoriDAO    kategoriDAO    = new KategoriDAO();
    static final AuditLogDAO    auditLogDAO    = new AuditLogDAO();
    static final LaporanDAO     laporanDAO     = new LaporanDAO();

    static final PaymentService paymentService =
        new DummyPaymentService(pembayaranDAO);

    static final AuthService        authService        = new AuthService(userDAO, auditLogDAO);
    static final SeminarService     seminarService     = new SeminarService(seminarDAO, auditLogDAO);
    static final PendaftaranService pendaftaranService = new PendaftaranService(
        pendaftaranDAO, seminarDAO, pembayaranDAO, auditLogDAO, paymentService);
    static final PresensiService    presensiService    = new PresensiService(
        presensiDAO, pendaftaranDAO, seminarDAO, auditLogDAO);
    static final SertifikatService  sertifikatService  = new SertifikatService(
        sertifikatDAO, pendaftaranDAO, presensiDAO, auditLogDAO);
    static final LaporanService     laporanService     = new LaporanService(laporanDAO);

    static final AuthController        authController        = new AuthController(authService);
    static final SeminarController     seminarController     = new SeminarController(seminarService);
    static final PendaftaranController pendaftaranController = new PendaftaranController(pendaftaranService);
    static final PresensiController    presensiController    = new PresensiController(presensiService);
    static final SertifikatController  sertifikatController  = new SertifikatController(sertifikatService);
    static final LaporanController     laporanController     = new LaporanController(laporanService);

    static final Scanner sc     = new Scanner(System.in);
    static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // ======================== MAIN ========================
    public static void main(String[] args) {
        printBanner();
        boolean running = true;
        while (running) {
            try {
                if (!AuthController.sudahLogin()) {
                    running = menuAwal();
                } else {
                    User user = AuthController.getUserAktif();
                    switch (user.getRole()) {
                        case PESERTA -> menuPeserta(user);
                        case PANITIA -> menuPanitia(user);
                        case ADMIN   -> menuAdmin(user);
                    }
                }
            } catch (exception.KoneksiDatabaseException e) {
                System.out.println("\n[FATAL] " + e.getMessage());
                running = false;
            } catch (Exception e) {
                System.out.println("[ERROR] " + e.getMessage());
            }
        }
        System.out.println("\n=== Terima kasih telah menggunakan Eventix v5! ===");
        util.Koneksi.closeConnection();
    }

    // ======================== MENU AWAL ========================
    static boolean menuAwal() {
        System.out.println("""
        \n╔══════════════════════════════╗
        ║     MENU UTAMA EVENTIX v5    ║
        ╠══════════════════════════════╣
        ║ 1. Login                     ║
        ║ 2. Registrasi Peserta        ║
        ║ 3. Lihat Seminar Tersedia    ║
        ║ 0. Keluar                    ║
        ╚══════════════════════════════╝""");
        System.out.print("Pilih: ");
        switch (sc.nextLine().trim()) {
            case "1" -> doLogin();
            case "2" -> doRegistrasi();
            case "3" -> lihatSeminarPublik();
            case "0" -> { return false; }
            default  -> System.out.println("[!] Pilihan tidak valid.");
        }
        return true;
    }

    // ======================== MENU PESERTA ========================
    static void menuPeserta(User user) {
        System.out.println("\n┌─ MENU PESERTA [" + user.getNama() + "] ──────────────────────────┐");
        System.out.println("│ 1.  Lihat Seminar Tersedia                              │");
        System.out.println("│ 2.  Daftar Seminar (buat transaksi PENDING)             │");
        System.out.println("│ 3.  Konfirmasi Bayar (transaksi PENDING → CONFIRMED)    │");
        System.out.println("│ 4.  Retry Bayar (jika pembayaran GAGAL)                 │");
        System.out.println("│ 5.  Cek Status Pembayaran                               │");
        System.out.println("│ 6.  Riwayat Pendaftaran Saya                            │");
        System.out.println("│ 7.  Lihat Detail Tiket (via kode transaksi)             │");
        System.out.println("│ 8.  Batalkan Pendaftaran                                │");
        System.out.println("│ 9.  Generate Sertifikat (kode booking)                  │");
        System.out.println("│ 10. Lihat Sertifikat Saya                               │");
        System.out.println("│ 11. Laporan Riwayat Seminar (F1)                        │");
        System.out.println("│ 12. Ekspor Laporan ke CSV                               │");
        System.out.println("│ 13. Edit Profil                                         │");
        System.out.println("│ 0.  Logout                                              │");
        System.out.println("└─────────────────────────────────────────────────────────┘");
        System.out.print("Pilih: ");
        switch (sc.nextLine().trim()) {
            case "1"  -> lihatSeminarPublik();
            case "2"  -> doDaftar(user);
            case "3"  -> doKonfirmasiBayar();
            case "4"  -> doRetryBayar();
            case "5"  -> doCekStatusBayar();
            case "6"  -> lihatRiwayat(user);
            case "7"  -> lihatDetailTiket();
            case "8"  -> doBatalkan();
            case "9"  -> doGenerateSertifikat();
            case "10" -> lihatSertifikat(user);
            case "11" -> System.out.println(laporanController.getLaporanPeserta(user));
            case "12" -> doEksporLaporanPeserta(user);
            case "13" -> doEditProfil(user);
            case "0"  -> printHasil(authController.logout());
            default   -> System.out.println("[!] Pilihan tidak valid.");
        }
    }

    // ======================== MENU PANITIA ========================
    static void menuPanitia(User user) {
        System.out.println("\n┌─ MENU PANITIA [" + user.getNama() + "] ─────────────────────────┐");
        System.out.println("│ 1.  Seminar Saya                                        │");
        System.out.println("│ 2.  Tambah Seminar Baru                                 │");
        System.out.println("│ 3.  Edit Seminar                                        │");
        System.out.println("│ 4.  Hapus/Batalkan Seminar                              │");
        System.out.println("│ 5.  Selesaikan Seminar                                  │");
        System.out.println("│ 6.  Lihat Daftar Peserta Seminar                        │");
        System.out.println("│ 7.  Catat Presensi (kode booking)                       │");
        System.out.println("│ 8.  Cek Status Presensi Tiket                           │");
        System.out.println("│ 9.  Laporan Seminar (F2)                                │");
        System.out.println("│ 10. Ekspor Laporan ke CSV                               │");
        System.out.println("│ 0.  Logout                                              │");
        System.out.println("└─────────────────────────────────────────────────────────┘");
        System.out.print("Pilih: ");
        switch (sc.nextLine().trim()) {
            case "1"  -> lihatSeminarSaya(user);
            case "2"  -> doTambahSeminar(user);
            case "3"  -> doEditSeminar(user);
            case "4"  -> doHapusSeminar(user);
            case "5"  -> doSelesaikanSeminar(user);
            case "6"  -> lihatPesertaSeminar();
            case "7"  -> doScanPresensi(user);
            case "8"  -> doCekPresensi();
            case "9"  -> System.out.println(laporanController.getLaporanPanitia(user));
            case "10" -> doEksporLaporanPanitia(user);
            case "0"  -> printHasil(authController.logout());
            default   -> System.out.println("[!] Pilihan tidak valid.");
        }
    }

    // ======================== MENU ADMIN ========================
    static void menuAdmin(User user) {
        System.out.println("""
        \n┌─ MENU ADMIN ──────────────────────────────────────────┐
        │ 1. Semua Seminar                                       │
        │ 2. Tambah Seminar                                      │
        │ 3. Hapus/Batalkan Seminar Manapun                      │
        │ 4. Selesaikan Seminar                                  │
        │ 5. Catat Presensi                                      │
        │ 6. Laporan Seminar (F2)                                │
        │ 7. Ekspor Laporan ke CSV                               │
        │ 8. Lihat Institusi                                     │
        │ 9. Lihat Kategori                                      │
        │ 0. Logout                                              │
        └────────────────────────────────────────────────────────┘""");
        System.out.print("Pilih: ");
        switch (sc.nextLine().trim()) {
            case "1" -> lihatSemuaSeminar();
            case "2" -> doTambahSeminar(user);
            case "3" -> doHapusSeminar(user);
            case "4" -> doSelesaikanSeminar(user);
            case "5" -> doScanPresensi(user);
            case "6" -> System.out.println(laporanController.getLaporanPanitia(user));
            case "7" -> doEksporLaporanPanitia(user);
            case "8" -> lihatInstitusi();
            case "9" -> lihatKategori();
            case "0" -> printHasil(authController.logout());
            default  -> System.out.println("[!] Pilihan tidak valid.");
        }
    }

    // ======================== AKSI AUTH ========================

    static void doLogin() {
        System.out.print("Email   : "); String email = sc.nextLine().trim();
        System.out.print("Password: "); String pass  = sc.nextLine();
        printHasil(authController.login(email, pass));
    }

    static void doRegistrasi() {
        System.out.println("--- Registrasi Peserta Baru ---");
        System.out.print("Nama           : "); String nama  = sc.nextLine();
        System.out.print("Username       : "); String uname = sc.nextLine().trim();
        System.out.print("Email          : "); String email = sc.nextLine().trim();
        System.out.print("Password       : "); String pass  = sc.nextLine();
        System.out.print("No. Telpon     : "); String noTlp = sc.nextLine();
        lihatInstitusi();
        System.out.print("ID Institusi (Enter=lewati): ");
        String instStr = sc.nextLine().trim();
        Integer idInst = instStr.isEmpty() ? null : parseIntSafe(instStr);
        printHasil(authController.registrasi(nama, uname.isEmpty() ? null : uname,
            email, pass, noTlp, idInst));
    }

    static void doEditProfil(User user) {
        System.out.println("--- Edit Profil ---");
        System.out.print("Nama baru         (Enter=skip): "); String nama  = sc.nextLine();
        System.out.print("Username baru     (Enter=skip): "); String uname = sc.nextLine();
        System.out.print("No. Telpon baru   (Enter=skip): "); String tlp   = sc.nextLine();
        System.out.print("ID Institusi baru (Enter=skip): "); String inst  = sc.nextLine().trim();
        if (nama.isEmpty())  nama  = user.getNama();
        if (uname.isEmpty()) uname = user.getUsername();
        if (tlp.isEmpty())   tlp   = user.getNoTelepon() != null ? user.getNoTelepon() : "";
        Integer idInst = inst.isEmpty() ? user.getIdInstitusi() : parseIntSafe(inst);
        printHasil(authController.updateProfil(nama, uname, tlp, idInst));
    }

    // ======================== AKSI SEMINAR ========================

    static void lihatSeminarPublik() {
        List<Seminar> list = seminarController.getSeminarDibuka();
        if (list.isEmpty()) { System.out.println("[i] Belum ada seminar yang dibuka."); return; }
        System.out.println("\n=== SEMINAR TERSEDIA ===");
        list.forEach(s -> System.out.printf(
            "  [%d] %-32s | %s | %-7s | Sisa: %d | %s%n",
            s.getIdSeminar(), s.getJudul(),
            s.getTanggalMulai() != null ? s.getTanggalMulai().toLocalDate() : "-",
            s.getMode(),
            s.getSisaKuota(),
            s.isGratis() ? "GRATIS" : "Rp" + String.format("%,.0f", s.getHarga())));
    }

    static void lihatSemuaSeminar() {
        List<Seminar> list = seminarController.getSemuaSeminar();
        System.out.println("\n=== SEMUA SEMINAR ===");
        if (list.isEmpty()) { System.out.println("[i] Tidak ada seminar."); return; }
        list.forEach(s -> System.out.printf(
            "  [%d] %-32s | %s | %-7s | %s | Terisi: %d/%d%n",
            s.getIdSeminar(), s.getJudul(),
            s.getTanggalMulai() != null ? s.getTanggalMulai().toLocalDate() : "-",
            s.getMode(), s.getStatus(), s.getKuotaTerisi(), s.getKuota()));
    }

    static void lihatSeminarSaya(User panitia) {
        List<Seminar> list = seminarController.getSeminarSaya(panitia.getIdUser());
        System.out.println("\n=== SEMINAR SAYA ===");
        if (list.isEmpty()) { System.out.println("[i] Belum ada seminar."); return; }
        list.forEach(s -> System.out.printf(
            "  [%d] %-32s | Terisi: %d/%d | %s | %s%n",
            s.getIdSeminar(), s.getJudul(),
            s.getKuotaTerisi(), s.getKuota(), s.getMode(), s.getStatus()));
    }

    static void lihatInstitusi() {
        try {
            List<Institusi> list = institusiDAO.getAll();
            System.out.println("\n=== DAFTAR INSTITUSI ===");
            if (list.isEmpty()) { System.out.println("[i] Belum ada institusi."); return; }
            list.forEach(i -> System.out.printf("  [%d] %s — %s%n",
                i.getIdInstitusi(), i.getNama(), i.getAlamat() != null ? i.getAlamat() : "-"));
        } catch (Exception e) { System.out.println("[ERROR] " + e.getMessage()); }
    }

    static void lihatKategori() {
        try {
            List<Kategori> list = kategoriDAO.getAll();
            System.out.println("\n=== DAFTAR KATEGORI ===");
            if (list.isEmpty()) { System.out.println("[i] Belum ada kategori."); return; }
            list.forEach(k -> System.out.printf("  [%d] %s%n", k.getIdKategori(), k.getNamaKategori()));
        } catch (Exception e) { System.out.println("[ERROR] " + e.getMessage()); }
    }

    static void doTambahSeminar(User panitia) {
        System.out.println("--- Tambah Seminar ---");
        lihatInstitusi();
        System.out.print("ID Institusi          : "); int idInstitusi = parseIntSafe(sc.nextLine());
        lihatKategori();
        System.out.print("ID Kategori (Enter=skip): "); String katStr = sc.nextLine().trim();
        Integer idKategori = katStr.isEmpty() ? null : parseIntSafe(katStr);
        System.out.print("Judul                 : "); String judul     = sc.nextLine();
        System.out.print("Deskripsi             : "); String desk      = sc.nextLine();
        System.out.print("Pembicara             : "); String pembicara = sc.nextLine();
        System.out.print("Mode (ONLINE/OFFLINE) : "); String modeStr   = sc.nextLine().trim().toUpperCase();
        System.out.print("Tanggal Mulai  (yyyy-MM-dd HH:mm): "); String mulaiStr  = sc.nextLine();
        System.out.print("Tanggal Selesai(yyyy-MM-dd HH:mm): "); String selesStr  = sc.nextLine();
        System.out.print("Lokasi                : "); String lokasi    = sc.nextLine();
        System.out.print("Kuota                 : "); int    kuota     = parseIntSafe(sc.nextLine());
        System.out.print("Harga (0=gratis)      : "); double harga     = parseDoubleSafe(sc.nextLine());
        try {
            LocalDateTime mulai  = LocalDateTime.parse(mulaiStr.trim(), FMT);
            LocalDateTime seles  = LocalDateTime.parse(selesStr.trim(), FMT);
            ModeSeminar   mode   = modeStr.equals("ONLINE") ? ModeSeminar.ONLINE : ModeSeminar.OFFLINE;
            printHasil(seminarController.tambahSeminar(panitia, idInstitusi, idKategori,
                judul, desk, pembicara, mulai, seles, mode, lokasi, kuota, harga));
        } catch (DateTimeParseException e) {
            System.out.println("[ERROR] Format tanggal salah. Gunakan: yyyy-MM-dd HH:mm (contoh: 2026-07-10 09:00)");
        }
    }

    static void doEditSeminar(User panitia) {
        lihatSeminarSaya(panitia);
        System.out.print("ID Seminar yang diedit: "); int id = parseIntSafe(sc.nextLine());
        Seminar s = seminarController.getDetail(id);
        if (s == null) { System.out.println("[ERROR] Seminar tidak ditemukan."); return; }
        System.out.println("Judul saat ini : " + s.getJudul());
        System.out.print("Judul baru     (Enter=skip): "); String judul     = sc.nextLine();
        System.out.print("Pembicara baru (Enter=skip): "); String pembicara = sc.nextLine();
        System.out.print("Lokasi baru    (Enter=skip): "); String lokasi    = sc.nextLine();
        System.out.print("Kuota baru     (Enter=skip): "); String kuotaStr  = sc.nextLine();
        System.out.print("Harga baru     (Enter=skip): "); String hargaStr  = sc.nextLine();
        if (!judul.isEmpty())     s.setJudul(judul);
        if (!pembicara.isEmpty()) s.setPembicara(pembicara);
        if (!lokasi.isEmpty())    s.setLokasi(lokasi);
        if (!kuotaStr.isEmpty())  s.setKuota(parseIntSafe(kuotaStr));
        if (!hargaStr.isEmpty())  s.setHarga(parseDoubleSafe(hargaStr));
        printHasil(seminarController.editSeminar(panitia, s));
    }

    static void doHapusSeminar(User panitia) {
        lihatSeminarSaya(panitia);
        System.out.print("ID Seminar yang dihapus: "); int id = parseIntSafe(sc.nextLine());
        System.out.print("Konfirmasi hapus seminar #" + id + "? (ya/tidak): ");
        if (sc.nextLine().trim().equalsIgnoreCase("ya"))
            printHasil(seminarController.hapusSeminar(panitia, id));
        else System.out.println("[i] Hapus dibatalkan.");
    }

    static void doSelesaikanSeminar(User panitia) {
        lihatSeminarSaya(panitia);
        System.out.print("ID Seminar yang diselesaikan: "); int id = parseIntSafe(sc.nextLine());
        printHasil(seminarController.selesaikanSeminar(panitia, id));
    }

    // ======================== AKSI PENDAFTARAN ========================

    static void doDaftar(User user) {
        lihatSeminarPublik();
        System.out.print("ID Seminar         : "); int idSeminar = parseIntSafe(sc.nextLine());
        Seminar s = seminarController.getDetail(idSeminar);
        if (s == null) { System.out.println("[ERROR] Seminar tidak ditemukan."); return; }
        System.out.printf("Harga per tiket    : %s%n",
            s.isGratis() ? "GRATIS" : "Rp" + String.format("%,.0f", s.getHarga()));
        System.out.print("Jumlah tiket (1-4) : "); int jumlah = parseIntSafe(sc.nextLine());
        List<DetailPendaftaran> tiketList = new ArrayList<>();
        for (int i = 1; i <= jumlah; i++) {
            System.out.println("  -- Peserta ke-" + i + " --");
            System.out.print("  Nama    : "); String nama  = sc.nextLine();
            System.out.print("  Email   : "); String email = sc.nextLine().trim();
            System.out.print("  No. Tlp : "); String tlp   = sc.nextLine();
            tiketList.add(new DetailPendaftaran(0, nama, email, tlp, "", ""));
        }
        System.out.println("Metode Bayar (QRIS / E-Wallet / Virtual Account): ");
        String metode = sc.nextLine();
        printHasil(pendaftaranController.daftar(user.getIdUser(), idSeminar, tiketList, metode));
    }

    static void doKonfirmasiBayar() {
        System.out.print("Kode Transaksi     : "); String kode   = sc.nextLine().trim();
        System.out.println("Metode Bayar (QRIS / E-Wallet / Virtual Account): ");
        String metode = sc.nextLine();
        printHasil(pendaftaranController.konfirmasiBayar(kode, metode));
    }

    static void doRetryBayar() {
        System.out.print("Kode Transaksi     : "); String kode   = sc.nextLine().trim();
        System.out.println("Metode Bayar baru (QRIS / E-Wallet / Virtual Account): ");
        String metode = sc.nextLine();
        printHasil(pendaftaranController.retryBayar(kode, metode));
    }

    static void doCekStatusBayar() {
        System.out.print("Kode Transaksi: "); String kode = sc.nextLine().trim();
        printHasil(pendaftaranController.cekStatusPembayaran(kode));
    }

    static void lihatRiwayat(User user) {
        List<Object[]> list = pendaftaranController.getRiwayat(user.getIdUser());
        System.out.println("\n=== RIWAYAT PENDAFTARAN ===");
        if (list.isEmpty()) { System.out.println("[i] Belum ada pendaftaran."); return; }
        System.out.printf("  %-4s %-22s %-30s %-12s %-10s %s%n",
            "ID","Kode Transaksi","Seminar","Tanggal","Status","Total");
        System.out.println("  " + "─".repeat(95));
        list.forEach(row -> {
            String tgl = row[3] != null ? row[3].toString().substring(0, 10) : "-";
            System.out.printf("  %-4s %-22s %-30s %-12s %-10s Rp%,.0f%n",
                row[0], row[1], row[2], tgl, row[4], row[5]);
        });
    }

    static void lihatDetailTiket() {
        System.out.print("Kode Transaksi: "); String kode = sc.nextLine().trim();
        List<DetailPendaftaran> list = pendaftaranController.getDetailTiket(kode);
        System.out.println("\n=== DETAIL TIKET TRANSAKSI " + kode.toUpperCase() + " ===");
        if (list.isEmpty()) { System.out.println("[i] Tidak ada tiket atau transaksi tidak ditemukan."); return; }
        System.out.printf("  %-6s %-22s %-30s %-20s%n", "ID","Nama Peserta","Email","Kode Booking");
        System.out.println("  " + "─".repeat(80));
        list.forEach(d -> System.out.printf("  %-6d %-22s %-30s %-20s%n",
            d.getIdDetail(), d.getNamaPeserta(), d.getEmailPeserta(), d.getKodeBooking()));
    }

    static void doBatalkan() {
        System.out.print("ID Pendaftaran yang dibatalkan: "); int id = parseIntSafe(sc.nextLine());
        System.out.print("Konfirmasi batalkan pendaftaran #" + id + "? (ya/tidak): ");
        if (sc.nextLine().trim().equalsIgnoreCase("ya"))
            printHasil(pendaftaranController.batalkan(id));
        else System.out.println("[i] Pembatalan dibatalkan.");
    }

    static void lihatPesertaSeminar() {
        System.out.print("ID Seminar: "); int id = parseIntSafe(sc.nextLine());
        List<Object[]> list = pendaftaranController.getPesertaSeminar(id);
        System.out.println("\n=== PESERTA SEMINAR #" + id + " ===");
        if (list.isEmpty()) { System.out.println("[i] Belum ada peserta."); return; }
        System.out.printf("  %-4s %-22s %-28s %-18s %-10s Rp%n",
            "ID","Nama","Email","Kode Transaksi","Status");
        System.out.println("  " + "─".repeat(90));
        list.forEach(row -> System.out.printf("  %-4s %-22s %-28s %-18s %-10s %,.0f%n",
            row[0], row[1], row[2], row[3], row[5], row[6]));
    }

    // ======================== AKSI PRESENSI ========================

    static void doScanPresensi(User panitia) {
        System.out.print("Kode Booking tiket: "); String kode = sc.nextLine().trim();
        printHasil(presensiController.scanPresensi(panitia, kode));
    }

    static void doCekPresensi() {
        System.out.print("Kode Booking tiket: "); String kode = sc.nextLine().trim();
        printHasil(presensiController.cekStatus(kode));
    }

    // ======================== AKSI SERTIFIKAT ========================

    static void doGenerateSertifikat() {
        System.out.print("Kode Booking tiket: "); String kode = sc.nextLine().trim();
        printHasil(sertifikatController.generate(kode));
    }

    static void lihatSertifikat(User user) {
        List<Object[]> list = sertifikatController.getDaftarSertifikat(user.getIdUser());
        System.out.println("\n=== SERTIFIKAT SAYA ===");
        if (list.isEmpty()) { System.out.println("[i] Belum ada sertifikat."); return; }
        System.out.printf("  %-25s %-4s %-30s %-20s %s%n",
            "Nomor Sertifikat","Ver","Seminar","a.n.","Terbit");
        System.out.println("  " + "─".repeat(85));
        list.forEach(row -> System.out.printf("  %-25s %-4d %-30s %-20s %s%n",
            row[0], row[2], row[4], row[5], row[1]));
    }

    // ======================== AKSI LAPORAN ========================

    static void doEksporLaporanPeserta(User user) {
        System.out.print("Path file CSV (Enter=auto): "); String path = sc.nextLine().trim();
        printHasil(laporanController.eksporPesertaCsv(user, path.isEmpty() ? null : path));
    }

    static void doEksporLaporanPanitia(User user) {
        System.out.print("Path file CSV (Enter=auto): "); String path = sc.nextLine().trim();
        printHasil(laporanController.eksporPanitia(user, path.isEmpty() ? null : path));
    }

    // ======================== HELPERS ========================

    static void printHasil(String hasil) {
        if (hasil == null) return;
        if (hasil.startsWith("SUKSES|"))     System.out.println("[✓] " + hasil.substring(7));
        else if (hasil.startsWith("ERROR|")) System.out.println("[✗] " + hasil.substring(6));
        else                                 System.out.println("[i] " + hasil.replace("INFO|", ""));
    }

    static int parseIntSafe(String s) {
        try { return Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) {
            System.out.println("[!] Input harus angka, menggunakan 0."); return 0;
        }
    }

    static double parseDoubleSafe(String s) {
        try { return Double.parseDouble(s.trim()); }
        catch (NumberFormatException e) {
            System.out.println("[!] Input harus angka, menggunakan 0."); return 0.0;
        }
    }

    static void printBanner() {
        System.out.println("""
        ╔═══════════════════════════════════════════════════════════╗
        ║   ███████╗██╗   ██╗███████╗███╗   ██╗████████╗           ║
        ║   ██╔════╝██║   ██║██╔════╝████╗  ██║╚══██╔══╝           ║
        ║   █████╗  ██║   ██║█████╗  ██╔██╗ ██║   ██║              ║
        ║   ██╔══╝  ╚██╗ ██╔╝██╔══╝  ██║╚██╗██║   ██║              ║
        ║   ███████╗ ╚████╔╝ ███████╗██║ ╚████║   ██║              ║
        ║   ╚══════╝  ╚═══╝  ╚══════╝╚═╝  ╚═══╝   ╚═╝              ║
        ║       Sistem Informasi Pengelolaan Seminar  v5.0          ║
        ╚═══════════════════════════════════════════════════════════╝
        """);
    }
}
