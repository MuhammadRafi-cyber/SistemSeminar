package service;

import dao.AuditLogDAO;
import dao.UserDAO;
import exception.*;
import model.Peserta;
import model.User;
import util.PasswordHelper;
import util.Validator;
import java.sql.SQLException;

/**
 * AuthService — business logic registrasi dan login.
 * Error handling: setiap exception ditangkap atau diteruskan ke Controller.
 */
public class AuthService {
    private final UserDAO     userDAO;
    private final AuditLogDAO auditLogDAO;

    public AuthService(UserDAO userDAO, AuditLogDAO auditLogDAO) {
        this.userDAO     = userDAO;
        this.auditLogDAO = auditLogDAO;
    }

    /**
     * Registrasi peserta baru.
     * @throws InputKosongException       jika nama/email/password kosong
     * @throws EmailTidakValidException   jika format email salah
     * @throws PasswordTidakValidException jika password tidak memenuhi syarat
     * @throws EmailSudahTerdaftarException jika email sudah dipakai
     * @throws SQLException               jika terjadi error DB
     */
    public Peserta registrasi(String nama, String email, String password,
                               String noTelepon, Integer idInstitusi)
            throws InputKosongException, EmailTidakValidException,
                   PasswordTidakValidException, EmailSudahTerdaftarException, SQLException {

        Validator.cekTidakKosong(nama, "Nama");
        Validator.cekEmail(email);
        Validator.cekPassword(password);

        if (userDAO.emailSudahAda(email))
            throw new EmailSudahTerdaftarException(email);

        String passwordHash = PasswordHelper.hash(password);
        Peserta p = new Peserta(idInstitusi, nama, email.trim().toLowerCase(), passwordHash, noTelepon);
        userDAO.registrasi(p);
        auditLogDAO.log(p.getIdUser(), "REGISTRASI", "user");
        return p;
    }

    /**
     * Login — verifikasi email + password.
     * @return User subclass sesuai role
     * @throws LoginGagalException jika email tidak ditemukan atau password salah
     */
    public User login(String email, String password)
            throws InputKosongException, LoginGagalException, SQLException {

        Validator.cekTidakKosong(email,    "Email");
        Validator.cekTidakKosong(password, "Password");

        User user = userDAO.cariByEmail(email.trim().toLowerCase());
        if (user == null || !PasswordHelper.verify(password, user.getPasswordHash()))
            throw new LoginGagalException();

        auditLogDAO.log(user.getIdUser(), "LOGIN", "user");
        return user;
    }

    /**
     * Update profil (nama, no_telepon, id_institusi).
     */
    public boolean updateProfil(int idUser, String nama, String noTelepon, Integer idInstitusi)
            throws InputKosongException, SQLException {
        Validator.cekTidakKosong(nama, "Nama");
        boolean ok = userDAO.updateProfil(idUser, nama, noTelepon, idInstitusi);
        if (ok) auditLogDAO.log(idUser, "UPDATE_PROFIL", "user");
        return ok;
    }

    /**
     * Ganti password — verifikasi password lama terlebih dahulu.
     * @throws LoginGagalException jika password lama salah
     */
    public boolean gantiPassword(User user, String passwordLama, String passwordBaru)
            throws PasswordTidakValidException, LoginGagalException,
                   InputKosongException, SQLException {

        Validator.cekTidakKosong(passwordLama, "Password lama");
        if (!PasswordHelper.verify(passwordLama, user.getPasswordHash()))
            throw new LoginGagalException();

        Validator.cekPassword(passwordBaru);
        String newHash = PasswordHelper.hash(passwordBaru);
        boolean ok = userDAO.updatePassword(user.getIdUser(), newHash);
        if (ok) {
            user.setPasswordHash(newHash);
            auditLogDAO.log(user.getIdUser(), "GANTI_PASSWORD", "user");
        }
        return ok;
    }
}
