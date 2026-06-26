package controller;

import exception.*;
import model.Peserta;
import model.User;
import service.AuthService;
import java.sql.SQLException;

/**
 * AuthController — bridge View ↔ AuthService.
 * Semua exception ditangkap di sini; View hanya menerima String "SUKSES|..." atau "ERROR|...".
 */
public class AuthController {
    private final AuthService authService;
    private static User userAktif;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public String registrasi(String nama, String email, String password,
                              String noTelepon, Integer idInstitusi) {
        try {
            Peserta p = authService.registrasi(nama, email, password, noTelepon, idInstitusi);
            return "SUKSES|Registrasi berhasil! Selamat datang, " + p.getNama()
                 + ". ID akun Anda: " + p.getIdUser();
        } catch (InputKosongException | EmailTidakValidException
               | PasswordTidakValidException | EmailSudahTerdaftarException e) {
            return "ERROR|" + e.getMessage();
        } catch (SQLException e) {
            return "ERROR|Gagal menyimpan data ke database. Detail: " + e.getMessage();
        } catch (Exception e) {
            return "ERROR|Terjadi kesalahan tidak terduga: " + e.getMessage();
        }
    }

    public String login(String email, String password) {
        try {
            userAktif = authService.login(email, password);
            return "SUKSES|Login berhasil! Selamat datang, " + userAktif.getNama()
                 + " [" + userAktif.getRole() + "]";
        } catch (InputKosongException | LoginGagalException e) {
            return "ERROR|" + e.getMessage();
        } catch (SQLException e) {
            return "ERROR|Gagal menghubungi database. Pastikan MySQL aktif. Detail: " + e.getMessage();
        } catch (Exception e) {
            return "ERROR|Terjadi kesalahan tidak terduga: " + e.getMessage();
        }
    }

    public String logout() {
        if (userAktif == null) return "INFO|Tidak ada sesi aktif.";
        String nama = userAktif.getNama();
        userAktif = null;
        return "SUKSES|Sampai jumpa, " + nama + "!";
    }

    public String updateProfil(String nama, String noTelepon, Integer idInstitusi) {
        if (userAktif == null) return "ERROR|Anda harus login terlebih dahulu.";
        try {
            boolean ok = authService.updateProfil(userAktif.getIdUser(), nama, noTelepon, idInstitusi);
            if (ok) {
                userAktif.setNama(nama);
                userAktif.setNoTelepon(noTelepon);
                userAktif.setIdInstitusi(idInstitusi);
                return "SUKSES|Profil berhasil diperbarui.";
            }
            return "ERROR|Tidak ada perubahan yang tersimpan.";
        } catch (InputKosongException e) {
            return "ERROR|" + e.getMessage();
        } catch (SQLException e) {
            return "ERROR|Gagal memperbarui profil. Detail: " + e.getMessage();
        } catch (Exception e) {
            return "ERROR|Terjadi kesalahan tidak terduga: " + e.getMessage();
        }
    }

    public String gantiPassword(String passwordLama, String passwordBaru) {
        if (userAktif == null) return "ERROR|Anda harus login terlebih dahulu.";
        try {
            boolean ok = authService.gantiPassword(userAktif, passwordLama, passwordBaru);
            return ok ? "SUKSES|Password berhasil diubah."
                      : "ERROR|Gagal mengubah password.";
        } catch (PasswordTidakValidException e) {
            return "ERROR|Password baru tidak valid: " + e.getMessage();
        } catch (LoginGagalException e) {
            return "ERROR|Password lama salah. Silakan coba lagi.";
        } catch (InputKosongException e) {
            return "ERROR|" + e.getMessage();
        } catch (SQLException e) {
            return "ERROR|Gagal memperbarui password. Detail: " + e.getMessage();
        } catch (Exception e) {
            return "ERROR|Terjadi kesalahan tidak terduga: " + e.getMessage();
        }
    }

    public static User    getUserAktif()           { return userAktif; }
    public static boolean sudahLogin()             { return userAktif != null; }
    public static void    setUserAktif(User u)     { userAktif = u; }
}
