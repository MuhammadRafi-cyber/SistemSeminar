package dao;

import model.Sertifikat;
import util.Koneksi;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SertifikatDAO {

    /** E1: Insert sertifikat baru (hanya jika status = HADIR, dicek di Service). */
    public int insert(int idDetail, String nomor, String filePath) throws SQLException {
        String sql = "INSERT INTO sertifikat (id_detail, nomor, versi, file_path) VALUES (?, ?, 1, ?)";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1,    idDetail);
            ps.setString(2, nomor);
            ps.setString(3, filePath);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("Gagal mendapatkan generated key untuk sertifikat.");
    }

    /** Regenerate: update nomor, naikkan versi, update file_path. */
    public boolean regenerate(int idDetail, String nomorBaru, String filePathBaru) throws SQLException {
        String sql = "UPDATE sertifikat SET nomor = ?, versi = versi + 1, file_path = ?, "
                   + "tanggal_terbit = NOW() WHERE id_detail = ?";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nomorBaru);
            ps.setString(2, filePathBaru);
            ps.setInt(3,    idDetail);
            return ps.executeUpdate() > 0;
        }
    }

    public Sertifikat getByDetail(int idDetail) throws SQLException {
        String sql = "SELECT id_sertifikat, id_detail, nomor, versi, file_path, tanggal_terbit "
                   + "FROM sertifikat WHERE id_detail = ?";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idDetail);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    /** E2: Sertifikat milik seorang pemesan (via pendaftaran → detail → sertifikat). */
    public List<Object[]> getSertifikatByUser(int idUser) throws SQLException {
        String sql =
            "SELECT st.nomor, st.tanggal_terbit, st.versi, st.file_path, "
          + "se.judul, dp.nama_peserta "
          + "FROM sertifikat st "
          + "JOIN detail_pendaftaran dp ON st.id_detail = dp.id_detail "
          + "JOIN pendaftaran p ON dp.id_pendaftaran = p.id_pendaftaran "
          + "JOIN seminar se ON p.id_seminar = se.id_seminar "
          + "WHERE p.id_pemesan = ? ORDER BY st.tanggal_terbit DESC";
        List<Object[]> list = new ArrayList<>();
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idUser);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getString("nomor"),
                        rs.getString("tanggal_terbit"),
                        rs.getInt("versi"),
                        rs.getString("file_path"),
                        rs.getString("judul"),
                        rs.getString("nama_peserta")
                    });
                }
            }
        }
        return list;
    }

    public int hitungBySeminar(int idSeminar) throws SQLException {
        String sql = "SELECT COUNT(*) FROM sertifikat st "
                   + "JOIN detail_pendaftaran dp ON st.id_detail = dp.id_detail "
                   + "JOIN pendaftaran p ON dp.id_pendaftaran = p.id_pendaftaran "
                   + "WHERE p.id_seminar = ?";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idSeminar);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    private Sertifikat mapRow(ResultSet rs) throws SQLException {
        return new Sertifikat(
            rs.getInt("id_sertifikat"),
            rs.getInt("id_detail"),
            rs.getString("nomor"),
            rs.getInt("versi"),
            rs.getString("file_path"),
            rs.getString("tanggal_terbit")
        );
    }
}
