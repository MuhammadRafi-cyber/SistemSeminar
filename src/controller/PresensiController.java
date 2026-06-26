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
            StatusHadir hasil = presensiService.scanPresensi(panitia, kodeBooking);
            return "SUKSES|Presensi berhasil dicatat. Tiket '" + kodeBooking.trim().toUpperCase()
                 + "' → Status: " + hasil;
        } catch (AksesDitolakException e) {
            return "ERROR|Akses ditolak: " + e.getMessage();
        } catch (InputKosongException e) {
            return "ERROR|Input tidak valid: " + e.getMessage();
        } catch (DataTidakDitemukanException e) {
            return "ERROR|Tiket tidak ditemukan: " + e.getMessage();
        } catch (PresensiDuplikatException e) {
            return "ERROR|Presensi duplikat: " + e.getMessage();
        } catch (SQLException e) {
            return "ERROR|Gagal mencatat presensi. Cek koneksi database. Detail: " + e.getMessage();
        } catch (Exception e) {
            return "ERROR|Terjadi kesalahan tidak terduga: " + e.getMessage();
        }
    }

    public String cekStatus(String kodeBooking) {
        try {
            StatusHadir status = presensiService.cekStatus(kodeBooking);
            if (status == null)
                return "INFO|Tiket '" + kodeBooking.trim().toUpperCase() + "' belum melakukan presensi.";
            return "INFO|Status presensi tiket '" + kodeBooking.trim().toUpperCase() + "': " + status;
        } catch (InputKosongException e) {
            return "ERROR|Input tidak valid: " + e.getMessage();
        } catch (DataTidakDitemukanException e) {
            return "ERROR|" + e.getMessage();
        } catch (SQLException e) {
            return "ERROR|Gagal cek status presensi. Detail: " + e.getMessage();
        } catch (Exception e) {
            return "ERROR|Terjadi kesalahan tidak terduga: " + e.getMessage();
        }
    }

    public int hitungHadir(int idSeminar) {
        try { return presensiService.hitungHadir(idSeminar); }
        catch (SQLException e) {
            System.err.println("[ERROR] Gagal hitung hadir: " + e.getMessage());
            return 0;
        }
    }
}
