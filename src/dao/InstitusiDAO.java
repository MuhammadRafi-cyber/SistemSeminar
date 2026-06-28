package dao;

import exception.DataTidakDitemukanException;
import model.Institusi;
import util.Koneksi;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InstitusiDAO {

    public List<Institusi> getAll() throws SQLException {
        String sql = "SELECT id_institusi, nama, alamat, logo_path FROM institusi ORDER BY nama";
        List<Institusi> list = new ArrayList<>();
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public Institusi getById(int id) throws SQLException, DataTidakDitemukanException {
        String sql = "SELECT id_institusi, nama, alamat, logo_path FROM institusi WHERE id_institusi = ?";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        throw new DataTidakDitemukanException("Institusi", id);
    }

    public int insert(Institusi inst) throws SQLException {
        String sql = "INSERT INTO institusi (nama, alamat, logo_path) VALUES (?, ?, ?)";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, inst.getNama());
            ps.setString(2, inst.getAlamat());
            ps.setString(3, inst.getLogoPath());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) { int id = rs.getInt(1); inst.setIdInstitusi(id); return id; }
            }
        }
        throw new SQLException("Gagal mendapatkan generated key untuk institusi.");
    }

    private Institusi mapRow(ResultSet rs) throws SQLException {
        return new Institusi(
            rs.getInt("id_institusi"), rs.getString("nama"),
            rs.getString("alamat"),    rs.getString("logo_path")
        );
    }
}
