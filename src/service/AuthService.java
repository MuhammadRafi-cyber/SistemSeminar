package service;

import dao.UserDAO;
import exception.*;
import model.Peserta;
import model.User;
import util.PasswordHelper;
import util.Validator;

import java.sql.SQLException;

/**
 * AuthService — Business logic registrasi dan login.
 * Validasi dilakukan di sini, BUKAN di DAO atau View.
 */
public class AuthService {

    private final UserDAO userDAO;

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Registrasi Peserta baru.
     * @return objek Peserta yang sudah punya idUser
     */
    public Peserta registrasi(String nama, String email, String password, String noTelepon)
            throws InputKosongException, EmailTidakValidException, PasswordTidakValidException,
                   EmailSudahTerdaftarException, SQLException {

        // Validasi input
        Validator.cekTidakKosong(nama,      "Nama");
        Validator.cekEmail(email);
        Validator.cekPassword(password);

        // Cek email duplikat
        if (userDAO.emailSudahAda(email)) {
            throw new EmailSudahTerdaftarException(email);
        }

        // Hash password sebelum simpan
        String hash = PasswordHelper.hash(password);

        // Buat objek dan simpan
        Peserta peserta = new Peserta(nama, email, hash, noTelepon);
        userDAO.registrasi(peserta);

        return peserta;
    }

    /**
     * Login — verifikasi email + password, kembalikan objek User sesuai role.
     * @throws LoginGagalException jika email/password salah
     */
    public User login(String email, String password)
            throws InputKosongException, LoginGagalException, SQLException {

        Validator.cekTidakKosong(email,    "Email");
        Validator.cekTidakKosong(password, "Password");

        User user = userDAO.cariByEmail(email);

        if (user == null) {
            throw new LoginGagalException();
        }

        // Verifikasi password hash
        if (!PasswordHelper.verify(password, user.getPasswordHash())) {
            throw new LoginGagalException();
        }

        return user;
    }

    /**
     * Update profil (nama & no_telepon).
     */
    public boolean updateProfil(int idUser, String nama, String noTelepon)
            throws InputKosongException, SQLException {
        Validator.cekTidakKosong(nama, "Nama");
        return userDAO.updateProfil(idUser, nama, noTelepon);
    }

    /**
     * Ganti password: verifikasi password lama dulu.
     */
    public boolean gantiPassword(User user, String passwordLama, String passwordBaru)
            throws PasswordTidakValidException, LoginGagalException, InputKosongException, SQLException {

        if (!PasswordHelper.verify(passwordLama, user.getPasswordHash())) {
            throw new LoginGagalException();
        }
        Validator.cekPassword(passwordBaru);
        String newHash = PasswordHelper.hash(passwordBaru);
        return userDAO.updatePassword(user.getIdUser(), newHash);
    }
}
