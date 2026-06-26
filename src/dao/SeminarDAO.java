package dao;

import enums.StatusSeminar;
import exception.DataTidakDitemukanException;
import model.Seminar;
import util.Koneksi;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SeminarDAO {

    private static final String SELECT_COLS =
        "id_seminar, id_institusi, id_panitia, judul, deskripsi, " +
        "tanggal_mulai, tanggal_selesai, lokasi, kuota, kuota_terisi, harga, status, dibuat_pada ";

    public List<Seminar> getAll() throws SQLException {
        return query(SELECT_COLS + "FROM seminar ORDER BY tanggal_mulai ASC", ps -> {});
    }

    public List<Seminar> getDibuka() throws SQLException {
        return query(SELECT_COLS + "FROM seminar WHERE status = 'DIBUKA' ORDER BY tanggal_mulai ASC", ps -> {});
    }

    public List<Seminar> getByPanitia(int idPanitia) throws SQLException {
        return query(SELECT_COLS + "FROM seminar WHERE id_panitia = ? ORDER BY tanggal_mulai ASC",
            ps -> ps.setInt(1, idPanitia));
    }

    public List<Seminar> getByInstitusi(int idInstitusi) throws SQLException {
        return query(SELECT_COLS + "FROM seminar WHERE id_institusi = ? AND status = 'DIBUKA'",
            ps -> ps.setInt(1, idInstitusi));
    }

    public Seminar getById(int id) throws SQLException, DataTidakDitemukanException {
        List<Seminar> r = query(SELECT_COLS + "FROM seminar WHERE id_seminar = ?",
            ps -> ps.setInt(1, id));
        if (r.isEmpty()) throw new DataTidakDitemukanException("Seminar", id);
        return r.get(0);
    }

    public int insert(Seminar s) throws SQLException {
        String sql = "INSERT INTO seminar "
                   + "(id_institusi, id_panitia, judul, deskripsi, tanggal_mulai, tanggal_selesai, "
                   + "lokasi, kuota, harga) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1,    s.getIdInstitusi());
            ps.setInt(2,    s.getIdPanitia());
            ps.setString(3, s.getJudul());
            ps.setString(4, s.getDeskripsi());
            ps.setTimestamp(5, Timestamp.valueOf(s.getTanggalMulai()));
            ps.setTimestamp(6, s.getTanggalSelesai() != null ? Timestamp.valueOf(s.getTanggalSelesai()) : null);
            ps.setString(7, s.getLokasi());
            ps.setInt(8,    s.getKuota());
            ps.setDouble(9, s.getHarga());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    s.setIdSeminar(id);
                    return id;
                }
            }
        }
        throw new SQLException("Gagal mendapatkan generated key untuk seminar baru.");
    }

    public boolean update(Seminar s) throws SQLException {
        String sql = "UPDATE seminar SET judul=?, deskripsi=?, tanggal_mulai=?, tanggal_selesai=?, "
                   + "lokasi=?, kuota=?, harga=?, status=?, id_institusi=? WHERE id_seminar=?";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, s.getJudul());
            ps.setString(2, s.getDeskripsi());
            ps.setTimestamp(3, Timestamp.valueOf(s.getTanggalMulai()));
            ps.setTimestamp(4, s.getTanggalSelesai() != null ? Timestamp.valueOf(s.getTanggalSelesai()) : null);
            ps.setString(5, s.getLokasi());
            ps.setInt(6,    s.getKuota());
            ps.setDouble(7, s.getHarga());
            ps.setString(8, s.getStatus().name());
            ps.setInt(9,    s.getIdInstitusi());
            ps.setInt(10,   s.getIdSeminar());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean hapus(int idSeminar) throws SQLException {
        String sql = "DELETE FROM seminar WHERE id_seminar = ?";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idSeminar);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean selesaikan(int idSeminar) throws SQLException {
        String sql = "UPDATE seminar SET status = 'SELESAI' WHERE id_seminar = ?";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idSeminar);
            return ps.executeUpdate() > 0;
        }
    }

    /** C3: tambah kuota_terisi sejumlah tiket yang dibeli */
    public boolean tambahKuotaTerisi(int idSeminar, int jumlah) throws SQLException {
        String sql = "UPDATE seminar SET kuota_terisi = kuota_terisi + ? WHERE id_seminar = ?";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, jumlah);
            ps.setInt(2, idSeminar);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean kurangiKuotaTerisi(int idSeminar, int jumlah) throws SQLException {
        String sql = "UPDATE seminar SET kuota_terisi = kuota_terisi - ? "
                   + "WHERE id_seminar = ? AND kuota_terisi >= ?";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, jumlah);
            ps.setInt(2, idSeminar);
            ps.setInt(3, jumlah);
            return ps.executeUpdate() > 0;
        }
    }

    // ── Helper ─────────────────────────────────────────────────────────
    @FunctionalInterface
    interface PsSetter { void set(PreparedStatement ps) throws SQLException; }

    private List<Seminar> query(String sql, PsSetter setter) throws SQLException {
        List<Seminar> list = new ArrayList<>();
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT " + sql)) {
            setter.set(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    private Seminar mapRow(ResultSet rs) throws SQLException {
        Timestamp tmMulai  = rs.getTimestamp("tanggal_mulai");
        Timestamp tmSeles  = rs.getTimestamp("tanggal_selesai");
        LocalDateTime mulai   = tmMulai  != null ? tmMulai.toLocalDateTime()  : null;
        LocalDateTime selesai = tmSeles  != null ? tmSeles.toLocalDateTime()  : null;
        return new Seminar(
            rs.getInt("id_seminar"),
            rs.getInt("id_institusi"),
            rs.getInt("id_panitia"),
            rs.getString("judul"),
            rs.getString("deskripsi"),
            mulai, selesai,
            rs.getString("lokasi"),
            rs.getInt("kuota"),
            rs.getInt("kuota_terisi"),
            rs.getDouble("harga"),
            StatusSeminar.valueOf(rs.getString("status")),
            rs.getString("dibuat_pada")
        );
    }
}
