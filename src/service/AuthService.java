package service;

import dao.AuditLogDAO;
import dao.UserDAO;
import exception.*;
import model.Peserta;
import model.User;
import util.PasswordHelper;
import util.Validator;
import java.sql.SQLException;

public class AuthService {
    private final UserDAO     userDAO;
    private final AuditLogDAO auditLogDAO;

    public AuthService(UserDAO userDAO, AuditLogDAO auditLogDAO) {
        this.userDAO     = userDAO;
        this.auditLogDAO = auditLogDAO;
    }

    /**
     * Registrasi peserta baru.
     * Username disimpan tapi tidak dipakai untuk login (PRD Bagian 18).
     */
    public Peserta registrasi(String nama, String username, String email, String password,
                               String noTelepon, Integer idInstitusi)
            throws InputKosongException, EmailTidakValidException, PasswordTidakValidException,
                   EmailSudahTerdaftarException, SQLException {

        Validator.cekTidakKosong(nama, "Nama");
        Validator.cekEmail(email);
        Validator.cekPassword(password);

        if (userDAO.emailSudahAda(email.trim().toLowerCase()))
            throw new EmailSudahTerdaftarException(email);

        // Cek username unik jika diisi
        if (username != null && !username.isEmpty() && userDAO.usernameSudahAda(username))
            throw new EmailSudahTerdaftarException("Username '" + username + "' sudah dipakai.");

        String passwordHash = PasswordHelper.hash(password);
        Peserta p = new Peserta(idInstitusi, nama,
            username != null ? username.trim() : null,
            email.trim().toLowerCase(), passwordHash, noTelepon);
        userDAO.registrasi(p);
        auditLogDAO.log(p.getIdUser(), "REGISTRASI", "user", p.getIdUser(),
            "Registrasi baru: " + email);
        return p;
    }

    /** Login menggunakan email + password (bukan username). */
    public User login(String email, String password)
            throws InputKosongException, LoginGagalException, SQLException {
        Validator.cekTidakKosong(email,    "Email");
        Validator.cekTidakKosong(password, "Password");
        User user = userDAO.cariByEmail(email.trim().toLowerCase());
        if (user == null || !PasswordHelper.verify(password, user.getPasswordHash()))
            throw new LoginGagalException();
        auditLogDAO.log(user.getIdUser(), "LOGIN", "user", user.getIdUser(),
            "Login: " + email);
        return user;
    }

    public boolean updateProfil(int idUser, String nama, String username,
                                 String noTelepon, Integer idInstitusi)
            throws InputKosongException, SQLException {
        Validator.cekTidakKosong(nama, "Nama");
        boolean ok = userDAO.updateProfil(idUser, nama, username, noTelepon, idInstitusi);
        if (ok) auditLogDAO.log(idUser, "UPDATE_PROFIL", "user", idUser, "Update profil");
        return ok;
    }

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
            auditLogDAO.log(user.getIdUser(), "GANTI_PASSWORD", "user",
                user.getIdUser(), "Ganti password");
        }
        return ok;
    }
}
