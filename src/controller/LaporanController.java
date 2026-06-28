package controller;

import model.User;
import service.LaporanService;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class LaporanController {
    private final LaporanService laporanService;

    public LaporanController(LaporanService laporanService) {
        this.laporanService = laporanService;
    }

    /** F1: laporan peserta siap cetak */
    public String getLaporanPeserta(User user) {
        return laporanService.formatLaporanPeserta(user);
    }

    /** F2: laporan panitia siap cetak */
    public String getLaporanPanitia(User user) {
        return laporanService.formatLaporanPanitia(user);
    }

    /** FR-027: ekspor laporan peserta ke CSV */
    public String eksporPesertaCsv(User user, String filePath) {
        try {
            String hasil = laporanService.eksporLaporanPesertaCsv(user.getIdUser(), filePath);
            return "SUKSES|Laporan berhasil diekspor ke: " + hasil;
        } catch (SQLException e) {
            return "ERROR|Gagal ambil data laporan: " + e.getMessage();
        } catch (IOException e) {
            return "ERROR|Gagal menulis file CSV: " + e.getMessage();
        } catch (Exception e) {
            return "ERROR|Kesalahan tidak terduga: " + e.getMessage();
        }
    }

    /** FR-027: ekspor laporan panitia ke CSV */
    public String eksporPanitia(User user, String filePath) {
        try {
            String hasil = laporanService.eksporLaporanPanitia(user.getIdUser(), filePath);
            return "SUKSES|Laporan berhasil diekspor ke: " + hasil;
        } catch (SQLException e) {
            return "ERROR|Gagal ambil data laporan: " + e.getMessage();
        } catch (IOException e) {
            return "ERROR|Gagal menulis file CSV: " + e.getMessage();
        } catch (Exception e) {
            return "ERROR|Kesalahan tidak terduga: " + e.getMessage();
        }
    }

    public List<Object[]> getRawLaporanPeserta(int idUser) {
        try { return laporanService.getLaporanPeserta(idUser); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public List<Object[]> getRawLaporanPanitia(int idPanitia) {
        try { return laporanService.getLaporanPanitia(idPanitia); }
        catch (Exception e) { return Collections.emptyList(); }
    }
}
