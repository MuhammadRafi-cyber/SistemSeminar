package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Class Koneksi
 * Menyimpan URL, username, dan password database secara terpusat.
 * Dipakai oleh seluruh DAO untuk membuka/menutup koneksi ke MySQL.
 *
 * CATATAN UNTUK PEMULA:
 * - Sesuaikan DB_NAME, USER, dan PASSWORD dengan setting MySQL di komputermu.
 * - Jika kamu TIDAK memakai package (file Java langsung di folder src),
 *   hapus baris "package util;" di paling atas.
 */
public class Koneksi {

    private static final String DB_NAME = "db_pengelolaan_seminar";
    private static final String URL = "jdbc:mysql://localhost:3306/" + DB_NAME
            + "?useSSL=false&serverTimezone=Asia/Jakarta";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // ganti sesuai password MySQL kamu

    private static Connection connection;

    private Koneksi() {
        // Mencegah class ini di-instansiasi langsung (hanya dipakai lewat method static)
    }

    /**
     * Mengambil koneksi aktif ke database.
     * Jika belum ada koneksi atau koneksi sudah tertutup, akan dibuat ulang.
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Koneksi database berhasil dibuat.");
            }
        } catch (SQLException e) {
            System.err.println("Gagal terhubung ke database: " + e.getMessage());
        }
        return connection;
    }

    /**
     * Menutup koneksi database. Panggil ini saat aplikasi ditutup.
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Koneksi database ditutup.");
            }
        } catch (SQLException e) {
            System.err.println("Gagal menutup koneksi: " + e.getMessage());
        }
    }
}
