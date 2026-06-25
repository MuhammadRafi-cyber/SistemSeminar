import controller.*;
import dao.*;
import model.*;
import service.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;

/**
 * Main — Entry point aplikasi Eventix.
 * Struktur Layer: View (Main) → Controller → Service → DAO → Database
 */
public class Main {

    // === WIRING ===
    static UserDAO        userDAO        = new UserDAO();
    static SeminarDAO     seminarDAO     = new SeminarDAO();
    static PendaftaranDAO pendaftaranDAO = new PendaftaranDAO();
    static PresensiDAO    presensiDAO    = new PresensiDAO();
    static SertifikatDAO  sertifikatDAO  = new SertifikatDAO();
    static LaporanDAO     laporanDAO     = new LaporanDAO();

    static AuthService        authService        = new AuthService(userDAO);
    static SeminarService     seminarService     = new SeminarService(seminarDAO);
    static PendaftaranService pendaftaranService = new PendaftaranService(pendaftaranDAO, seminarDAO);
    static PresensiService    presensiService    = new PresensiService(presensiDAO, pendaftaranDAO);
    static SertifikatService  sertifikatService  = new SertifikatService(sertifikatDAO, pendaftaranDAO, presensiDAO);
    static LaporanService     laporanService     = new LaporanService(laporanDAO);

    static AuthController        authController        = new AuthController(authService);
    static SeminarController     seminarController     = new SeminarController(seminarService);
    static PendaftaranController pendaftaranController = new PendaftaranController(pendaftaranService);
    static PresensiController    presensiController    = new PresensiController(presensiService);
    static SertifikatController  sertifikatController  = new SertifikatController(sertifikatService);
    static LaporanController     laporanController     = new LaporanController(laporanService);

    static Scanner sc = new Scanner(System.in);

    // ===================== MAIN =====================
    public static void main(String[] args) {
        printBanner();
        boolean running = true;
        while (running) {
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
        }
        System.out.println("\n=== Terima kasih telah menggunakan Eventix! ===");
        util.Koneksi.closeConnection();
    }

    // ===================== MENU AWAL =====================
    static boolean menuAwal() {
        System.out.println("\n╔══════════════════════════════╗");
        System.out.println("║        MENU UTAMA EVENTIX    ║");
        System.out.println("╠══════════════════════════════╣");
        System.out.println("║ 1. Login                     ║");
        System.out.println("║ 2. Registrasi Peserta        ║");
        System.out.println("║ 3. Lihat Seminar Tersedia    ║");
        System.out.println("║ 0. Keluar                    ║");
        System.out.println("╚══════════════════════════════╝");
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
        System.out.println("\n┌─ MENU PESERTA [" + user.getNama() + "] ───────────────────┐");
        System.out.println("│ 1. Lihat Seminar Tersedia                         │");
        System.out.println("│ 2. Daftar Seminar                                 │");
        System.out.println("│ 3. Riwayat Pendaftaran                            │");
        System.out.println("│ 4. Batalkan Pendaftaran                           │");
        System.out.println("│ 5. Unduh Sertifikat (via ID Pendaftaran)          │");
        System.out.println("│ 6. Lihat Sertifikat Saya                          │");
        System.out.println("│ 7. Laporan Riwayat Seminar (F1)                   │");
        System.out.println("│ 8. Edit Profil                                    │");
        System.out.println("│ 0. Logout                                         │");
        System.out.println("└───────────────────────────────────────────────────┘");
        System.out.print("Pilih: ");
        switch (sc.nextLine().trim()) {
            case "1" -> lihatSeminarPublik();
            case "2" -> doDaftar(user);
            case "3" -> lihatRiwayat(user);
            case "4" -> doBatalkan();
            case "5" -> doGenerateSertifikat();
            case "6" -> lihatSertifikat(user);
            case "7" -> tampilLaporanPeserta(user);
            case "8" -> doEditProfil(user);
            case "0" -> System.out.println(authController.logout());
            default  -> System.out.println("[!] Pilihan tidak valid.");
        }
    }

    // ===================== MENU PANITIA =====================
    static void menuPanitia(User user) {
        System.out.println("\n┌─ MENU PANITIA [" + user.getNama() + "] ──────────────────┐");
        System.out.println("│ 1. Seminar Saya                                   │");
        System.out.println("│ 2. Tambah Seminar Baru                            │");
        System.out.println("│ 3. Edit Seminar                                   │");
        System.out.println("│ 4. Hapus Seminar                                  │");
        System.out.println("│ 5. Selesaikan Seminar                             │");
        System.out.println("│ 6. Lihat Daftar Peserta Seminar                   │");
        System.out.println("│ 7. Catat Presensi (ID Pendaftaran)                │");
        System.out.println("│ 8. Cek Status Presensi                            │");
        System.out.println("│ 9. Laporan Seminar (F2)                           │");
        System.out.println("│ 0. Logout                                         │");
        System.out.println("└───────────────────────────────────────────────────┘");
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
            case "9" -> tampilLaporanPanitia(user);
            case "0" -> System.out.println(authController.logout());
            default  -> System.out.println("[!] Pilihan tidak valid.");
        }
    }

    // ===================== MENU ADMIN =====================
    static void menuAdmin(User user) {
        System.out.println("\n┌─ MENU ADMIN ──────────────────────────────────────┐");
        System.out.println("│ 1. Semua Seminar                                  │");
        System.out.println("│ 2. Tambah Seminar                                 │");
        System.out.println("│ 3. Hapus Seminar Manapun                          │");
        System.out.println("│ 4. Selesaikan Seminar                             │");
        System.out.println("│ 5. Catat Presensi                                 │");
        System.out.println("│ 6. Laporan Seminar (F2)                           │");
        System.out.println("│ 0. Logout                                         │");
        System.out.println("└───────────────────────────────────────────────────┘");
        System.out.print("Pilih: ");
        switch (sc.nextLine().trim()) {
            case "1" -> lihatSemuaSeminar();
            case "2" -> doTambahSeminar(user);
            case "3" -> doHapusSeminar(user);
            case "4" -> doSelesaikanSeminar(user);
            case "5" -> doScanPresensi(user);
            case "6" -> tampilLaporanPanitia(user);
            case "0" -> System.out.println(authController.logout());
            default  -> System.out.println("[!] Pilihan tidak valid.");
        }
    }

    // ===================== AKSI AUTH =====================

    static void doLogin() {
        System.out.print("Email   : "); String email = sc.nextLine();
        System.out.print("Password: "); String pass  = sc.nextLine();
        printHasil(authController.login(email, pass));
    }

    static void doRegistrasi() {
        System.out.println("--- Registrasi Peserta Baru ---");
        System.out.print("Nama       : "); String nama  = sc.nextLine();
        System.out.print("Email      : "); String email = sc.nextLine();
        System.out.print("Password   : "); String pass  = sc.nextLine();
        System.out.print("No. Telpon : "); String noTlp = sc.nextLine();
        printHasil(authController.registrasi(nama, email, pass, noTlp));
    }

    static void doEditProfil(User user) {
        System.out.println("--- Edit Profil ---");
        System.out.print("Nama baru       (Enter=skip): "); String nama = sc.nextLine();
        System.out.print("No. Telpon baru (Enter=skip): "); String tlp  = sc.nextLine();
        if (nama.isEmpty()) nama = user.getNama();
        if (tlp.isEmpty())  tlp  = user.getNoTelepon();
        printHasil(authController.updateProfil(nama, tlp));
    }

    // ===================== AKSI SEMINAR =====================

    static void lihatSeminarPublik() {
        List<model.Seminar> list = seminarController.getSeminarDibuka();
        if (list.isEmpty()) { System.out.println("[i] Belum ada seminar yang dibuka."); return; }
        System.out.println("\n=== SEMINAR TERSEDIA ===");
        list.forEach(s -> System.out.printf("  [%d] %-40s | %s | Sisa: %d slot%n",
            s.getIdSeminar(), s.getJudul(), s.getTanggalPelaksanaan(), s.getSisaKuota()));
    }

    static void lihatSemuaSeminar() {
        List<model.Seminar> list = seminarController.getSemuaSeminar();
        System.out.println("\n=== SEMUA SEMINAR ===");
        if (list.isEmpty()) { System.out.println("[i] Tidak ada seminar."); return; }
        list.forEach(s -> System.out.printf("  [%d] %-40s | %s | %s%n",
            s.getIdSeminar(), s.getJudul(), s.getTanggalPelaksanaan(), s.getStatus()));
    }

    static void lihatSeminarSaya(User panitia) {
        List<model.Seminar> list = seminarController.getSeminarSaya(panitia.getIdUser());
        System.out.println("\n=== SEMINAR SAYA ===");
        if (list.isEmpty()) { System.out.println("[i] Belum ada seminar."); return; }
        list.forEach(s -> System.out.printf("  [%d] %-40s | Terisi: %d/%d | %s%n",
            s.getIdSeminar(), s.getJudul(), s.getKuotaTerisi(), s.getKuota(), s.getStatus()));
    }

    static void doTambahSeminar(User panitia) {
        System.out.println("--- Tambah Seminar ---");
        System.out.print("Judul              : "); String judul    = sc.nextLine();
        System.out.print("Deskripsi          : "); String desk     = sc.nextLine();
        System.out.print("Tanggal (YYYY-MM-DD)    : "); String tglStr   = sc.nextLine();
        System.out.print("Waktu Mulai (HH:MM)     : "); String mulaiStr = sc.nextLine();
        System.out.print("Waktu Selesai (HH:MM)   : "); String selesStr = sc.nextLine();
        System.out.print("Lokasi             : "); String lokasi   = sc.nextLine();
        System.out.print("Kuota              : "); int    kuota    = Integer.parseInt(sc.nextLine());
        System.out.print("Harga (0=gratis)   : "); double harga    = Double.parseDouble(sc.nextLine());
        try {
            LocalDate tgl   = LocalDate.parse(tglStr);
            LocalTime mulai = mulaiStr.isEmpty() ? null : LocalTime.parse(mulaiStr);
            LocalTime seles = selesStr.isEmpty() ? null : LocalTime.parse(selesStr);
            printHasil(seminarController.tambahSeminar(panitia, judul, desk, tgl, mulai, seles, lokasi, kuota, harga));
        } catch (Exception e) {
            System.out.println("[ERROR] Format tanggal/waktu salah. Gunakan YYYY-MM-DD dan HH:MM.");
        }
    }

    static void doEditSeminar(User panitia) {
        lihatSeminarSaya(panitia);
        System.out.print("ID Seminar yang diedit: ");
        int id = Integer.parseInt(sc.nextLine());
        model.Seminar s = seminarController.getDetail(id);
        if (s == null) { System.out.println("[ERROR] Seminar tidak ditemukan."); return; }
        System.out.println("Judul saat ini : " + s.getJudul());
        System.out.print("Judul baru  (Enter=skip): "); String judul    = sc.nextLine();
        System.out.print("Lokasi baru (Enter=skip): "); String lokasi   = sc.nextLine();
        System.out.print("Kuota baru  (Enter=skip): "); String kuotaStr = sc.nextLine();
        if (!judul.isEmpty())    s.setJudul(judul);
        if (!lokasi.isEmpty())   s.setLokasi(lokasi);
        if (!kuotaStr.isEmpty()) s.setKuota(Integer.parseInt(kuotaStr));
        printHasil(seminarController.editSeminar(panitia, s));
    }

    static void doHapusSeminar(User panitia) {
        lihatSeminarSaya(panitia);
        System.out.print("ID Seminar yang dihapus: ");
        int id = Integer.parseInt(sc.nextLine());
        printHasil(seminarController.hapusSeminar(panitia, id));
    }

    static void doSelesaikanSeminar(User panitia) {
        lihatSeminarSaya(panitia);
        System.out.print("ID Seminar yang diselesaikan: ");
        int id = Integer.parseInt(sc.nextLine());
        printHasil(seminarController.selesaikanSeminar(panitia, id));
    }

    // ===================== AKSI PENDAFTARAN =====================

    static void doDaftar(User user) {
        lihatSeminarPublik();
        System.out.print("ID Seminar: ");
        int idSeminar = Integer.parseInt(sc.nextLine());
        printHasil(pendaftaranController.daftar(user.getIdUser(), idSeminar));
    }

    static void lihatRiwayat(User user) {
        List<Object[]> list = pendaftaranController.getRiwayat(user.getIdUser());
        System.out.println("\n=== RIWAYAT PENDAFTARAN ===");
        if (list.isEmpty()) { System.out.println("[i] Belum ada pendaftaran."); return; }
        list.forEach(row -> System.out.printf("  ID:%-4s | %-35s | %s | %s%n",
            row[3], row[0], row[1], row[2]));
    }

    static void doBatalkan() {
        System.out.print("ID Pendaftaran yang dibatalkan: ");
        int id = Integer.parseInt(sc.nextLine());
        printHasil(pendaftaranController.batalkan(id));
    }

    static void lihatPesertaSeminar() {
        System.out.print("ID Seminar: ");
        int id = Integer.parseInt(sc.nextLine());
        List<Object[]> list = pendaftaranController.getPesertaSeminar(id);
        System.out.println("\n=== PESERTA SEMINAR #" + id + " ===");
        if (list.isEmpty()) { System.out.println("[i] Belum ada peserta."); return; }
        list.forEach(row -> System.out.printf("  ID:%-4s | %-25s | %-30s | %s%n",
            row[0], row[1], row[2], row[4]));
    }

    // ===================== AKSI PRESENSI =====================

    static void doScanPresensi(User panitia) {
        System.out.print("ID Pendaftaran peserta: ");
        int idPendaftaran = Integer.parseInt(sc.nextLine());
        printHasil(presensiController.scanPresensi(panitia, idPendaftaran));
    }

    static void doCekPresensi() {
        System.out.print("ID Pendaftaran: ");
        int idPendaftaran = Integer.parseInt(sc.nextLine());
        printHasil(presensiController.cekStatus(idPendaftaran));
    }

    // ===================== AKSI SERTIFIKAT =====================

    static void doGenerateSertifikat() {
        System.out.print("ID Pendaftaran: ");
        int idPendaftaran = Integer.parseInt(sc.nextLine());
        printHasil(sertifikatController.generate(idPendaftaran));
    }

    static void lihatSertifikat(User user) {
        List<Object[]> list = sertifikatController.getDaftarSertifikat(user.getIdUser());
        System.out.println("\n=== SERTIFIKAT SAYA ===");
        if (list.isEmpty()) { System.out.println("[i] Belum ada sertifikat."); return; }
        list.forEach(row -> System.out.printf("  %s | %-35s | %s%n", row[0], row[2], row[1]));
    }

    // ===================== AKSI LAPORAN =====================

    /** F1 — Laporan riwayat peserta (judul, tanggal, kehadiran, sertifikat) */
    static void tampilLaporanPeserta(User user) {
        System.out.println();
        System.out.println(laporanController.getLaporanPeserta(user));
    }

    /** F2 — Laporan seminar panitia (pendaftar, hadir, sertifikat per seminar) */
    static void tampilLaporanPanitia(User user) {
        System.out.println();
        System.out.println(laporanController.getLaporanPanitia(user));
    }

    // ===================== HELPERS =====================

    static void printHasil(String hasil) {
        if (hasil.startsWith("SUKSES|"))      System.out.println("[✓] " + hasil.substring(7));
        else if (hasil.startsWith("ERROR|"))  System.out.println("[✗] " + hasil.substring(6));
        else                                  System.out.println("[i] " + hasil.replace("INFO|", ""));
    }

    static void printBanner() {
        System.out.println("""
        ╔═══════════════════════════════════════════════════╗
        ║    ███████╗██╗   ██╗███████╗███╗   ██╗████████╗  ║
        ║    ██╔════╝██║   ██║██╔════╝████╗  ██║╚══██╔══╝  ║
        ║    █████╗  ██║   ██║█████╗  ██╔██╗ ██║   ██║     ║
        ║    ██╔══╝  ╚██╗ ██╔╝██╔══╝  ██║╚██╗██║   ██║     ║
        ║    ███████╗ ╚████╔╝ ███████╗██║ ╚████║   ██║     ║
        ║    ╚══════╝  ╚═══╝  ╚══════╝╚═╝  ╚═══╝   ╚═╝     ║
        ║         Sistem Informasi Pengelolaan Seminar       ║
        ╚═══════════════════════════════════════════════════╝
        """);
    }
}
