package dao;

import enums.StatusHadir;
import util.Koneksi;
import java.sql.*;

public class PresensiDAO {

    /**
     * D1: INSERT ... ON DUPLICATE KEY UPDATE
     * UNIQUE(id_detail) di DB menjamin satu tiket = satu presensi.
     * Kolom: status, waktu (sesuai DB v4)
     */
    public boolean simpanAtauUpdate(int idDetail, StatusHadir status) throws SQLException {
        String sql = "INSERT INTO presensi (id_detail, status, waktu) VALUES (?, ?, NOW()) "
                   + "ON DUPLICATE KEY UPDATE status = ?, waktu = NOW()";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1,    idDetail);
            ps.setString(2, status.name());
            ps.setString(3, status.name());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * D2: Cek status kehadiran berdasarkan id_detail.
     * @return StatusHadir atau null jika belum pernah presensi.
     */
    public StatusHadir getStatusByDetail(int idDetail) throws SQLException {
        String sql = "SELECT status FROM presensi WHERE id_detail = ?";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idDetail);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return StatusHadir.valueOf(rs.getString("status"));
            }
        }
        return null;
    }

    public int hitungHadirBySeminar(int idSeminar) throws SQLException {
        String sql = "SELECT COUNT(*) FROM presensi pr "
                   + "JOIN detail_pendaftaran dp ON pr.id_detail = dp.id_detail "
                   + "JOIN pendaftaran p ON dp.id_pendaftaran = p.id_pendaftaran "
                   + "WHERE p.id_seminar = ? AND pr.status = 'HADIR'";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idSeminar);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }
}
