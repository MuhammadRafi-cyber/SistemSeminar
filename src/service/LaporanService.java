package service;

import dao.LaporanDAO;
import model.User;
import util.CsvExporter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class LaporanService {
    private final LaporanDAO laporanDAO;

    public LaporanService(LaporanDAO laporanDAO) { this.laporanDAO = laporanDAO; }

    public List<Object[]> getLaporanPeserta(int idUser) throws SQLException {
        return laporanDAO.getLaporanPeserta(idUser);
    }
    public List<Object[]> getLaporanPanitia(int idPanitia) throws SQLException {
        return laporanDAO.getLaporanPanitia(idPanitia);
    }

    /** F1: format laporan peserta sebagai tabel string. */
    public String formatLaporanPeserta(User user) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n=== LAPORAN RIWAYAT SEMINAR PESERTA ===\n");
        sb.append(String.format("Peserta  : %s <%s>%n", user.getNama(), user.getEmail()));
        if (user.getUsername() != null)
            sb.append(String.format("Username : @%s%n", user.getUsername()));
        String sep = "─".repeat(88);
        sb.append(sep).append("\n");
        sb.append(String.format("%-28s %-12s %-18s %-15s %-10s %-15s%n",
            "Seminar","Tanggal","Nama Peserta","Kode Booking","Hadir","Sertifikat"));
        sb.append(sep).append("\n");
        try {
            List<Object[]> rows = laporanDAO.getLaporanPeserta(user.getIdUser());
            if (rows.isEmpty()) {
                sb.append("  (Belum ada riwayat pendaftaran seminar)\n");
            } else {
                for (Object[] r : rows) {
                    String tgl = str(r[1]); if (tgl.length() > 10) tgl = tgl.substring(0, 10);
                    sb.append(String.format("%-28s %-12s %-18s %-15s %-10s %-15s%n",
                        trunc(str(r[0]), 26), tgl, trunc(str(r[2]), 16),
                        str(r[3]), r[4] != null ? str(r[4]) : "BELUM",
                        r[5] != null ? str(r[5]) : "BELUM"));
                }
            }
        } catch (SQLException e) {
            sb.append("[ERROR] ").append(e.getMessage()).append("\n");
        }
        sb.append(sep).append("\n");
        return sb.toString();
    }

    /** F2: format laporan panitia sebagai tabel string. */
    public String formatLaporanPanitia(User user) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n=== LAPORAN SEMINAR PANITIA ===\n");
        sb.append(String.format("Panitia  : %s%n", user.getNama()));
        String sep = "─".repeat(88);
        sb.append(sep).append("\n");
        sb.append(String.format("%-28s %-12s %8s %6s %6s %8s %6s %8s%n",
            "Seminar","Tanggal","Pendaftar","Tiket","Hadir","TdkHadir","Batal","Sertifikat"));
        sb.append(sep).append("\n");
        try {
            List<Object[]> rows = laporanDAO.getLaporanPanitia(user.getIdUser());
            if (rows.isEmpty()) {
                sb.append("  (Belum ada seminar yang dikelola)\n");
            } else {
                int tP=0, tT=0, tH=0, tTH=0, tB=0, tS=0;
                for (Object[] r : rows) {
                    String tgl = str(r[1]); if (tgl.length() > 10) tgl = tgl.substring(0, 10);
                    int p=(int)r[2], t=(int)r[3], h=(int)r[4],
                        th=(int)r[5], b=(int)r[6], s=(int)r[7];
                    sb.append(String.format("%-28s %-12s %8d %6d %6d %8d %6d %8d%n",
                        trunc(str(r[0]), 26), tgl, p, t, h, th, b, s));
                    tP+=p; tT+=t; tH+=h; tTH+=th; tB+=b; tS+=s;
                }
                sb.append(sep).append("\n");
                sb.append(String.format("%-41s %8d %6d %6d %8d %6d %8d%n",
                    "TOTAL", tP, tT, tH, tTH, tB, tS));
            }
        } catch (SQLException e) {
            sb.append("[ERROR] ").append(e.getMessage()).append("\n");
        }
        sb.append(sep).append("\n");
        return sb.toString();
    }

    /**
     * FR-027: Ekspor laporan peserta ke CSV.
     * @param filePath path file output (misal "laporan_peserta.csv")
     * @return path file yang berhasil ditulis
     */
    public String eksporLaporanPesertaCsv(int idUser, String filePath)
            throws SQLException, IOException {
        List<Object[]> rows = laporanDAO.getLaporanPeserta(idUser);
        String[] header = {"Seminar","Tanggal Mulai","Nama Peserta",
                           "Kode Booking","Status Hadir","Nomor Sertifikat"};
        if (filePath == null || filePath.isEmpty())
            filePath = CsvExporter.generateNamaFile("peserta");
        CsvExporter.ekspor(filePath, header, rows);
        return filePath;
    }

    /**
     * FR-027: Ekspor laporan panitia ke CSV.
     * @param filePath path file output
     * @return path file yang berhasil ditulis
     */
    public String eksporLaporanPanitia(int idPanitia, String filePath)
            throws SQLException, IOException {
        List<Object[]> rows = laporanDAO.getLaporanPanitia(idPanitia);
        String[] header = {"Seminar","Tanggal Mulai","Pendaftar","Tiket",
                           "Hadir","Tidak Hadir","Batal","Sertifikat"};
        if (filePath == null || filePath.isEmpty())
            filePath = CsvExporter.generateNamaFile("panitia");
        CsvExporter.ekspor(filePath, header, rows);
        return filePath;
    }

    private String str(Object o)          { return o != null ? o.toString() : "-"; }
    private String trunc(String s, int n) { return s.length() > n ? s.substring(0, n-2)+".." : s; }
}
