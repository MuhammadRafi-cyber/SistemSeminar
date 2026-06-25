package controller;

import exception.*;
import model.Seminar;
import model.User;
import service.SeminarService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

/**
 * SeminarController — Bridge View ↔ SeminarService.
 * B5 sekarang hapus (hard delete).
 */
public class SeminarController {

    private final SeminarService seminarService;

    public SeminarController(SeminarService seminarService) {
        this.seminarService = seminarService;
    }

    public List<Seminar> getSemuaSeminar() {
        try { return seminarService.getAll(); }
        catch (SQLException e) { return Collections.emptyList(); }
    }

    public List<Seminar> getSeminarDibuka() {
        try { return seminarService.getDibuka(); }
        catch (SQLException e) { return Collections.emptyList(); }
    }

    public List<Seminar> getSeminarSaya(int idPanitia) {
        try { return seminarService.getMilikku(idPanitia); }
        catch (SQLException e) { return Collections.emptyList(); }
    }

    public Seminar getDetail(int idSeminar) {
        try { return seminarService.getById(idSeminar); }
        catch (DataTidakDitemukanException | SQLException e) { return null; }
    }

    public String tambahSeminar(User panitia, String judul, String deskripsi,
                                 LocalDate tanggal, LocalTime mulai, LocalTime selesai,
                                 String lokasi, int kuota, double harga) {
        try {
            Seminar s = seminarService.tambahSeminar(panitia, judul, deskripsi,
                                                      tanggal, mulai, selesai, lokasi, kuota, harga);
            return "SUKSES|Seminar '" + s.getJudul() + "' berhasil dibuat. ID: " + s.getIdSeminar();
        } catch (InputKosongException | KuotaTidakValidException
                | TanggalTidakValidException | AksesDitolakException e) {
            return "ERROR|" + e.getMessage();
        } catch (SQLException e) {
            return "ERROR|Gagal menyimpan seminar ke database.";
        }
    }

    public String editSeminar(User panitia, Seminar seminar) {
        try {
            boolean ok = seminarService.editSeminar(panitia, seminar);
            return ok ? "SUKSES|Seminar berhasil diperbarui." : "ERROR|Tidak ada perubahan.";
        } catch (AksesDitolakException | TanggalTidakValidException
                | KuotaTidakValidException | InputKosongException e) {
            return "ERROR|" + e.getMessage();
        } catch (SQLException e) {
            return "ERROR|Gagal memperbarui seminar.";
        }
    }

    // B5: hard delete
    public String hapusSeminar(User panitia, int idSeminar) {
        try {
            boolean ok = seminarService.hapusSeminar(panitia, idSeminar);
            return ok ? "SUKSES|Seminar berhasil dihapus." : "ERROR|Gagal menghapus seminar.";
        } catch (AksesDitolakException | DataTidakDitemukanException e) {
            return "ERROR|" + e.getMessage();
        } catch (SQLException e) {
            return "ERROR|Gagal menghapus seminar.";
        }
    }

    public String selesaikanSeminar(User panitia, int idSeminar) {
        try {
            boolean ok = seminarService.selesaikanSeminar(panitia, idSeminar);
            return ok ? "SUKSES|Seminar berhasil ditandai SELESAI. Laporan dan sertifikat kini bisa diakses."
                    : "ERROR|Gagal mengubah status seminar.";
        } catch (AksesDitolakException | DataTidakDitemukanException e) {
            return "ERROR|" + e.getMessage();
        } catch (Exception e) {
            return "ERROR|Gagal mengubah status seminar.";
        }
    }
}


