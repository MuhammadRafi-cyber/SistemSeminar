package exception;
public class EmailTidakValidException extends Exception {
    public EmailTidakValidException() {
        super("Format email tidak valid. Gunakan format: nama@domain.com");
    }
}
