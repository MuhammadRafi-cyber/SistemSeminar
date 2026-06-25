package dao;

import enums.StatusPendaftaran;
import model.Pendaftaran;
import util.Koneksi;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * PendaftaranDAO — query C1–C5 dari file SQL tim.
 * Tabel: pendaftaran(id_pendaftaran, id_user, id_seminar, tanggal_daftar, status_pendaftaran)
 */
public class PendaftaranDAO {

    // C1. Cek apakah peserta sudah pernah daftar seminar ini
    public boolean sudahDaftar(int idUser, int idSeminar) throws SQLException {
        String sql = "SELECT id_pendaftaran FROM pendaftaran WHERE id_user = ? AND id_seminar = ?";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUser);
            ps.setInt(2, idSeminar);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    // C2. Simpan pendaftaran baru
    public int insert(Pendaftaran p) throws SQLException {
        String sql = "INSERT INTO pendaftaran (id_user, id_seminar) VALUES (?, ?)";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getIdUser());
            ps.setInt(2, p.getIdSeminar());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) { int id = rs.getInt(1); p.setIdPendaftaran(id); return id; }
            }
        }
        return -1;
    }

    // C4. Seluruh peserta dari satu seminar (untuk Panitia)
    public List<Object[]> getPesertaBySeminar(int idSeminar) throws SQLException {
        String sql = "SELECT p.id_pendaftaran, u.nama, u.email, p.tanggal_daftar, p.status_pendaftaran "
                   + "FROM pendaftaran p JOIN user u ON p.id_user = u.id_user "
                   + "WHERE p.id_seminar = ?";
        List<Object[]> list = new ArrayList<>();
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idSeminar);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getInt("id_pendaftaran"),
                        rs.getString("nama"),
                        rs.getString("email"),
                        rs.getString("tanggal_daftar"),
                        rs.getString("status_pendaftaran")
                    });
                }
            }
        }
        return list;
    }

    // C5. Riwayat pendaftaran seorang peserta (join dengan seminar)
    public List<Object[]> getRiwayatPeserta(int idUser) throws SQLException {
        String sql = "SELECT s.judul, s.tanggal_pelaksanaan, p.status_pendaftaran, p.id_pendaftaran "
                   + "FROM pendaftaran p JOIN seminar s ON p.id_seminar = s.id_seminar "
                   + "WHERE p.id_user = ?";
        List<Object[]> list = new ArrayList<>();
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUser);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getString("judul"),
                        rs.getString("tanggal_pelaksanaan"),
                        rs.getString("status_pendaftaran"),
                        rs.getInt("id_pendaftaran")
                    });
                }
            }
        }
        return list;
    }

    // Ambil pendaftaran by ID (untuk validasi di service)
    public Pendaftaran getById(int idPendaftaran) throws SQLException {
        String sql = "SELECT id_pendaftaran, id_user, id_seminar, tanggal_daftar, status_pendaftaran "
                   + "FROM pendaftaran WHERE id_pendaftaran = ?";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPendaftaran);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    // Update status pendaftaran
    public boolean updateStatus(int idPendaftaran, StatusPendaftaran status) throws SQLException {
        String sql = "UPDATE pendaftaran SET status_pendaftaran = ? WHERE id_pendaftaran = ?";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, idPendaftaran);
            return ps.executeUpdate() > 0;
        }
    }

    private Pendaftaran mapRow(ResultSet rs) throws SQLException {
        return new Pendaftaran(
            rs.getInt("id_pendaftaran"),
            rs.getInt("id_user"),
            rs.getInt("id_seminar"),
            rs.getString("tanggal_daftar"),
            StatusPendaftaran.valueOf(rs.getString("status_pendaftaran"))
        );
    }
}
