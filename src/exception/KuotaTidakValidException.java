package exception;

public class KuotaTidakValidException extends Exception {
    public KuotaTidakValidException() {
        super("Kuota seminar harus berupa angka positif (minimal 1).");
    }
}
