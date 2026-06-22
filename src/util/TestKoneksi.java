package util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * TestKoneksi
 * Jalankan file ini (klik kanan > Run, atau lewat terminal) untuk
 * memastikan koneksi ke database sudah berhasil sebelum lanjut ke
 * pembuatan DAO/service yang lebih kompleks.
 *
 * Jika sukses, akan tercetak daftar nama tabel dan jumlah user di database.
 */
public class TestKoneksi {

    public static void main(String[] args) {
        Connection conn = Koneksi.getConnection();

        if (conn == null) {
            System.out.println("Koneksi GAGAL. Cek lagi URL, USER, PASSWORD di Koneksi.java");
            return;
        }

        System.out.println("Koneksi BERHASIL ke database db_pengelolaan_seminar");

        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM user");
            if (rs.next()) {
                System.out.println("Jumlah data di tabel user: " + rs.getInt("total"));
            }
        } catch (Exception e) {
            System.out.println("Koneksi berhasil, tapi query gagal: " + e.getMessage());
            System.out.println("Pastikan kamu sudah menjalankan 01_schema_database.sql dan 02_dummy_data.sql");
        }

        Koneksi.closeConnection();
    }
}
