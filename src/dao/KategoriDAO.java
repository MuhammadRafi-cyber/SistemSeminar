package dao;

import exception.DataTidakDitemukanException;
import model.Kategori;
import util.Koneksi;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KategoriDAO {

    public List<Kategori> getAll() throws SQLException {
        String sql = "SELECT id_kategori, nama_kategori FROM kategori ORDER BY nama_kategori";
        List<Kategori> list = new ArrayList<>();
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(new Kategori(rs.getInt(1), rs.getString(2)));
        }
        return list;
    }

    public Kategori getById(int id) throws SQLException, DataTidakDitemukanException {
        String sql = "SELECT id_kategori, nama_kategori FROM kategori WHERE id_kategori = ?";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new Kategori(rs.getInt(1), rs.getString(2));
            }
        }
        throw new DataTidakDitemukanException("Kategori", id);
    }

    public int insert(Kategori k) throws SQLException {
        String sql = "INSERT INTO kategori (nama_kategori) VALUES (?)";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, k.getNamaKategori());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) { int id = rs.getInt(1); k.setIdKategori(id); return id; }
            }
        }
        throw new SQLException("Gagal mendapatkan generated key untuk kategori.");
    }
}
