package service;

import dao.LaporanDAO;
import model.User;
import java.sql.SQLException;
import java.util.List;

/**
 * LaporanService — format laporan F1 (peserta) dan F2 (panitia).
 * Query disesuaikan dengan DB v4:
 *   - JOIN via id_pemesan (bukan id_user di pendaftaran)
 *   - presensi.status (bukan status_hadir)
 *   - sertifikat.nomor (bukan kode_sertifikat)
 *   - seminar.tanggal_mulai DATETIME
 */
public class LaporanService {
    private final LaporanDAO laporanDAO;

    public LaporanService(LaporanDAO laporanDAO) {
        this.laporanDAO = laporanDAO;
    }

    public List<Object[]> getLaporanPeserta(int idUser) throws SQLException {
        return laporanDAO.getLaporanPeserta(idUser);
    }

    public List<Object[]> getLaporanPanitia(int idPanitia) throws SQLException {
        return laporanDAO.getLaporanPanitia(idPanitia);
    }

    /**
     * Format laporan peserta sebagai String tabel (untuk tampil di console).
     * Kolom: Seminar | Tanggal | Nama Tiket | Kehadiran | Sertifikat
     */
    public String formatLaporanPeserta(User user) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n=== LAPORAN RIWAYAT SEMINAR PESERTA ===\n");
        sb.append(String.format("Peserta : %s <%s>%n", user.getNama(), user.getEmail()));
        String sep = "─".repeat(80);
        sb.append(sep).append("\n");
        sb.append(String.format("%-28s %-12s %-18s %-10s %-20s%n",
            "Seminar", "Tanggal", "Nama Peserta", "Hadir", "Nomor Sertifikat"));
        sb.append(sep).append("\n");

        try {
            List<Object[]> rows = laporanDAO.getLaporanPeserta(user.getIdUser());
            if (rows.isEmpty()) {
                sb.append("  (Belum ada riwayat pendaftaran seminar)\n");
            } else {
                for (Object[] r : rows) {
                    String judul  = truncate(str(r[0]), 26);
                    String tgl    = str(r[1]).length() > 10 ? str(r[1]).substring(0, 10) : str(r[1]);
                    String nama   = truncate(str(r[2]), 16);
                    String hadir  = r[3] != null ? str(r[3]) : "BELUM";
                    String nomSer = r[4] != null ? str(r[4]) : "BELUM";
                    sb.append(String.format("%-28s %-12s %-18s %-10s %-20s%n",
                        judul, tgl, nama, hadir, nomSer));
                }
            }
        } catch (SQLException e) {
            sb.append("[ERROR] Gagal mengambil data laporan: ").append(e.getMessage()).append("\n");
        }

        sb.append(sep).append("\n");
        return sb.toString();
    }

    /**
     * Format laporan panitia sebagai String tabel (untuk tampil di console).
     * Kolom: Seminar | Tanggal | Pendaftar | Tiket | Hadir | Sertifikat
     */
    public String formatLaporanPanitia(User user) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n=== LAPORAN SEMINAR PANITIA ===\n");
        sb.append(String.format("Panitia : %s%n", user.getNama()));
        String sep = "─".repeat(80);
        sb.append(sep).append("\n");
        sb.append(String.format("%-28s %-12s %10s %7s %7s %10s%n",
            "Seminar", "Tanggal", "Pendaftar", "Tiket", "Hadir", "Sertifikat"));
        sb.append(sep).append("\n");

        try {
            List<Object[]> rows = laporanDAO.getLaporanPanitia(user.getIdUser());
            if (rows.isEmpty()) {
                sb.append("  (Belum ada seminar yang dikelola)\n");
            } else {
                int totalP = 0, totalT = 0, totalH = 0, totalS = 0;
                for (Object[] r : rows) {
                    String judul = truncate(str(r[0]), 26);
                    String tgl   = str(r[1]).length() > 10 ? str(r[1]).substring(0, 10) : str(r[1]);
                    int p = (int) r[2], t = (int) r[3], h = (int) r[4], s = (int) r[5];
                    sb.append(String.format("%-28s %-12s %10d %7d %7d %10d%n",
                        judul, tgl, p, t, h, s));
                    totalP += p; totalT += t; totalH += h; totalS += s;
                }
                sb.append(sep).append("\n");
                sb.append(String.format("%-41s %10d %7d %7d %10d%n",
                    "TOTAL", totalP, totalT, totalH, totalS));
            }
        } catch (SQLException e) {
            sb.append("[ERROR] Gagal mengambil data laporan: ").append(e.getMessage()).append("\n");
        }

        sb.append(sep).append("\n");
        return sb.toString();
    }

    private String str(Object o)                { return o != null ? o.toString() : "-"; }
    private String truncate(String s, int max)  { return s.length() > max ? s.substring(0, max - 2) + ".." : s; }
}
