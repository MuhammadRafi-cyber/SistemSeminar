package dao;

import enums.StatusSeminar;
import model.Seminar;
import util.Koneksi;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * SeminarDAO — query B1–B5 dari file SQL tim.
 * B5 di tim adalah DELETE (hard delete), bukan soft delete.
 */
public class SeminarDAO {

    // B1. Semua seminar
    public List<Seminar> getAll() throws SQLException {
        String sql = "SELECT id_seminar, judul, deskripsi, tanggal_pelaksanaan, waktu_mulai, "
                   + "waktu_selesai, lokasi, kuota, kuota_terisi, status, id_panitia "
                   + "FROM seminar ORDER BY tanggal_pelaksanaan ASC";
        List<Seminar> list = new ArrayList<>();
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // B1 filtered: hanya DIBUKA (untuk landing page)
    public List<Seminar> getDibuka() throws SQLException {
        String sql = "SELECT id_seminar, judul, deskripsi, tanggal_pelaksanaan, waktu_mulai, "
                   + "waktu_selesai, lokasi, kuota, kuota_terisi, status, id_panitia "
                   + "FROM seminar WHERE status = 'DIBUKA' ORDER BY tanggal_pelaksanaan ASC";
        List<Seminar> list = new ArrayList<>();
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // B2. Seminar milik panitia tertentu
    public List<Seminar> getByPanitia(int idPanitia) throws SQLException {
        String sql = "SELECT id_seminar, judul, deskripsi, tanggal_pelaksanaan, waktu_mulai, "
                   + "waktu_selesai, lokasi, kuota, kuota_terisi, status, id_panitia "
                   + "FROM seminar WHERE id_panitia = ?";
        List<Seminar> list = new ArrayList<>();
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPanitia);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public Seminar getById(int idSeminar) throws SQLException {
        String sql = "SELECT id_seminar, judul, deskripsi, tanggal_pelaksanaan, waktu_mulai, "
                   + "waktu_selesai, lokasi, kuota, kuota_terisi, status, id_panitia "
                   + "FROM seminar WHERE id_seminar = ?";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idSeminar);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    // B3. Insert seminar baru
    public int insert(Seminar s) throws SQLException {
        String sql = "INSERT INTO seminar "
                   + "(judul, deskripsi, tanggal_pelaksanaan, waktu_mulai, waktu_selesai, lokasi, kuota, id_panitia) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getJudul());
            ps.setString(2, s.getDeskripsi());
            ps.setDate(3,   Date.valueOf(s.getTanggalPelaksanaan()));
            ps.setTime(4,   s.getWaktuMulai()   != null ? Time.valueOf(s.getWaktuMulai())   : null);
            ps.setTime(5,   s.getWaktuSelesai() != null ? Time.valueOf(s.getWaktuSelesai()) : null);
            ps.setString(6, s.getLokasi());
            ps.setInt(7,    s.getKuota());
            ps.setInt(8,    s.getIdPanitia());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) { int id = rs.getInt(1); s.setIdSeminar(id); return id; }
            }
        }
        return -1;
    }

    // B4. Update seminar
    public boolean update(Seminar s) throws SQLException {
        String sql = "UPDATE seminar SET judul=?, deskripsi=?, tanggal_pelaksanaan=?, "
                   + "waktu_mulai=?, waktu_selesai=?, lokasi=?, kuota=?, status=? "
                   + "WHERE id_seminar=?";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getJudul());
            ps.setString(2, s.getDeskripsi());
            ps.setDate(3,   Date.valueOf(s.getTanggalPelaksanaan()));
            ps.setTime(4,   s.getWaktuMulai()   != null ? Time.valueOf(s.getWaktuMulai())   : null);
            ps.setTime(5,   s.getWaktuSelesai() != null ? Time.valueOf(s.getWaktuSelesai()) : null);
            ps.setString(6, s.getLokasi());
            ps.setInt(7,    s.getKuota());
            ps.setString(8, s.getStatus().name());
            ps.setInt(9,    s.getIdSeminar());
            return ps.executeUpdate() > 0;
        }
    }

    // B5. Hapus seminar (hard delete — sesuai DB tim)
    public boolean hapus(int idSeminar) throws SQLException {
        String sql = "DELETE FROM seminar WHERE id_seminar = ?";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idSeminar);
            return ps.executeUpdate() > 0;
        }
    }

    // C3. Tambah kuota_terisi +1
    public boolean tambahKuotaTerisi(int idSeminar) throws SQLException {
        String sql = "UPDATE seminar SET kuota_terisi = kuota_terisi + 1 WHERE id_seminar = ?";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idSeminar);
            return ps.executeUpdate() > 0;
        }
    }

    // Kurangi kuota_terisi -1 (saat pembatalan)
    public boolean kurangiKuotaTerisi(int idSeminar) throws SQLException {
        String sql = "UPDATE seminar SET kuota_terisi = kuota_terisi - 1 "
                   + "WHERE id_seminar = ? AND kuota_terisi > 0";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idSeminar);
            return ps.executeUpdate() > 0;
        }
    }

    // Extra: ubah status seminar menjadi SELESAI (dipanggil Panitia setelah acara)
    public boolean selesaikan(int idSeminar) throws SQLException {
        String sql = "UPDATE seminar SET status = 'SELESAI' WHERE id_seminar = ?";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idSeminar);
            return ps.executeUpdate() > 0;
        }
    }

    private Seminar mapRow(ResultSet rs) throws SQLException {
        int          id      = rs.getInt("id_seminar");
        String       judul   = rs.getString("judul");
        String       desk    = rs.getString("deskripsi");
        LocalDate    tgl     = rs.getDate("tanggal_pelaksanaan").toLocalDate();
        Time         tmM     = rs.getTime("waktu_mulai");
        Time         tmS     = rs.getTime("waktu_selesai");
        LocalTime    mulai   = tmM != null ? tmM.toLocalTime() : null;
        LocalTime    selesai = tmS != null ? tmS.toLocalTime() : null;
        String       lokasi  = rs.getString("lokasi");
        int          kuota   = rs.getInt("kuota");
        int          kt      = rs.getInt("kuota_terisi");
        StatusSeminar status = StatusSeminar.valueOf(rs.getString("status"));
        int          idPan   = rs.getInt("id_panitia");
        // dibuat_pada tidak ada di query B1/B2 tim, pakai null
        return new Seminar(id, judul, desk, tgl, mulai, selesai, lokasi, kuota, kt, 0.0, status, idPan, null);
    }
}


