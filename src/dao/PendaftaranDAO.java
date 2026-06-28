package dao;

import enums.StatusPendaftaran;
import exception.DataTidakDitemukanException;
import model.DetailPendaftaran;
import model.Pendaftaran;
import util.Koneksi;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PendaftaranDAO {

    /**
     * Insert header pendaftaran + semua tiket dalam satu transaksi atomik.
     * Kuota BELUM dikurangi di sini — dikurangi setelah pembayaran BERHASIL.
     */
    public int insertDenganDetail(Pendaftaran p) throws SQLException {
        Connection c = Koneksi.getConnection();
        c.setAutoCommit(false);
        try {
            // 1. Insert header
            String sqlHeader = "INSERT INTO pendaftaran (id_pemesan, id_seminar, kode_transaksi, status, total) "
                             + "VALUES (?, ?, ?, ?, ?)";
            int idPendaftaran;
            try (PreparedStatement ps = c.prepareStatement(sqlHeader, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, p.getIdPemesan()); ps.setInt(2, p.getIdSeminar());
                ps.setString(3, p.getKodeTransaksi()); ps.setString(4, p.getStatus().name());
                ps.setDouble(5, p.getTotal());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (!rs.next()) throw new SQLException("Gagal mendapatkan ID pendaftaran.");
                    idPendaftaran = rs.getInt(1);
                    p.setIdPendaftaran(idPendaftaran);
                }
            }
            // 2. Insert tiket batch
            String sqlDetail = "INSERT INTO detail_pendaftaran "
                             + "(id_pendaftaran, nama_peserta, email_peserta, no_telepon, kode_booking, qr_data) "
                             + "VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = c.prepareStatement(sqlDetail, Statement.RETURN_GENERATED_KEYS)) {
                for (DetailPendaftaran d : p.getDetailList()) {
                    ps.setInt(1, idPendaftaran); ps.setString(2, d.getNamaPeserta());
                    ps.setString(3, d.getEmailPeserta()); ps.setString(4, d.getNoTelepon());
                    ps.setString(5, d.getKodeBooking()); ps.setString(6, d.getQrData());
                    ps.addBatch();
                }
                ps.executeBatch();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    int i = 0;
                    while (rs.next() && i < p.getDetailList().size())
                        p.getDetailList().get(i++).setIdDetail(rs.getInt(1));
                }
            }
            c.commit();
            return idPendaftaran;
        } catch (SQLException e) {
            c.rollback();
            throw e;
        } finally {
            c.setAutoCommit(true);
        }
    }

    public Pendaftaran getById(int id) throws SQLException, DataTidakDitemukanException {
        String sql = "SELECT id_pendaftaran, id_pemesan, id_seminar, kode_transaksi, status, total, tanggal_daftar "
                   + "FROM pendaftaran WHERE id_pendaftaran = ?";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        throw new DataTidakDitemukanException("Pendaftaran", id);
    }

    public Pendaftaran getByKodeTransaksi(String kode) throws SQLException, DataTidakDitemukanException {
        String sql = "SELECT id_pendaftaran, id_pemesan, id_seminar, kode_transaksi, status, total, tanggal_daftar "
                   + "FROM pendaftaran WHERE kode_transaksi = ?";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, kode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        throw new DataTidakDitemukanException("Pendaftaran", kode);
    }

    /** C5 / F1: riwayat pendaftaran pemesan */
    public List<Object[]> getRiwayatPemesan(int idPemesan) throws SQLException {
        String sql = "SELECT p.id_pendaftaran, p.kode_transaksi, s.judul, s.tanggal_mulai, "
                   + "p.status, p.total "
                   + "FROM pendaftaran p JOIN seminar s ON p.id_seminar = s.id_seminar "
                   + "WHERE p.id_pemesan = ? ORDER BY p.tanggal_daftar DESC";
        List<Object[]> list = new ArrayList<>();
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idPemesan);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(new Object[]{
                    rs.getInt("id_pendaftaran"), rs.getString("kode_transaksi"),
                    rs.getString("judul"),       rs.getString("tanggal_mulai"),
                    rs.getString("status"),      rs.getDouble("total")
                });
            }
        }
        return list;
    }

    /** C4: daftar peserta seminar (untuk Panitia) */
    public List<Object[]> getPesertaBySeminar(int idSeminar) throws SQLException {
        String sql = "SELECT p.id_pendaftaran, u.nama, u.email, p.kode_transaksi, "
                   + "p.tanggal_daftar, p.status, p.total "
                   + "FROM pendaftaran p JOIN user u ON p.id_pemesan = u.id_user "
                   + "WHERE p.id_seminar = ? ORDER BY p.tanggal_daftar ASC";
        List<Object[]> list = new ArrayList<>();
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idSeminar);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(new Object[]{
                    rs.getInt("id_pendaftaran"), rs.getString("nama"),
                    rs.getString("email"),       rs.getString("kode_transaksi"),
                    rs.getString("tanggal_daftar"), rs.getString("status"),
                    rs.getDouble("total")
                });
            }
        }
        return list;
    }

    public boolean updateStatus(int idPendaftaran, StatusPendaftaran status) throws SQLException {
        String sql = "UPDATE pendaftaran SET status = ? WHERE id_pendaftaran = ?";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status.name()); ps.setInt(2, idPendaftaran);
            return ps.executeUpdate() > 0;
        }
    }

    public List<DetailPendaftaran> getDetailByPendaftaran(int idPendaftaran) throws SQLException {
        String sql = "SELECT id_detail, id_pendaftaran, nama_peserta, email_peserta, "
                   + "no_telepon, kode_booking, qr_data FROM detail_pendaftaran WHERE id_pendaftaran = ?";
        List<DetailPendaftaran> list = new ArrayList<>();
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idPendaftaran);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapDetailRow(rs));
            }
        }
        return list;
    }

    /** Cari detail tiket berdasarkan kode_booking (untuk presensi & sertifikat). */
    public DetailPendaftaran getDetailByKodeBooking(String kode)
            throws SQLException, DataTidakDitemukanException {
        String sql = "SELECT id_detail, id_pendaftaran, nama_peserta, email_peserta, "
                   + "no_telepon, kode_booking, qr_data FROM detail_pendaftaran WHERE kode_booking = ?";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, kode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapDetailRow(rs);
            }
        }
        throw new DataTidakDitemukanException("Tiket", kode);
    }

    public int hitungDetail(int idPendaftaran) throws SQLException {
        String sql = "SELECT COUNT(*) FROM detail_pendaftaran WHERE id_pendaftaran = ?";
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idPendaftaran);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getInt(1) : 0; }
        }
    }

    private Pendaftaran mapRow(ResultSet rs) throws SQLException {
        return new Pendaftaran(
            rs.getInt("id_pendaftaran"), rs.getInt("id_pemesan"),
            rs.getInt("id_seminar"),     rs.getString("kode_transaksi"),
            StatusPendaftaran.valueOf(rs.getString("status")),
            rs.getDouble("total"),       rs.getString("tanggal_daftar")
        );
    }
    private DetailPendaftaran mapDetailRow(ResultSet rs) throws SQLException {
        return new DetailPendaftaran(
            rs.getInt("id_detail"), rs.getInt("id_pendaftaran"),
            rs.getString("nama_peserta"), rs.getString("email_peserta"),
            rs.getString("no_telepon"),   rs.getString("kode_booking"),
            rs.getString("qr_data")
        );
    }
}
