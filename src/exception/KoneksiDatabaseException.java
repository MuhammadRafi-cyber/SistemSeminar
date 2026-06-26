package exception;
public class KoneksiDatabaseException extends RuntimeException {
    public KoneksiDatabaseException(String detail) {
        super("Koneksi ke database gagal: " + detail
            + "\nPastikan MySQL (XAMPP) sudah berjalan dan konfigurasi di Koneksi.java sudah benar.");
    }
}
