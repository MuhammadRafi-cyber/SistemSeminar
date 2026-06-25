package service;

import dao.LaporanDAO;
import model.User;

import java.sql.SQLException;
import java.util.List;

/**
 * LaporanService — business logic laporan F1 dan F2.
 * generateLaporan() di model Peserta/Panitia memanggil service ini.
 */
public class LaporanService {

    private final LaporanDAO laporanDAO;

    public LaporanService(LaporanDAO laporanDAO) {
        this.laporanDAO = laporanDAO;
    }

    /**
     * F1. Laporan riwayat peserta.
     * Return: List Object[]{judul, tanggal_pelaksanaan, status_hadir, kode_sertifikat}
     */
    public List<Object[]> getLaporanPeserta(int idUser) throws SQLException {
        return laporanDAO.getLaporanPeserta(idUser);
    }

    /**
     * F2. Laporan seminar per panitia.
     * Return: List Object[]{judul, jumlah_pendaftar, jumlah_hadir, jumlah_sertifikat}
     */
    public List<Object[]> getLaporanPanitia(int idPanitia) throws SQLException {
        return laporanDAO.getLaporanPanitia(idPanitia);
    }

    /**
     * Format laporan peserta sebagai String (untuk generateLaporan() di model).
     */
    public String formatLaporanPeserta(User user) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== LAPORAN RIWAYAT SEMINAR ===\n");
        sb.append(String.format("Peserta : %s <%s>%n", user.getNama(), user.getEmail()));
        sb.append("─".repeat(65)).append("\n");
        sb.append(String.format("%-35s %-12s %-12s %-15s%n",
            "Seminar", "Tanggal", "Kehadiran", "Sertifikat"));
        sb.append("─".repeat(65)).append("\n");

        try {
            List<Object[]> rows = laporanDAO.getLaporanPeserta(user.getIdUser());
            if (rows.isEmpty()) {
                sb.append("Belum ada riwayat pendaftaran seminar.\n");
            } else {
                for (Object[] row : rows) {
                    String judul      = row[0] != null ? row[0].toString() : "-";
                    String tgl        = row[1] != null ? row[1].toString() : "-";
                    String hadir      = row[2] != null ? row[2].toString() : "BELUM";
                    String sertifikat = row[3] != null ? row[3].toString() : "BELUM";

                    // Potong judul jika terlalu panjang
                    if (judul.length() > 33) judul = judul.substring(0, 30) + "...";

                    sb.append(String.format("%-35s %-12s %-12s %-15s%n",
                        judul, tgl, hadir, sertifikat));
                }
            }
        } catch (SQLException e) {
            sb.append("[ERROR] Gagal mengambil data laporan: ").append(e.getMessage());
        }

        sb.append("─".repeat(65)).append("\n");
        return sb.toString();
    }

    /**
     * Format laporan panitia sebagai String (untuk generateLaporan() di model).
     */
    public String formatLaporanPanitia(User user) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== LAPORAN SEMINAR PANITIA ===\n");
        sb.append(String.format("Panitia : %s <%s>%n", user.getNama(), user.getEmail()));
        sb.append("─".repeat(65)).append("\n");
        sb.append(String.format("%-35s %10s %10s %12s%n",
            "Seminar", "Pendaftar", "Hadir", "Sertifikat"));
        sb.append("─".repeat(65)).append("\n");

        try {
            List<Object[]> rows = laporanDAO.getLaporanPanitia(user.getIdUser());
            if (rows.isEmpty()) {
                sb.append("Belum ada seminar yang dikelola.\n");
            } else {
                int totalPendaftar = 0, totalHadir = 0, totalSertifikat = 0;
                for (Object[] row : rows) {
                    String judul = row[0] != null ? row[0].toString() : "-";
                    int pendaftar  = (int) row[1];
                    int hadir      = (int) row[2];
                    int sertifikat = (int) row[3];

                    if (judul.length() > 33) judul = judul.substring(0, 30) + "...";

                    sb.append(String.format("%-35s %10d %10d %12d%n",
                        judul, pendaftar, hadir, sertifikat));

                    totalPendaftar  += pendaftar;
                    totalHadir      += hadir;
                    totalSertifikat += sertifikat;
                }
                sb.append("─".repeat(65)).append("\n");
                sb.append(String.format("%-35s %10d %10d %12d%n",
                    "TOTAL", totalPendaftar, totalHadir, totalSertifikat));
            }
        } catch (SQLException e) {
            sb.append("[ERROR] Gagal mengambil data laporan: ").append(e.getMessage());
        }

        sb.append("─".repeat(65)).append("\n");
        return sb.toString();
    }
}
