package dao;

import util.Koneksi;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * LaporanDAO — query F1 dan F2 dari file SQL tim.
 */
public class LaporanDAO {

    /**
     * F1. Laporan peserta: riwayat seminar, kehadiran, sertifikat.
     * Return: List Object[]{judul, tanggal_pelaksanaan, status_hadir, kode_sertifikat}
     * status_hadir dan kode_sertifikat bisa NULL jika belum presensi / belum dapat sertifikat.
     */
    public List<Object[]> getLaporanPeserta(int idUser) throws SQLException {
        String sql =
            "SELECT se.judul, se.tanggal_pelaksanaan, pr.status_hadir, st.kode_sertifikat " +
            "FROM pendaftaran pd " +
            "JOIN seminar se ON pd.id_seminar = se.id_seminar " +
            "LEFT JOIN presensi pr ON pd.id_pendaftaran = pr.id_pendaftaran " +
            "LEFT JOIN sertifikat st ON pd.id_pendaftaran = st.id_pendaftaran " +
            "WHERE pd.id_user = ?";

        List<Object[]> list = new ArrayList<>();
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUser);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getString("judul"),
                        rs.getString("tanggal_pelaksanaan"),
                        rs.getString("status_hadir"),    // bisa null
                        rs.getString("kode_sertifikat")  // bisa null
                    });
                }
            }
        }
        return list;
    }

    /**
     * F2. Laporan panitia: jumlah pendaftar, kehadiran, sertifikat per seminar.
     * Return: List Object[]{judul, jumlah_pendaftar, jumlah_hadir, jumlah_sertifikat}
     */
    public List<Object[]> getLaporanPanitia(int idPanitia) throws SQLException {
        String sql =
            "SELECT " +
            "  se.judul, " +
            "  COUNT(pd.id_pendaftaran) AS jumlah_pendaftar, " +
            "  SUM(CASE WHEN pr.status_hadir = 'HADIR' THEN 1 ELSE 0 END) AS jumlah_hadir, " +
            "  COUNT(st.id_sertifikat) AS jumlah_sertifikat " +
            "FROM seminar se " +
            "LEFT JOIN pendaftaran pd ON se.id_seminar = pd.id_seminar " +
            "LEFT JOIN presensi pr ON pd.id_pendaftaran = pr.id_pendaftaran " +
            "LEFT JOIN sertifikat st ON pd.id_pendaftaran = st.id_pendaftaran " +
            "WHERE se.id_panitia = ? " +
            "GROUP BY se.id_seminar, se.judul";

        List<Object[]> list = new ArrayList<>();
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPanitia);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getString("judul"),
                        rs.getInt("jumlah_pendaftar"),
                        rs.getInt("jumlah_hadir"),
                        rs.getInt("jumlah_sertifikat")
                    });
                }
            }
        }
        return list;
    }
}
