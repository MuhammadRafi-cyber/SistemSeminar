package exception;
public class EmailSudahTerdaftarException extends Exception {
    public EmailSudahTerdaftarException(String email) {
        super("Email '" + email + "' sudah terdaftar. Gunakan email lain atau langsung login.");
    }
}
