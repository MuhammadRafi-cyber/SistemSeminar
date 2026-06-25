package controller;

import enums.StatusHadir;
import exception.*;
import model.User;
import service.PresensiService;

import java.sql.SQLException;

/**
 * PresensiController — Bridge View ↔ PresensiService.
 * Input: id_pendaftaran (sesuai DB tim).
 */
public class PresensiController {

    private final PresensiService presensiService;

    public PresensiController(PresensiService presensiService) {
        this.presensiService = presensiService;
    }

    public String scanPresensi(User panitia, int idPendaftaran) {
        try {
            StatusHadir hasil = presensiService.scanPresensi(panitia, idPendaftaran);
            return "SUKSES|Presensi berhasil dicatat. Status: " + hasil;
        } catch (AksesDitolakException | DataTidakDitemukanException
                | PresensiDuplikatException e) {
            return "ERROR|" + e.getMessage();
        } catch (SQLException e) {
            return "ERROR|Gagal mencatat presensi. Cek koneksi database.";
        }
    }

    public String cekStatus(int idPendaftaran) {
        try {
            StatusHadir status = presensiService.cekStatus(idPendaftaran);
            if (status == null) return "INFO|Peserta belum melakukan presensi.";
            return "INFO|Status presensi: " + status;
        } catch (SQLException e) {
            return "ERROR|Gagal mengambil status presensi.";
        }
    }

    public int hitungHadir(int idSeminar) {
        try { return presensiService.hitungHadir(idSeminar); }
        catch (SQLException e) { return 0; }
    }
}
