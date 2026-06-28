package util;

import exception.KoneksiDatabaseException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Koneksi — Singleton JDBC Connection Manager.
 * Untuk mengganti port: ubah DB_PORT (misal 3307 jika XAMPP custom).
 */
public class Koneksi {

    private static final String DB_HOST     = "localhost";
    private static final int    DB_PORT     = 3306;
    private static final String DB_NAME     = "db_pengelolaan_seminar";
    private static final String DB_USER     = "root";
    private static final String DB_PASSWORD = "";

    private static final String URL = String.format(
        "jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=Asia/Jakarta&allowPublicKeyRetrieval=true",
        DB_HOST, DB_PORT, DB_NAME
    );

    private static Connection connection;

    private Koneksi() {}

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
            }
            return connection;
        } catch (ClassNotFoundException e) {
            throw new KoneksiDatabaseException(
                "Driver MySQL tidak ditemukan. Tambahkan mysql-connector-j ke project.");
        } catch (SQLException e) {
            throw new KoneksiDatabaseException(e.getMessage());
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {
            System.err.println("[WARN] Gagal menutup koneksi: " + e.getMessage());
        }
    }
}
