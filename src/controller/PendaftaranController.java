package controller;

import exception.*;
import model.Pendaftaran;
import service.PendaftaranService;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * PendaftaranController — Bridge View ↔ PendaftaranService.
 * Tidak ada detail tiket / payment — sesuai DB tim.
 */
public class PendaftaranController {

    private final PendaftaranService pendaftaranService;

    public PendaftaranController(PendaftaranService pendaftaranService) {
        this.pendaftaranService = pendaftaranService;
    }

    public String daftar(int idUser, int idSeminar) {
        try {
            Pendaftaran p = pendaftaranService.daftar(idUser, idSeminar);
            return "SUKSES|Pendaftaran berhasil! ID Pendaftaran: " + p.getIdPendaftaran()
                 + " | Status: " + p.getStatusPendaftaran();
        } catch (PendaftaranDuplikatException | KuotaPenuhException | DataTidakDitemukanException e) {
            return "ERROR|" + e.getMessage();
        } catch (SQLException e) {
            return "ERROR|Gagal memproses pendaftaran. Cek koneksi database.";
        }
    }

    public String batalkan(int idPendaftaran) {
        try {
            boolean ok = pendaftaranService.batalkan(idPendaftaran);
            return ok ? "SUKSES|Pendaftaran berhasil dibatalkan." : "ERROR|Gagal membatalkan.";
        } catch (DataTidakDitemukanException e) {
            return "ERROR|" + e.getMessage();
        } catch (SQLException e) {
            return "ERROR|Gagal membatalkan pendaftaran.";
        }
    }

    // C5: riwayat [{judul, tanggal, status, idPendaftaran}]
    public List<Object[]> getRiwayat(int idUser) {
        try { return pendaftaranService.getRiwayatPeserta(idUser); }
        catch (SQLException e) { return Collections.emptyList(); }
    }

    // C4: peserta seminar [{idPendaftaran, nama, email, tanggal, status}]
    public List<Object[]> getPesertaSeminar(int idSeminar) {
        try { return pendaftaranService.getPesertaSeminar(idSeminar); }
        catch (SQLException e) { return Collections.emptyList(); }
    }
}
