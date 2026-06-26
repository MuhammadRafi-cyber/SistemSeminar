package dao;

import enums.Role;
import exception.DataTidakDitemukanException;
import model.*;
import util.Koneksi;
import java.sql.*;

public class UserDAO {

    public int registrasi(User user) throws SQLException {
        String sql = "INSERT INTO user (id_institusi, nama, email, password_hash, role, no_telepon) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (user.getIdInstitusi() != null) ps.setInt(1, user.getIdInstitusi());
            else ps.setNull(1, Types.INTEGER);
            ps.setString(2, user.getNama());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPasswordHash());   // kolom password_hash
            ps.setString(5, user.getRole().name());
            ps.setString(6, user.getNoTelepon());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    user.setIdUser(id);
                    return id;
                }
            }
        }
        throw new SQLException("Gagal mendapatkan generated key untuk user baru.");
    }

    public boolean emailSudahAda(String email) throws SQLException {
        String sql = "SELECT id_user FROM user WHERE email = ?";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Cari user berdasarkan email.
     * @return User subclass, atau null jika tidak ditemukan.
     */
    public User cariByEmail(String email) throws SQLException {
        String sql = "SELECT id_user, id_institusi, nama, email, password_hash, role, no_telepon, tanggal_daftar "
                   + "FROM user WHERE email = ?";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public User cariById(int idUser) throws SQLException, DataTidakDitemukanException {
        String sql = "SELECT id_user, id_institusi, nama, email, password_hash, role, no_telepon, tanggal_daftar "
                   + "FROM user WHERE id_user = ?";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idUser);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        throw new DataTidakDitemukanException("User", idUser);
    }

    public boolean updateProfil(int idUser, String nama, String noTelepon, Integer idInstitusi)
            throws SQLException {
        String sql = "UPDATE user SET nama = ?, no_telepon = ?, id_institusi = ? WHERE id_user = ?";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nama);
            ps.setString(2, noTelepon);
            if (idInstitusi != null) ps.setInt(3, idInstitusi);
            else ps.setNull(3, Types.INTEGER);
            ps.setInt(4, idUser);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updatePassword(int idUser, String newPasswordHash) throws SQLException {
        String sql = "UPDATE user SET password_hash = ? WHERE id_user = ?";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, newPasswordHash);
            ps.setInt(2, idUser);
            return ps.executeUpdate() > 0;
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        int     idUser       = rs.getInt("id_user");
        int     rawInst      = rs.getInt("id_institusi");
        Integer idInstitusi  = rs.wasNull() ? null : rawInst;
        String  nama         = rs.getString("nama");
        String  email        = rs.getString("email");
        String  passwordHash = rs.getString("password_hash");   // kolom password_hash
        Role    role         = Role.valueOf(rs.getString("role"));
        String  noTelepon    = rs.getString("no_telepon");
        String  tanggalDaftar = rs.getString("tanggal_daftar");

        return switch (role) {
            case PESERTA -> new Peserta(idUser, idInstitusi, nama, email, passwordHash, noTelepon, tanggalDaftar);
            case PANITIA -> new Panitia(idUser, idInstitusi, nama, email, passwordHash, noTelepon, tanggalDaftar);
            case ADMIN   -> new Admin(idUser, idInstitusi, nama, email, passwordHash, noTelepon, tanggalDaftar);
        };
    }
}
