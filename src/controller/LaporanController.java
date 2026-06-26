package controller;

import model.User;
import service.LaporanService;
import java.util.Collections;
import java.util.List;

public class LaporanController {
    private final LaporanService laporanService;

    public LaporanController(LaporanService laporanService) {
        this.laporanService = laporanService;
    }

    /** F1: laporan peserta sebagai String siap cetak ke console */
    public String getLaporanPeserta(User user) {
        return laporanService.formatLaporanPeserta(user);
    }

    /** F2: laporan panitia sebagai String siap cetak ke console */
    public String getLaporanPanitia(User user) {
        return laporanService.formatLaporanPanitia(user);
    }

    /** F1 raw data — untuk tim Frontend yang butuh List */
    public List<Object[]> getRawLaporanPeserta(int idUser) {
        try { return laporanService.getLaporanPeserta(idUser); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    /** F2 raw data — untuk tim Frontend yang butuh List */
    public List<Object[]> getRawLaporanPanitia(int idPanitia) {
        try { return laporanService.getLaporanPanitia(idPanitia); }
        catch (Exception e) { return Collections.emptyList(); }
    }
}
