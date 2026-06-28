package controller;

import enums.StatusHadir;
import exception.*;
import model.User;
import service.PresensiService;
import java.sql.SQLException;

public class PresensiController {
    private final PresensiService presensiService;

    public PresensiController(PresensiService presensiService) {
        this.presensiService = presensiService;
    }

    public String scanPresensi(User panitia, String kodeBooking) {
        try {
            StatusHadir h = presensiService.scanPresensi(panitia, kodeBooking);
            return "SUKSES|Presensi berhasil dicatat."
                 + " Tiket '" + kodeBooking.trim().toUpperCase() + "' → " + h;
        } catch (AksesDitolakException e) {
            return "ERROR|Akses ditolak: " + e.getMessage();
        } catch (InputKosongException e) {
            return "ERROR|" + e.getMessage();
        } catch (KodeBookingTidakValidException e) {
            return "ERROR|" + e.getMessage();
        } catch (PresensiDuplikatException e) {
            return "ERROR|" + e.getMessage();
        } catch (DataTidakDitemukanException e) {
            return "ERROR|" + e.getMessage();
        } catch (SQLException e) {
            return "ERROR|Gagal mencatat presensi: " + e.getMessage();
        } catch (Exception e) {
            return "ERROR|Kesalahan tidak terduga: " + e.getMessage();
        }
    }

    public String cekStatus(String kodeBooking) {
        try {
            StatusHadir s = presensiService.cekStatus(kodeBooking);
            String kode = kodeBooking.trim().toUpperCase();
            if (s == null) return "INFO|Tiket '" + kode + "' belum melakukan presensi.";
            return "INFO|Status presensi tiket '" + kode + "': " + s;
        } catch (InputKosongException e) {
            return "ERROR|" + e.getMessage();
        } catch (KodeBookingTidakValidException | DataTidakDitemukanException e) {
            return "ERROR|" + e.getMessage();
        } catch (SQLException e) {
            return "ERROR|Gagal cek status: " + e.getMessage();
        } catch (Exception e) {
            return "ERROR|Kesalahan tidak terduga: " + e.getMessage();
        }
    }

    public int hitungHadir(int idSeminar) {
        try { return presensiService.hitungHadir(idSeminar); }
        catch (SQLException e) { System.err.println("[ERROR] " + e.getMessage()); return 0; }
    }
}
