package exception;
public class KuotaTidakValidException extends Exception {
    public KuotaTidakValidException() { super("Kuota harus berupa angka positif (minimal 1)."); }
}
