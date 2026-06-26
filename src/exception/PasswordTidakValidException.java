package exception;
public class PasswordTidakValidException extends Exception {
    public PasswordTidakValidException() {
        super("Password minimal 8 karakter dan harus mengandung huruf serta angka.");
    }
}
