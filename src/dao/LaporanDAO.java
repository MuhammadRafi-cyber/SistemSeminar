package dao;

import util.Koneksi;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LaporanDAO {

    /**
     * F1: Laporan riwayat peserta — seminar, kehadiran, sertifikat.
     * Return: [{judulSeminar, tanggalMulai, namaPeserta, kodeBooking, statusHadir, nomorSertifikat}]
     */
    public List<Object[]> getLaporanPeserta(int idUser) throws SQLException {
        String sql =
            "SELECT se.judul, se.tanggal_mulai, dp.nama_peserta, dp.kode_booking, "
          + "pr.status AS status_hadir, st.nomor_sertifikat "
          + "FROM pendaftaran p "
          + "JOIN seminar se             ON p.id_seminar = se.id_seminar "
          + "JOIN detail_pendaftaran dp  ON p.id_pendaftaran = dp.id_pendaftaran "
          + "LEFT JOIN presensi pr       ON dp.id_detail = pr.id_detail "
          + "LEFT JOIN sertifikat st     ON dp.id_detail = st.id_detail "
          + "WHERE p.id_pemesan = ? AND p.status != 'CANCELLED' "
          + "ORDER BY se.tanggal_mulai DESC";
        List<Object[]> list = new ArrayList<>();
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idUser);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(new Object[]{
                    rs.getString("judul"),
                    rs.getString("tanggal_mulai"),
                    rs.getString("nama_peserta"),
                    rs.getString("kode_booking"),
                    rs.getString("status_hadir"),
                    rs.getString("nomor_sertifikat")
                });
            }
        }
        return list;
    }

    /**
     * F2: Laporan seminar per panitia.
     * Return: [{judulSeminar, tanggalMulai, jmlPendaftar, jmlTiket, jmlHadir, jmlTidakHadir,
     *           jmlBatal, jmlSertifikat}]
     */
    public List<Object[]> getLaporanPanitia(int idPanitia) throws SQLException {
        String sql =
            "SELECT se.judul, se.tanggal_mulai, "
          + "COUNT(DISTINCT CASE WHEN p.status != 'CANCELLED' THEN p.id_pendaftaran END) AS jml_pendaftar, "
          + "COUNT(DISTINCT CASE WHEN p.status != 'CANCELLED' THEN dp.id_detail END)     AS jml_tiket, "
          + "SUM(CASE WHEN pr.status = 'HADIR' THEN 1 ELSE 0 END)                        AS jml_hadir, "
          + "SUM(CASE WHEN pr.status = 'TIDAK_HADIR' AND p.status='CONFIRMED' THEN 1 ELSE 0 END) AS jml_tidak_hadir, "
          + "COUNT(DISTINCT CASE WHEN p.status = 'CANCELLED' THEN p.id_pendaftaran END)  AS jml_batal, "
          + "COUNT(DISTINCT st.id_sertifikat)                                             AS jml_sertifikat "
          + "FROM seminar se "
          + "LEFT JOIN pendaftaran p         ON se.id_seminar = p.id_seminar "
          + "LEFT JOIN detail_pendaftaran dp ON p.id_pendaftaran = dp.id_pendaftaran "
          + "LEFT JOIN presensi pr           ON dp.id_detail = pr.id_detail "
          + "LEFT JOIN sertifikat st         ON dp.id_detail = st.id_detail "
          + "WHERE se.id_panitia = ? "
          + "GROUP BY se.id_seminar, se.judul, se.tanggal_mulai "
          + "ORDER BY se.tanggal_mulai DESC";
        List<Object[]> list = new ArrayList<>();
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idPanitia);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(new Object[]{
                    rs.getString("judul"),
                    rs.getString("tanggal_mulai"),
                    rs.getInt("jml_pendaftar"),
                    rs.getInt("jml_tiket"),
                    rs.getInt("jml_hadir"),
                    rs.getInt("jml_tidak_hadir"),
                    rs.getInt("jml_batal"),
                    rs.getInt("jml_sertifikat")
                });
            }
        }
        return list;
    }
}
