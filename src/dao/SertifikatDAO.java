package dao;

import model.Sertifikat;
import util.Koneksi;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SertifikatDAO — query E1 dan E2 dari file SQL tim.
 * Tabel: sertifikat(id_sertifikat, id_pendaftaran, kode_sertifikat, tanggal_terbit)
 * FK ke pendaftaran.id_pendaftaran.
 */
public class SertifikatDAO {

    // E1. Buat sertifikat baru
    public int insert(int idPendaftaran, String kodeSertifikat) throws SQLException {
        String sql = "INSERT INTO sertifikat (id_pendaftaran, kode_sertifikat) VALUES (?, ?)";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1,    idPendaftaran);
            ps.setString(2, kodeSertifikat);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    // E2. Sertifikat milik seorang peserta
    public List<Object[]> getSertifikatByUser(int idUser) throws SQLException {
        String sql = "SELECT st.kode_sertifikat, st.tanggal_terbit, se.judul "
                   + "FROM sertifikat st "
                   + "JOIN pendaftaran pd ON st.id_pendaftaran = pd.id_pendaftaran "
                   + "JOIN seminar se ON pd.id_seminar = se.id_seminar "
                   + "WHERE pd.id_user = ?";
        List<Object[]> list = new ArrayList<>();
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUser);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getString("kode_sertifikat"),
                        rs.getString("tanggal_terbit"),
                        rs.getString("judul")
                    });
                }
            }
        }
        return list;
    }

    // Cek apakah sertifikat sudah ada untuk pendaftaran ini
    public Sertifikat getByPendaftaran(int idPendaftaran) throws SQLException {
        String sql = "SELECT id_sertifikat, id_pendaftaran, kode_sertifikat, tanggal_terbit "
                   + "FROM sertifikat WHERE id_pendaftaran = ?";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPendaftaran);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Sertifikat(
                        rs.getInt("id_sertifikat"),
                        rs.getInt("id_pendaftaran"),
                        rs.getString("kode_sertifikat"),
                        rs.getString("tanggal_terbit")
                    );
                }
            }
        }
        return null;
    }

    // Hitung sertifikat per seminar (untuk laporan F2)
    public int hitungSertifikatBySeminar(int idSeminar) throws SQLException {
        String sql = "SELECT COUNT(st.id_sertifikat) FROM sertifikat st "
                   + "JOIN pendaftaran pd ON st.id_pendaftaran = pd.id_pendaftaran "
                   + "WHERE pd.id_seminar = ?";
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
