package dao;

import enums.Role;
import model.Admin;
import model.Panitia;
import model.Peserta;
import model.User;
import util.Koneksi;

import java.sql.*;

/**
 * UserDAO — query A1, A2, A3 dari file SQL tim.
 * PENTING: A3 di DB tim mencocokkan password langsung di SQL.
 * Karena kita pakai hashing, A3 hanya ambil by email,
 * verifikasi password tetap dilakukan di AuthService (Java).
 */
public class UserDAO {

    // A1. Registrasi user baru
    public int registrasi(User user) throws SQLException {
        String sql = "INSERT INTO user (nama, email, password, role, no_telepon) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getNama());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getRole().name());
            ps.setString(5, user.getNoTelepon());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) { int id = rs.getInt(1); user.setIdUser(id); return id; }
            }
        }
        return -1;
    }

    // A2. Cek email sudah ada
    public boolean emailSudahAda(String email) throws SQLException {
        String sql = "SELECT id_user FROM user WHERE email = ?";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    // A3. Ambil user by email (untuk login — verifikasi password di AuthService)
    public User cariByEmail(String email) throws SQLException {
        String sql = "SELECT id_user, nama, email, password, role, no_telepon, tanggal_daftar "
                   + "FROM user WHERE email = ?";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public User cariById(int idUser) throws SQLException {
        String sql = "SELECT id_user, nama, email, password, role, no_telepon, tanggal_daftar "
                   + "FROM user WHERE id_user = ?";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUser);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public boolean updateProfil(int idUser, String nama, String noTelepon) throws SQLException {
        String sql = "UPDATE user SET nama = ?, no_telepon = ? WHERE id_user = ?";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nama); ps.setString(2, noTelepon); ps.setInt(3, idUser);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updatePassword(int idUser, String newPasswordHash) throws SQLException {
        String sql = "UPDATE user SET password = ? WHERE id_user = ?";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPasswordHash); ps.setInt(2, idUser);
            return ps.executeUpdate() > 0;
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        int    idUser        = rs.getInt("id_user");
        String nama          = rs.getString("nama");
        String email         = rs.getString("email");
        String passwordHash  = rs.getString("password");
        String roleStr       = rs.getString("role");
        String noTelepon     = rs.getString("no_telepon");
        String tanggalDaftar = rs.getString("tanggal_daftar");
        Role role = Role.valueOf(roleStr);
        return switch (role) {
            case PESERTA -> new Peserta(idUser, nama, email, passwordHash, noTelepon, tanggalDaftar);
            case PANITIA -> new Panitia(idUser, nama, email, passwordHash, noTelepon, tanggalDaftar);
            case ADMIN   -> new Admin(idUser, nama, email, passwordHash, noTelepon, tanggalDaftar);
        };
    }
}
