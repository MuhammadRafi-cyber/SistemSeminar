package controller;

import exception.*;
import model.Peserta;
import model.User;
import service.AuthService;

import java.sql.SQLException;

/**
 * AuthController — Menerima input dari View, memanggil AuthService,
 * dan mengembalikan hasil atau pesan error ke View.
 *
 * View TIDAK boleh langsung memanggil DAO atau Service lain.
 */
public class AuthController {

    private final AuthService authService;

    // Session: user yang sedang login (null = belum login)
    private static User userAktif;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // ===================== REGISTRASI =====================

    /**
     * Proses registrasi peserta baru.
     * @return pesan sukses atau pesan error
     */
    public String registrasi(String nama, String email, String password, String noTelepon) {
        try {
            Peserta peserta = authService.registrasi(nama, email, password, noTelepon);
            return "SUKSES|Registrasi berhasil! Selamat datang, " + peserta.getNama()
                 + ". ID Anda: " + peserta.getIdUser();
        } catch (InputKosongException | EmailTidakValidException
                | PasswordTidakValidException | EmailSudahTerdaftarException e) {
            return "ERROR|" + e.getMessage();
        } catch (SQLException e) {
            return "ERROR|Gagal menyimpan data. Cek koneksi database.";
        }
    }

    // ===================== LOGIN =====================

    /**
     * Proses login.
     * @return pesan sukses atau pesan error
     */
    public String login(String email, String password) {
        try {
            User user = authService.login(email, password);
            userAktif = user;
            return "SUKSES|Login berhasil! Selamat datang, " + user.getNama()
                 + " [" + user.getRole() + "]";
        } catch (InputKosongException | LoginGagalException e) {
            return "ERROR|" + e.getMessage();
        } catch (SQLException e) {
            return "ERROR|Gagal menghubungi database.";
        }
    }

    // ===================== LOGOUT =====================

    public String logout() {
        if (userAktif == null) return "INFO|Tidak ada sesi aktif.";
        String nama = userAktif.getNama();
        userAktif = null;
        return "SUKSES|Sampai jumpa, " + nama + "!";
    }

    // ===================== UPDATE PROFIL =====================

    public String updateProfil(String nama, String noTelepon) {
        if (userAktif == null) return "ERROR|Anda harus login terlebih dahulu.";
        try {
            boolean berhasil = authService.updateProfil(userAktif.getIdUser(), nama, noTelepon);
            if (berhasil) {
                userAktif.setNama(nama);
                userAktif.setNoTelepon(noTelepon);
                return "SUKSES|Profil berhasil diperbarui.";
            }
            return "ERROR|Tidak ada data yang diubah.";
        } catch (InputKosongException e) {
            return "ERROR|" + e.getMessage();
        } catch (SQLException e) {
            return "ERROR|Gagal memperbarui profil.";
        }
    }

    public String gantiPassword(String passwordLama, String passwordBaru) {
        if (userAktif == null) return "ERROR|Anda harus login terlebih dahulu.";
        try {
            boolean berhasil = authService.gantiPassword(userAktif, passwordLama, passwordBaru);
            if (berhasil) return "SUKSES|Password berhasil diubah.";
            return "ERROR|Gagal mengubah password.";
        } catch (PasswordTidakValidException | LoginGagalException | InputKosongException e) {
            return "ERROR|" + e.getMessage();
        } catch (SQLException e) {
            return "ERROR|Gagal memperbarui password.";
        }
    }

    // ===================== SESSION =====================

    public static User getUserAktif()   { return userAktif; }
    public static boolean sudahLogin()  { return userAktif != null; }
    public static void setUserAktif(User u) { userAktif = u; }
}
