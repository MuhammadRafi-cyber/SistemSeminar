package exception;

public class KoneksiDatabaseException extends RuntimeException {
    public KoneksiDatabaseException() {
        super("Koneksi ke database gagal. Pastikan MySQL (XAMPP) sudah berjalan.");
    }

    public KoneksiDatabaseException(String detail) {
        super("Koneksi ke database gagal: " + detail);
    }
}
