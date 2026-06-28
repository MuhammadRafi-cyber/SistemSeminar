package controller;

import exception.*;
import model.Peserta;
import model.User;
import service.AuthService;
import java.sql.SQLException;

public class AuthController {
    private final AuthService authService;
    private static User userAktif;

    public AuthController(AuthService authService) { this.authService = authService; }

    public String registrasi(String nama, String username, String email, String password,
                              String noTelepon, Integer idInstitusi) {
        try {
            Peserta p = authService.registrasi(nama, username, email, password, noTelepon, idInstitusi);
            return "SUKSES|Registrasi berhasil! Selamat datang, " + p.getNama()
                 + ". ID akun: " + p.getIdUser();
        } catch (InputKosongException | EmailTidakValidException | PasswordTidakValidException
               | EmailSudahTerdaftarException e) {
            return "ERROR|" + e.getMessage();
        } catch (SQLException e) {
            return "ERROR|Gagal menyimpan ke database: " + e.getMessage();
        } catch (Exception e) {
            return "ERROR|Kesalahan tidak terduga: " + e.getMessage();
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
            return "ERROR|Gagal menghubungi database: " + e.getMessage();
        } catch (Exception e) {
            return "ERROR|Kesalahan tidak terduga: " + e.getMessage();
        }
    }

    public String logout() {
        if (userAktif == null) return "INFO|Tidak ada sesi aktif.";
        String nama = userAktif.getNama(); userAktif = null;
        return "SUKSES|Sampai jumpa, " + nama + "!";
    }

    public String updateProfil(String nama, String username, String noTelepon, Integer idInstitusi) {
        if (userAktif == null) return "ERROR|Anda harus login terlebih dahulu.";
        try {
            boolean ok = authService.updateProfil(userAktif.getIdUser(), nama, username, noTelepon, idInstitusi);
            if (ok) { userAktif.setNama(nama); userAktif.setUsername(username);
                      userAktif.setNoTelepon(noTelepon); userAktif.setIdInstitusi(idInstitusi); }
            return ok ? "SUKSES|Profil berhasil diperbarui." : "ERROR|Tidak ada perubahan.";
        } catch (InputKosongException e) { return "ERROR|" + e.getMessage();
        } catch (SQLException e) { return "ERROR|Gagal update profil: " + e.getMessage();
        } catch (Exception e) { return "ERROR|Kesalahan tidak terduga: " + e.getMessage(); }
    }

    public String gantiPassword(String passwordLama, String passwordBaru) {
        if (userAktif == null) return "ERROR|Anda harus login terlebih dahulu.";
        try {
            boolean ok = authService.gantiPassword(userAktif, passwordLama, passwordBaru);
            return ok ? "SUKSES|Password berhasil diubah." : "ERROR|Gagal mengubah password.";
        } catch (PasswordTidakValidException e) { return "ERROR|Password baru tidak valid: " + e.getMessage();
        } catch (LoginGagalException e) { return "ERROR|Password lama salah.";
        } catch (InputKosongException e) { return "ERROR|" + e.getMessage();
        } catch (SQLException e) { return "ERROR|Gagal update password: " + e.getMessage();
        } catch (Exception e) { return "ERROR|Kesalahan tidak terduga: " + e.getMessage(); }
    }

    public static User    getUserAktif()       { return userAktif; }
    public static boolean sudahLogin()         { return userAktif != null; }
    public static void    setUserAktif(User u) { userAktif = u; }
}
