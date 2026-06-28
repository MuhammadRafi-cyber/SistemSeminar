package util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * CsvExporter — utilitas ekspor data ke file CSV menggunakan Java standard library.
 * Tidak membutuhkan dependency eksternal (hanya FileWriter + BufferedWriter).
 * Referensi PRD: FR-027, TC-17.
 */
public class CsvExporter {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    /**
     * Ekspor data ke file CSV.
     *
     * @param filePath   Path lengkap file output, misal: "laporan_peserta_20260701.csv"
     * @param header     Array nama kolom, misal: {"Seminar", "Tanggal", "Status"}
     * @param rows       List baris data; setiap Object[] adalah satu baris
     * @throws IOException jika terjadi error saat menulis file
     */
    public static void ekspor(String filePath, String[] header, List<Object[]> rows)
            throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            // Tulis header
            bw.write(joinRow(header));
            bw.newLine();

            // Tulis data
            for (Object[] row : rows) {
                String[] cols = new String[row.length];
                for (int i = 0; i < row.length; i++) {
                    cols[i] = row[i] != null ? row[i].toString() : "";
                }
                bw.write(joinRow(cols));
                bw.newLine();
            }
        }
    }

    /**
     * Generate nama file CSV otomatis berdasarkan jenis laporan dan timestamp.
     * Contoh hasil: "laporan_peserta_20260701_093045.csv"
     */
    public static String generateNamaFile(String jenisLaporan) {
        String timestamp = LocalDateTime.now().format(FMT);
        return "laporan_" + jenisLaporan.toLowerCase().replace(" ", "_")
             + "_" + timestamp + ".csv";
    }

    /**
     * Gabungkan array string menjadi satu baris CSV.
     * Nilai yang mengandung koma atau tanda kutip akan di-escape.
     */
    private static String joinRow(String[] cols) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cols.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(escapeField(cols[i]));
        }
        return sb.toString();
    }

    /**
     * Escape field CSV: bungkus dengan kutip ganda jika mengandung koma, newline, atau kutip.
     */
    private static String escapeField(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
