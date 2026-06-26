package dao;

import util.Koneksi;
import java.sql.*;

public class AuditLogDAO {

    /**
     * Catat aksi ke audit_log.
     * Kolom DB v4: id_user (nullable), aksi, entitas, waktu (auto).
     * Gagal senyap — error audit tidak boleh hentikan alur utama.
     */
    public void log(Integer idUser, String aksi, String entitas) {
        String sql = "INSERT INTO audit_log (id_user, aksi, entitas) VALUES (?, ?, ?)";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (idUser != null) ps.setInt(1, idUser);
            else ps.setNull(1, Types.INTEGER);
            ps.setString(2, aksi);
            ps.setString(3, entitas);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[AUDIT LOG ERROR] Gagal mencatat aksi '" + aksi + "': " + e.getMessage());
        }
    }
}
