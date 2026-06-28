package dao;

import enums.ModeSeminar;
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
        "id_seminar, id_institusi, id_panitia, id_kategori, judul, deskripsi, pembicara, " +
        "tanggal_mulai, tanggal_selesai, mode, lokasi, kuota, kuota_terisi, harga, status, " +
        "banner_path, dibuat_pada ";

    public List<Seminar> getAll() throws SQLException {
        return query(SELECT_COLS + "FROM seminar ORDER BY tanggal_mulai DESC", ps -> {});
    }

    public List<Seminar> getDibuka() throws SQLException {
        return query(SELECT_COLS + "FROM seminar WHERE status = 'DIBUKA' ORDER BY tanggal_mulai ASC", ps -> {});
    }

    public List<Seminar> getByPanitia(int idPanitia) throws SQLException {
        return query(SELECT_COLS + "FROM seminar WHERE id_panitia = ? ORDER BY tanggal_mulai DESC",
            ps -> ps.setInt(1, idPanitia));
    }

    public List<Seminar> getByInstitusi(int idInstitusi) throws SQLException {
        return query(SELECT_COLS + "FROM seminar WHERE id_institusi = ? AND status = 'DIBUKA' ORDER BY tanggal_mulai ASC",
            ps -> ps.setInt(1, idInstitusi));
    }

    public List<Seminar> getByKategori(int idKategori) throws SQLException {
        return query(SELECT_COLS + "FROM seminar WHERE id_kategori = ? AND status = 'DIBUKA'",
            ps -> ps.setInt(1, idKategori));
    }

    public Seminar getById(int id) throws SQLException, DataTidakDitemukanException {
        List<Seminar> r = query(SELECT_COLS + "FROM seminar WHERE id_seminar = ?",
            ps -> ps.setInt(1, id));
        if (r.isEmpty()) throw new DataTidakDitemukanException("Seminar", id);
        return r.get(0);
    }

    public int insert(Seminar s) throws SQLException {
        String sql = "INSERT INTO seminar "
                   + "(id_institusi, id_panitia, id_kategori, judul, deskripsi, pembicara, "
                   + "tanggal_mulai, tanggal_selesai, mode, lokasi, kuota, harga, banner_path) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1,    s.getIdInstitusi());
            ps.setInt(2,    s.getIdPanitia());
            if (s.getIdKategori() != null) ps.setInt(3, s.getIdKategori());
            else ps.setNull(3, Types.INTEGER);
            ps.setString(4, s.getJudul());
            ps.setString(5, s.getDeskripsi());
            ps.setString(6, s.getPembicara());
            ps.setTimestamp(7, Timestamp.valueOf(s.getTanggalMulai()));
            ps.setTimestamp(8, Timestamp.valueOf(s.getTanggalSelesai()));
            ps.setString(9, s.getMode() != null ? s.getMode().name() : "OFFLINE");
            ps.setString(10, s.getLokasi());
            ps.setInt(11,   s.getKuota());
            ps.setDouble(12, s.getHarga());
            ps.setString(13, s.getBannerPath());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) { int id = rs.getInt(1); s.setIdSeminar(id); return id; }
            }
        }
        throw new SQLException("Gagal mendapatkan generated key untuk seminar baru.");
    }

    public boolean update(Seminar s) throws SQLException {
        String sql = "UPDATE seminar SET judul=?, deskripsi=?, pembicara=?, tanggal_mulai=?, "
                   + "tanggal_selesai=?, mode=?, lokasi=?, kuota=?, harga=?, status=?, "
                   + "id_kategori=?, banner_path=? WHERE id_seminar=?";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, s.getJudul());
            ps.setString(2, s.getDeskripsi());
            ps.setString(3, s.getPembicara());
            ps.setTimestamp(4, Timestamp.valueOf(s.getTanggalMulai()));
            ps.setTimestamp(5, Timestamp.valueOf(s.getTanggalSelesai()));
            ps.setString(6, s.getMode() != null ? s.getMode().name() : "OFFLINE");
            ps.setString(7, s.getLokasi());
            ps.setInt(8,    s.getKuota());
            ps.setDouble(9, s.getHarga());
            ps.setString(10, s.getStatus().name());
            if (s.getIdKategori() != null) ps.setInt(11, s.getIdKategori());
            else ps.setNull(11, Types.INTEGER);
            ps.setString(12, s.getBannerPath());
            ps.setInt(13,   s.getIdSeminar());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Soft delete: ubah status ke CANCELLED (BR-07, 9.1).
     * Hard delete TIDAK dilakukan jika seminar sudah punya peserta.
     */
    public boolean softDelete(int idSeminar) throws SQLException {
        String sql = "UPDATE seminar SET status = 'CANCELLED' WHERE id_seminar = ?";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idSeminar);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Hard delete — hanya boleh jika belum ada peserta.
     * Pakai softDelete jika ada peserta (dicek di SeminarService).
     */
    public boolean hardDelete(int idSeminar) throws SQLException {
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

    public boolean tambahKuotaTerisi(int idSeminar, int jumlah) throws SQLException {
        String sql = "UPDATE seminar SET kuota_terisi = kuota_terisi + ? WHERE id_seminar = ?";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, jumlah); ps.setInt(2, idSeminar);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean kurangiKuotaTerisi(int idSeminar, int jumlah) throws SQLException {
        String sql = "UPDATE seminar SET kuota_terisi = kuota_terisi - ? "
                   + "WHERE id_seminar = ? AND kuota_terisi >= ?";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, jumlah); ps.setInt(2, idSeminar); ps.setInt(3, jumlah);
            return ps.executeUpdate() > 0;
        }
    }

    /** Cek apakah seminar sudah punya peserta (untuk menentukan soft vs hard delete). */
    public boolean punyaPeserta(int idSeminar) throws SQLException {
        String sql = "SELECT COUNT(*) FROM pendaftaran WHERE id_seminar = ? AND status != 'CANCELLED'";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idSeminar);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    @FunctionalInterface interface PsSetter { void set(PreparedStatement ps) throws SQLException; }

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
        Timestamp tmM = rs.getTimestamp("tanggal_mulai");
        Timestamp tmS = rs.getTimestamp("tanggal_selesai");
        LocalDateTime mulai   = tmM != null ? tmM.toLocalDateTime() : null;
        LocalDateTime selesai = tmS != null ? tmS.toLocalDateTime() : null;

        int     rawKat   = rs.getInt("id_kategori");
        Integer idKat    = rs.wasNull() ? null : rawKat;
        String  modeStr  = rs.getString("mode");
        ModeSeminar mode = modeStr != null ? ModeSeminar.valueOf(modeStr) : ModeSeminar.OFFLINE;

        return new Seminar(
            rs.getInt("id_seminar"), rs.getInt("id_institusi"),
            rs.getInt("id_panitia"), idKat,
            rs.getString("judul"), rs.getString("deskripsi"), rs.getString("pembicara"),
            mulai, selesai, mode, rs.getString("lokasi"),
            rs.getInt("kuota"), rs.getInt("kuota_terisi"), rs.getDouble("harga"),
            StatusSeminar.valueOf(rs.getString("status")),
            rs.getString("banner_path"), rs.getString("dibuat_pada")
        );
    }
}
