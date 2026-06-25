package util;

import exception.KoneksiDatabaseException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Koneksi — Singleton JDBC Connection Manager
 * Untuk mengganti port: ubah nilai DB_PORT (misal: 3307 jika pakai XAMPP custom).
 */
public class Koneksi {

    private static final String DB_HOST     = "localhost";
    private static final int    DB_PORT     = 3306;           // Ganti ke 3307 jika perlu
    private static final String DB_NAME     = "db_pengelolaan_seminar";
    private static final String DB_USER     = "root";
    private static final String DB_PASSWORD = "";             // Ganti jika ada password

    private static final String URL = String.format(
        "jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=Asia/Jakarta&allowPublicKeyRetrieval=true",
        DB_HOST, DB_PORT, DB_NAME
    );

    private static Connection connection;

    private Koneksi() {}

    /**
     * Mengambil koneksi aktif. Membuat koneksi baru jika belum ada atau sudah tertutup.
     * @throws KoneksiDatabaseException jika MySQL tidak bisa dihubungi
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
            }
        } catch (ClassNotFoundException e) {
            throw new KoneksiDatabaseException("Driver MySQL tidak ditemukan. Pastikan mysql-connector-j sudah ditambahkan ke project.");
        } catch (SQLException e) {
            throw new KoneksiDatabaseException(e.getMessage());
        }
        return connection;
    }

    /** Menutup koneksi database. Panggil saat aplikasi ditutup. */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Gagal menutup koneksi: " + e.getMessage());
        }
    }

    /**
     * Ubah port koneksi secara runtime (opsional, jika tim mau switch tanpa recompile).
     * Panggil SEBELUM getConnection() pertama kali.
     */
    public static String buildUrl(String host, int port, String dbName) {
        return String.format(
            "jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=Asia/Jakarta&allowPublicKeyRetrieval=true",
            host, port, dbName
        );
    }
}
