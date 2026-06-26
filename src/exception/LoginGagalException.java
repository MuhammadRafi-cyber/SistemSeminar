package exception;
public class LoginGagalException extends Exception {
    public LoginGagalException() {
        super("Email atau password salah. Silakan coba lagi.");
    }
}
