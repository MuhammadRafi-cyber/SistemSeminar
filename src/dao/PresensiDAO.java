package dao;

import enums.StatusHadir;
import model.Presensi;
import util.Koneksi;

import java.sql.*;

/**
 * PresensiDAO — query D1 dan D2 dari file SQL tim.
 * Tabel: presensi(id_presensi, id_pendaftaran, status_hadir, waktu_presensi)
 * FK ke pendaftaran.id_pendaftaran (bukan ke detail_pendaftaran).
 */
public class PresensiDAO {

    // D1. Simpan atau update presensi (INSERT ... ON DUPLICATE KEY UPDATE)
    public boolean simpanAtauUpdate(int idPendaftaran, StatusHadir status) throws SQLException {
        String sql = "INSERT INTO presensi (id_pendaftaran, status_hadir, waktu_presensi) "
                   + "VALUES (?, ?, NOW()) "
                   + "ON DUPLICATE KEY UPDATE status_hadir = ?, waktu_presensi = NOW()";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1,    idPendaftaran);
            ps.setString(2, status.name());
            ps.setString(3, status.name());
            return ps.executeUpdate() > 0;
        }
    }

    // D2. Cek status kehadiran berdasarkan id_pendaftaran
    public StatusHadir getStatusByPendaftaran(int idPendaftaran) throws SQLException {
        String sql = "SELECT status_hadir FROM presensi WHERE id_pendaftaran = ?";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPendaftaran);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return StatusHadir.valueOf(rs.getString("status_hadir"));
            }
        }
        return null; // belum pernah presensi
    }

    // Hitung peserta HADIR per seminar (untuk laporan F2)
    public int hitungHadirBySeminar(int idSeminar) throws SQLException {
        String sql = "SELECT COUNT(*) FROM presensi pr "
                   + "JOIN pendaftaran p ON pr.id_pendaftaran = p.id_pendaftaran "
                   + "WHERE p.id_seminar = ? AND pr.status_hadir = 'HADIR'";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idSeminar);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }
}
