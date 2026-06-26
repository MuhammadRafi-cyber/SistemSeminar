import controller.*;
import dao.*;
import model.*;
import service.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Main — Entry point Eventix v4
 *
 * DB v4 (9 tabel): institusi, user, seminar, pendaftaran,
 *   detail_pendaftaran, presensi, sertifikat, pembayaran, audit_log
 *
 * Layer: View (Main) → Controller → Service → DAO → MySQL
 */
public class Main {

    // ===================== WIRING =====================
    static final UserDAO        userDAO        = new UserDAO();
    static final SeminarDAO     seminarDAO     = new SeminarDAO();
    static final PendaftaranDAO pendaftaranDAO = new PendaftaranDAO();
    static final PresensiDAO    presensiDAO    = new PresensiDAO();
    static final SertifikatDAO  sertifikatDAO  = new SertifikatDAO();
    static final PembayaranDAO  pembayaranDAO  = new PembayaranDAO();
    static final InstitusiDAO   institusiDAO   = new InstitusiDAO();
    static final AuditLogDAO    auditLogDAO    = new AuditLogDAO();
    static final LaporanDAO     laporanDAO     = new LaporanDAO();

    static final AuthService        authService        = new AuthService(userDAO, auditLogDAO);
    static final SeminarService     seminarService     = new SeminarService(seminarDAO, auditLogDAO);
    static final PendaftaranService pendaftaranService = new PendaftaranService(pendaftaranDAO, seminarDAO, pembayaranDAO, auditLogDAO);
    static final PresensiService    presensiService    = new PresensiService(presensiDAO, pendaftaranDAO, auditLogDAO);
    static final SertifikatService  sertifikatService  = new SertifikatService(sertifikatDAO, pendaftaranDAO, presensiDAO, auditLogDAO);
    static final LaporanService     laporanService     = new LaporanService(laporanDAO);

    static final AuthController        authController        = new AuthController(authService);
    static final SeminarController     seminarController     = new SeminarController(seminarService);
    static final PendaftaranController pendaftaranController = new PendaftaranController(pendaftaranService);
    static final PresensiController    presensiController    = new PresensiController(presensiService);
    static final SertifikatController  sertifikatController  = new SertifikatController(sertifikatService);
    static final LaporanController     laporanController     = new LaporanController(laporanService);

    static final Scanner sc = new Scanner(System.in);
    static final DateTimeFormatter FMT_DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // ===================== MAIN =====================
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
                System.out.println("Aplikasi tidak dapat terhubung ke database. Periksa XAMPP MySQL dan konfigurasi Koneksi.java.");
                running = false;
            } catch (Exception e) {
                System.out.println("[ERROR] Terjadi kesalahan tidak terduga: " + e.getMessage());
            }
        }
        System.out.println("\n=== Terima kasih telah menggunakan Eventix v4! ===");
        util.Koneksi.closeConnection();
    }

    // ===================== MENU AWAL =====================
    static boolean menuAwal() {
        System.out.println("""
        \n╔══════════════════════════════╗
        ║     MENU UTAMA EVENTIX v4    ║
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

    // ===================== MENU PESERTA =====================
    static void menuPeserta(User user) {
        System.out.println("\n┌─ MENU PESERTA [" + user.getNama() + "] ─────────────────────┐");
        System.out.println("│ 1. Lihat Seminar Tersedia                            │");
        System.out.println("│ 2. Daftar Seminar (beli tiket)                       │");
        System.out.println("│ 3. Riwayat Pendaftaran Saya                          │");
        System.out.println("│ 4. Lihat Detail Tiket                                │");
        System.out.println("│ 5. Batalkan Pendaftaran                              │");
        System.out.println("│ 6. Generate Sertifikat (kode booking)                │");
        System.out.println("│ 7. Lihat Sertifikat Saya                             │");
        System.out.println("│ 8. Laporan Riwayat Seminar (F1)                      │");
        System.out.println("│ 9. Edit Profil                                       │");
        System.out.println("│ 0. Logout                                            │");
        System.out.println("└──────────────────────────────────────────────────────┘");
        System.out.print("Pilih: ");
        switch (sc.nextLine().trim()) {
            case "1" -> lihatSeminarPublik();
            case "2" -> doDaftar(user);
            case "3" -> lihatRiwayat(user);
            case "4" -> lihatDetailTiket();
            case "5" -> doBatalkan();
            case "6" -> doGenerateSertifikat();
            case "7" -> lihatSertifikat(user);
            case "8" -> System.out.println(laporanController.getLaporanPeserta(user));
            case "9" -> doEditProfil(user);
            case "0" -> printHasil(authController.logout());
            default  -> System.out.println("[!] Pilihan tidak valid.");
        }
    }

    // ===================== MENU PANITIA =====================
    static void menuPanitia(User user) {
        System.out.println("\n┌─ MENU PANITIA [" + user.getNama() + "] ────────────────────┐");
        System.out.println("│ 1. Seminar Saya                                      │");
        System.out.println("│ 2. Tambah Seminar Baru                               │");
        System.out.println("│ 3. Edit Seminar                                      │");
        System.out.println("│ 4. Hapus Seminar                                     │");
        System.out.println("│ 5. Selesaikan Seminar                                │");
        System.out.println("│ 6. Lihat Daftar Peserta Seminar                      │");
        System.out.println("│ 7. Catat Presensi (kode booking)                     │");
        System.out.println("│ 8. Cek Status Presensi                               │");
        System.out.println("│ 9. Laporan Seminar (F2)                              │");
        System.out.println("│ 0. Logout                                            │");
        System.out.println("└──────────────────────────────────────────────────────┘");
        System.out.print("Pilih: ");
        switch (sc.nextLine().trim()) {
            case "1" -> lihatSeminarSaya(user);
            case "2" -> doTambahSeminar(user);
            case "3" -> doEditSeminar(user);
            case "4" -> doHapusSeminar(user);
            case "5" -> doSelesaikanSeminar(user);
            case "6" -> lihatPesertaSeminar();
            case "7" -> doScanPresensi(user);
            case "8" -> doCekPresensi();
            case "9" -> System.out.println(laporanController.getLaporanPanitia(user));
            case "0" -> printHasil(authController.logout());
            default  -> System.out.println("[!] Pilihan tidak valid.");
        }
    }

    // ===================== MENU ADMIN =====================
    static void menuAdmin(User user) {
        System.out.println("""
        \n┌─ MENU ADMIN ────────────────────────────────────────┐
        │ 1. Semua Seminar                                     │
        │ 2. Tambah Seminar                                    │
        │ 3. Hapus Seminar Manapun                             │
        │ 4. Selesaikan Seminar                                │
        │ 5. Catat Presensi                                    │
        │ 6. Laporan Seminar (F2)                              │
        │ 7. Lihat Daftar Institusi                            │
        │ 0. Logout                                            │
        └──────────────────────────────────────────────────────┘""");
        System.out.print("Pilih: ");
        switch (sc.nextLine().trim()) {
            case "1" -> lihatSemuaSeminar();
            case "2" -> doTambahSeminar(user);
            case "3" -> doHapusSeminar(user);
            case "4" -> doSelesaikanSeminar(user);
            case "5" -> doScanPresensi(user);
            case "6" -> System.out.println(laporanController.getLaporanPanitia(user));
            case "7" -> lihatInstitusi();
            case "0" -> printHasil(authController.logout());
            default  -> System.out.println("[!] Pilihan tidak valid.");
        }
    }

    // ===================== AKSI AUTH =====================

    static void doLogin() {
        System.out.print("Email   : "); String email = sc.nextLine().trim();
        System.out.print("Password: "); String pass  = sc.nextLine();
        printHasil(authController.login(email, pass));
    }

    static void doRegistrasi() {
        System.out.println("--- Registrasi Peserta Baru ---");
        System.out.print("Nama       : "); String nama  = sc.nextLine();
        System.out.print("Email      : "); String email = sc.nextLine().trim();
        System.out.print("Password   : "); String pass  = sc.nextLine();
        System.out.print("No. Telpon : "); String noTlp = sc.nextLine();
        lihatInstitusi();
        System.out.print("ID Institusi (Enter=lewati): ");
        String instStr = sc.nextLine().trim();
        Integer idInst = instStr.isEmpty() ? null : parseIntSafe(instStr);
        printHasil(authController.registrasi(nama, email, pass, noTlp, idInst));
    }

    static void doEditProfil(User user) {
        System.out.println("--- Edit Profil ---");
        System.out.print("Nama baru         (Enter=skip): "); String nama = sc.nextLine();
        System.out.print("No. Telpon baru   (Enter=skip): "); String tlp  = sc.nextLine();
        System.out.print("ID Institusi baru (Enter=skip): "); String inst = sc.nextLine().trim();
        if (nama.isEmpty()) nama = user.getNama();
        if (tlp.isEmpty())  tlp  = user.getNoTelepon() != null ? user.getNoTelepon() : "";
        Integer idInst = inst.isEmpty() ? user.getIdInstitusi() : parseIntSafe(inst);
        printHasil(authController.updateProfil(nama, tlp, idInst));
    }

    // ===================== AKSI SEMINAR =====================

    static void lihatSeminarPublik() {
        List<Seminar> list = seminarController.getSeminarDibuka();
        if (list.isEmpty()) { System.out.println("[i] Belum ada seminar yang dibuka."); return; }
        System.out.println("\n=== SEMINAR TERSEDIA ===");
        list.forEach(s -> System.out.printf(
            "  [%d] %-35s | %s | Sisa: %d | Rp%,.0f%n",
            s.getIdSeminar(), s.getJudul(),
            s.getTanggalMulai() != null ? s.getTanggalMulai().toLocalDate() : "-",
            s.getSisaKuota(), s.getHarga()));
    }

    static void lihatSemuaSeminar() {
        List<Seminar> list = seminarController.getSemuaSeminar();
        System.out.println("\n=== SEMUA SEMINAR ===");
        if (list.isEmpty()) { System.out.println("[i] Tidak ada seminar."); return; }
        list.forEach(s -> System.out.printf(
            "  [%d] %-35s | %s | %s | Terisi: %d/%d%n",
            s.getIdSeminar(), s.getJudul(),
            s.getTanggalMulai() != null ? s.getTanggalMulai().toLocalDate() : "-",
            s.getStatus(), s.getKuotaTerisi(), s.getKuota()));
    }

    static void lihatSeminarSaya(User panitia) {
        List<Seminar> list = seminarController.getSeminarSaya(panitia.getIdUser());
        System.out.println("\n=== SEMINAR SAYA ===");
        if (list.isEmpty()) { System.out.println("[i] Belum ada seminar."); return; }
        list.forEach(s -> System.out.printf(
            "  [%d] %-35s | Terisi: %d/%d | %s%n",
            s.getIdSeminar(), s.getJudul(),
            s.getKuotaTerisi(), s.getKuota(), s.getStatus()));
    }

    static void lihatInstitusi() {
        try {
            List<Institusi> list = institusiDAO.getAll();
            System.out.println("\n=== DAFTAR INSTITUSI ===");
            if (list.isEmpty()) { System.out.println("[i] Belum ada institusi."); return; }
            list.forEach(i -> System.out.printf("  [%d] %s — %s%n",
                i.getIdInstitusi(), i.getNama(), i.getAlamat() != null ? i.getAlamat() : "-"));
        } catch (Exception e) {
            System.out.println("[ERROR] Gagal load institusi: " + e.getMessage());
        }
    }

    static void doTambahSeminar(User panitia) {
        System.out.println("--- Tambah Seminar ---");
        lihatInstitusi();
        System.out.print("ID Institusi          : ");
        int idInstitusi = parseIntSafe(sc.nextLine());
        System.out.print("Judul                 : "); String judul = sc.nextLine();
        System.out.print("Deskripsi             : "); String desk  = sc.nextLine();
        System.out.print("Tanggal Mulai (yyyy-MM-dd HH:mm)   : "); String mulaiStr = sc.nextLine();
        System.out.print("Tanggal Selesai (yyyy-MM-dd HH:mm) : "); String selesStr = sc.nextLine();
        System.out.print("Lokasi                : "); String lokasi = sc.nextLine();
        System.out.print("Kuota                 : "); int kuota = parseIntSafe(sc.nextLine());
        System.out.print("Harga (0=gratis)      : "); double harga = parseDoubleSafe(sc.nextLine());
        try {
            LocalDateTime mulai = LocalDateTime.parse(mulaiStr, FMT_DT);
            LocalDateTime seles = selesStr.isEmpty() ? null : LocalDateTime.parse(selesStr, FMT_DT);
            printHasil(seminarController.tambahSeminar(panitia, idInstitusi, judul, desk,
                mulai, seles, lokasi, kuota, harga));
        } catch (DateTimeParseException e) {
            System.out.println("[ERROR] Format tanggal salah. Gunakan: yyyy-MM-dd HH:mm (contoh: 2026-07-10 09:00)");
        }
    }

    static void doEditSeminar(User panitia) {
        lihatSeminarSaya(panitia);
        System.out.print("ID Seminar yang diedit: ");
        int id = parseIntSafe(sc.nextLine());
        Seminar s = seminarController.getDetail(id);
        if (s == null) { System.out.println("[ERROR] Seminar tidak ditemukan."); return; }

        System.out.println("Judul saat ini : " + s.getJudul());
        System.out.print("Judul baru  (Enter=skip): "); String judul    = sc.nextLine();
        System.out.print("Lokasi baru (Enter=skip): "); String lokasi   = sc.nextLine();
        System.out.print("Kuota baru  (Enter=skip): "); String kuotaStr = sc.nextLine();
        System.out.print("Harga baru  (Enter=skip): "); String hargaStr = sc.nextLine();

        if (!judul.isEmpty())    s.setJudul(judul);
        if (!lokasi.isEmpty())   s.setLokasi(lokasi);
        if (!kuotaStr.isEmpty()) s.setKuota(parseIntSafe(kuotaStr));
        if (!hargaStr.isEmpty()) s.setHarga(parseDoubleSafe(hargaStr));

        printHasil(seminarController.editSeminar(panitia, s));
    }

    static void doHapusSeminar(User panitia) {
        lihatSeminarSaya(panitia);
        System.out.print("ID Seminar yang dihapus: ");
        int id = parseIntSafe(sc.nextLine());
        System.out.print("Konfirmasi hapus seminar #" + id + "? (ya/tidak): ");
        if (sc.nextLine().trim().equalsIgnoreCase("ya")) {
            printHasil(seminarController.hapusSeminar(panitia, id));
        } else {
            System.out.println("[i] Hapus dibatalkan.");
        }
    }

    static void doSelesaikanSeminar(User panitia) {
        lihatSeminarSaya(panitia);
        System.out.print("ID Seminar yang diselesaikan: ");
        int id = parseIntSafe(sc.nextLine());
        printHasil(seminarController.selesaikanSeminar(panitia, id));
    }

    // ===================== AKSI PENDAFTARAN =====================

    static void doDaftar(User user) {
        lihatSeminarPublik();
        System.out.print("ID Seminar       : ");
        int idSeminar = parseIntSafe(sc.nextLine());

        Seminar s = seminarController.getDetail(idSeminar);
        if (s == null) { System.out.println("[ERROR] Seminar tidak ditemukan."); return; }

        System.out.printf("Harga per tiket  : Rp%,.0f%n", s.getHarga());
        System.out.print("Jumlah tiket (1-4): ");
        int jumlah = parseIntSafe(sc.nextLine());

        List<DetailPendaftaran> tiketList = new ArrayList<>();
        for (int i = 1; i <= jumlah; i++) {
            System.out.println("  -- Peserta ke-" + i + " --");
            System.out.print("  Nama    : "); String nama  = sc.nextLine();
            System.out.print("  Email   : "); String email = sc.nextLine().trim();
            System.out.print("  No. Tlp : "); String tlp   = sc.nextLine();
            tiketList.add(new DetailPendaftaran(0, nama, email, tlp, "", ""));
        }
        System.out.println("Metode Bayar (Transfer Bank / E-Wallet / QRIS): ");
        String metode = sc.nextLine();

        printHasil(pendaftaranController.daftar(user.getIdUser(), idSeminar, tiketList, metode));
    }

    static void lihatRiwayat(User user) {
        // [{id, kodeTransaksi, judul, tanggalMulai, status, total}]
        List<Object[]> list = pendaftaranController.getRiwayat(user.getIdUser());
        System.out.println("\n=== RIWAYAT PENDAFTARAN ===");
        if (list.isEmpty()) { System.out.println("[i] Belum ada pendaftaran."); return; }
        list.forEach(row -> System.out.printf("  ID:%-4s | %-15s | %-30s | %-10s | Rp%,.0f%n",
            row[0], row[1], row[2], row[4], row[5]));
    }

    static void lihatDetailTiket() {
        System.out.print("ID Pendaftaran: ");
        int id = parseIntSafe(sc.nextLine());
        List<DetailPendaftaran> list = pendaftaranController.getDetailTiket(id);
        System.out.println("\n=== DETAIL TIKET PENDAFTARAN #" + id + " ===");
        if (list.isEmpty()) { System.out.println("[i] Tidak ada tiket."); return; }
        list.forEach(d -> System.out.printf("  [%d] %-20s | %-30s | Kode: %s%n",
            d.getIdDetail(), d.getNamaPeserta(), d.getEmailPeserta(), d.getKodeBooking()));
    }

    static void doBatalkan() {
        System.out.print("ID Pendaftaran yang dibatalkan: ");
        int id = parseIntSafe(sc.nextLine());
        System.out.print("Konfirmasi batalkan pendaftaran #" + id + "? (ya/tidak): ");
        if (sc.nextLine().trim().equalsIgnoreCase("ya")) {
            printHasil(pendaftaranController.batalkan(id));
        } else {
            System.out.println("[i] Pembatalan dibatalkan.");
        }
    }

    static void lihatPesertaSeminar() {
        System.out.print("ID Seminar: ");
        int id = parseIntSafe(sc.nextLine());
        List<Object[]> list = pendaftaranController.getPesertaSeminar(id);
        System.out.println("\n=== PESERTA SEMINAR #" + id + " ===");
        if (list.isEmpty()) { System.out.println("[i] Belum ada peserta."); return; }
        System.out.printf("  %-4s %-20s %-30s %-16s %-12s Rp%n", "ID", "Nama", "Email", "Kode Transaksi", "Status");
        System.out.println("  " + "─".repeat(92));
        list.forEach(row -> System.out.printf("  %-4s %-20s %-30s %-16s %-12s %,.0f%n",
            row[0], row[1], row[2], row[3], row[5], row[6]));
    }

    // ===================== AKSI PRESENSI =====================

    static void doScanPresensi(User panitia) {
        System.out.print("Kode Booking tiket: ");
        String kode = sc.nextLine().trim();
        printHasil(presensiController.scanPresensi(panitia, kode));
    }

    static void doCekPresensi() {
        System.out.print("Kode Booking tiket: ");
        String kode = sc.nextLine().trim();
        printHasil(presensiController.cekStatus(kode));
    }

    // ===================== AKSI SERTIFIKAT =====================

    static void doGenerateSertifikat() {
        System.out.print("Kode Booking tiket: ");
        String kode = sc.nextLine().trim();
        printHasil(sertifikatController.generate(kode));
    }

    static void lihatSertifikat(User user) {
        // [{nomor, tanggalTerbit, versi, filePath, judulSeminar, namaPeserta}]
        List<Object[]> list = sertifikatController.getDaftarSertifikat(user.getIdUser());
        System.out.println("\n=== SERTIFIKAT SAYA ===");
        if (list.isEmpty()) { System.out.println("[i] Belum ada sertifikat."); return; }
        list.forEach(row -> System.out.printf(
            "  %-22s v%-2s | %-30s | a.n. %-20s | %s%n",
            row[0], row[2], row[4], row[5], row[1]));
    }

    // ===================== HELPERS =====================

    static void printHasil(String hasil) {
        if (hasil == null) return;
        if (hasil.startsWith("SUKSES|"))     System.out.println("[✓] " + hasil.substring(7));
        else if (hasil.startsWith("ERROR|")) System.out.println("[✗] " + hasil.substring(6));
        else                                 System.out.println("[i] " + hasil.replace("INFO|", ""));
    }

    static int parseIntSafe(String s) {
        try { return Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) {
            System.out.println("[!] Input harus berupa angka. Menggunakan nilai 0.");
            return 0;
        }
    }

    static double parseDoubleSafe(String s) {
        try { return Double.parseDouble(s.trim()); }
        catch (NumberFormatException e) {
            System.out.println("[!] Input harus berupa angka. Menggunakan nilai 0.");
            return 0.0;
        }
    }

    static void printBanner() {
        System.out.println("""
        ╔══════════════════════════════════════════════════════╗
        ║      ███████╗██╗   ██╗███████╗███╗   ██╗████████╗    ║
        ║      ██╔════╝██║   ██║██╔════╝████╗  ██║╚══██╔══╝    ║
        ║      █████╗  ██║   ██║█████╗  ██╔██╗ ██║   ██║       ║
        ║      ██╔══╝  ╚██╗ ██╔╝██╔══╝  ██║╚██╗██║   ██║       ║
        ║      ███████╗ ╚████╔╝ ███████╗██║ ╚████║   ██║       ║
        ║      ╚══════╝  ╚═══╝  ╚══════╝╚═╝  ╚═══╝   ╚═╝       ║
        ║      Sistem Informasi Pengelolaan Seminar v4.0       ║
        ╚══════════════════════════════════════════════════════╝
        """);
    }
}
