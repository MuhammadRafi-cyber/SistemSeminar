package dao;

import enums.StatusPembayaran;
import enums.StatusRefund;
import model.Pembayaran;
import util.Koneksi;
import java.sql.*;

public class PembayaranDAO {

    /** Insert pembayaran PENDING saat pendaftaran dibuat (sebelum user konfirmasi bayar). */
    public int insert(int idPendaftaran, String metode, double nominal) throws SQLException {
        String sql = "INSERT INTO pembayaran (id_pendaftaran, metode, status, nominal, status_refund) "
                   + "VALUES (?, ?, 'PENDING', ?, 'TIDAK_ADA')";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idPendaftaran);
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
     * Update status pembayaran (PENDING → BERHASIL / GAGAL).
     * Jika BERHASIL, set waktu_bayar = NOW().
     */
    public boolean updateStatus(int idPendaftaran, StatusPembayaran status) throws SQLException {
        String sql = status == StatusPembayaran.BERHASIL
            ? "UPDATE pembayaran SET status = ?, waktu_bayar = NOW() WHERE id_pendaftaran = ?"
            : "UPDATE pembayaran SET status = ? WHERE id_pendaftaran = ?";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, idPendaftaran);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Update status_refund (TIDAK_ADA → DIMINTA → DIPROSES → SELESAI).
     */
    public boolean updateStatusRefund(int idPendaftaran, StatusRefund statusRefund) throws SQLException {
        String sql = "UPDATE pembayaran SET status_refund = ? WHERE id_pendaftaran = ?";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, statusRefund.name());
            ps.setInt(2, idPendaftaran);
            return ps.executeUpdate() > 0;
        }
    }

    /** Update metode pembayaran (ketika user ganti metode saat retry bayar). */
    public boolean updateMetode(int idPendaftaran, String metode) throws SQLException {
        String sql = "UPDATE pembayaran SET metode = ? WHERE id_pendaftaran = ?";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, metode);
            ps.setInt(2, idPendaftaran);
            return ps.executeUpdate() > 0;
        }
    }

    public Pembayaran getByPendaftaran(int idPendaftaran) throws SQLException {
        String sql = "SELECT id_pembayaran, id_pendaftaran, metode, status, nominal, "
                   + "waktu_bayar, status_refund FROM pembayaran WHERE id_pendaftaran = ?";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idPendaftaran);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    private Pembayaran mapRow(ResultSet rs) throws SQLException {
        String srStr = rs.getString("status_refund");
        StatusRefund sr = srStr != null ? StatusRefund.valueOf(srStr) : StatusRefund.TIDAK_ADA;
        return new Pembayaran(
            rs.getInt("id_pembayaran"),
            rs.getInt("id_pendaftaran"),
            rs.getString("metode"),
            StatusPembayaran.valueOf(rs.getString("status")),
            rs.getDouble("nominal"),
            rs.getString("waktu_bayar"),
            sr
        );
    }
}
