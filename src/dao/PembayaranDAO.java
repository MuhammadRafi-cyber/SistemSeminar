package dao;

import enums.StatusPembayaran;
import model.Pembayaran;
import util.Koneksi;
import java.sql.*;

public class PembayaranDAO {

    /**
     * Insert pembayaran PENDING.
     * UNIQUE(id_pendaftaran) di DB menjamin 1 pembayaran per transaksi.
     */
    public int insert(int idPendaftaran, String metode, double nominal) throws SQLException {
        String sql = "INSERT INTO pembayaran (id_pendaftaran, metode, status, nominal) VALUES (?, ?, 'PENDING', ?)";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1,    idPendaftaran);
            ps.setString(2, metode);
            ps.setDouble(3, nominal);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("Gagal mendapatkan generated key untuk pembayaran.");
    }

    /**
     * Update status pembayaran (kolom: status, tanggal_bayar).
     * Jika BERHASIL, set tanggal_bayar = NOW().
     */
    public boolean updateStatus(int idPendaftaran, StatusPembayaran status) throws SQLException {
        String sql = status == StatusPembayaran.BERHASIL
            ? "UPDATE pembayaran SET status = ?, tanggal_bayar = NOW() WHERE id_pendaftaran = ?"
            : "UPDATE pembayaran SET status = ? WHERE id_pendaftaran = ?";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, idPendaftaran);
            return ps.executeUpdate() > 0;
        }
    }

    public Pembayaran getByPendaftaran(int idPendaftaran) throws SQLException {
        String sql = "SELECT id_pembayaran, id_pendaftaran, metode, status, nominal, tanggal_bayar "
                   + "FROM pembayaran WHERE id_pendaftaran = ?";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idPendaftaran);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Pembayaran(
                        rs.getInt("id_pembayaran"),
                        rs.getInt("id_pendaftaran"),
                        rs.getString("metode"),
                        StatusPembayaran.valueOf(rs.getString("status")),
                        rs.getDouble("nominal"),
                        rs.getString("tanggal_bayar")
                    );
                }
            }
        }
        return null;
    }
}
