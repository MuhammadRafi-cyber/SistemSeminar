package dao;

import util.Koneksi;
import java.sql.*;

public class AuditLogDAO {

    /**
     * Catat aksi ke audit_log.
     * Kolom DB v5: id_user (nullable), aksi, entitas, id_entitas, keterangan, waktu (auto).
     * Gagal senyap — error audit tidak boleh hentikan alur utama.
     */
    public void log(Integer idUser, String aksi, String entitas,
                    Integer idEntitas, String keterangan) {
        String sql = "INSERT INTO audit_log (id_user, aksi, entitas, id_entitas, keterangan) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (idUser != null) ps.setInt(1, idUser); else ps.setNull(1, Types.INTEGER);
            ps.setString(2, aksi);
            ps.setString(3, entitas);
            if (idEntitas != null) ps.setInt(4, idEntitas); else ps.setNull(4, Types.INTEGER);
            ps.setString(5, keterangan);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[AUDIT LOG ERROR] Gagal catat aksi '" + aksi + "': " + e.getMessage());
        }
    }
}
