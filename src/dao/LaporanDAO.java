package dao;

import util.Koneksi;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LaporanDAO {

    /**
     * F1: Laporan peserta — riwayat seminar, kehadiran, sertifikat.
     * Return: List Object[]{judulSeminar, tanggalMulai, namaPeserta, statusHadir, nomorSertifikat}
     */
    public List<Object[]> getLaporanPeserta(int idUser) throws SQLException {
        String sql =
            "SELECT se.judul, se.tanggal_mulai, dp.nama_peserta, " +
            "pr.status AS status_hadir, st.nomor AS nomor_sertifikat " +
            "FROM pendaftaran p " +
            "JOIN seminar se ON p.id_seminar = se.id_seminar " +
            "JOIN detail_pendaftaran dp ON p.id_pendaftaran = dp.id_pendaftaran " +
            "LEFT JOIN presensi pr ON dp.id_detail = pr.id_detail " +
            "LEFT JOIN sertifikat st ON dp.id_detail = st.id_detail " +
            "WHERE p.id_pemesan = ? " +
            "ORDER BY se.tanggal_mulai DESC";
        List<Object[]> list = new ArrayList<>();
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idUser);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getString("judul"),
                        rs.getString("tanggal_mulai"),
                        rs.getString("nama_peserta"),
                        rs.getString("status_hadir"),
                        rs.getString("nomor_sertifikat")
                    });
                }
            }
        }
        return list;
    }

    /**
     * F2: Laporan panitia — per seminar: jumlah pendaftar, tiket, hadir, sertifikat.
     * Return: List Object[]{judulSeminar, tanggalMulai, jmlPendaftar, jmlTiket, jmlHadir, jmlSertifikat}
     */
    public List<Object[]> getLaporanPanitia(int idPanitia) throws SQLException {
        String sql =
            "SELECT se.judul, se.tanggal_mulai, " +
            "COUNT(DISTINCT p.id_pendaftaran) AS jml_pendaftar, " +
            "COUNT(DISTINCT dp.id_detail)     AS jml_tiket, " +
            "SUM(CASE WHEN pr.status = 'HADIR' THEN 1 ELSE 0 END) AS jml_hadir, " +
            "COUNT(DISTINCT st.id_sertifikat) AS jml_sertifikat " +
            "FROM seminar se " +
            "LEFT JOIN pendaftaran p  ON se.id_seminar = p.id_seminar " +
            "LEFT JOIN detail_pendaftaran dp ON p.id_pendaftaran = dp.id_pendaftaran " +
            "LEFT JOIN presensi pr ON dp.id_detail = pr.id_detail " +
            "LEFT JOIN sertifikat st ON dp.id_detail = st.id_detail " +
            "WHERE se.id_panitia = ? " +
            "GROUP BY se.id_seminar, se.judul, se.tanggal_mulai " +
            "ORDER BY se.tanggal_mulai DESC";
        List<Object[]> list = new ArrayList<>();
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idPanitia);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getString("judul"),
                        rs.getString("tanggal_mulai"),
                        rs.getInt("jml_pendaftar"),
                        rs.getInt("jml_tiket"),
                        rs.getInt("jml_hadir"),
                        rs.getInt("jml_sertifikat")
                    });
                }
            }
        }
        return list;
    }
}
