package controller;

import exception.*;
import model.Seminar;
import model.User;
import service.SeminarService;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class SeminarController {
    private final SeminarService seminarService;

    public SeminarController(SeminarService seminarService) {
        this.seminarService = seminarService;
    }

    public List<Seminar> getSemuaSeminar() {
        try { return seminarService.getAll(); }
        catch (SQLException e) {
            System.err.println("[ERROR] Gagal load seminar: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<Seminar> getSeminarDibuka() {
        try { return seminarService.getDibuka(); }
        catch (SQLException e) {
            System.err.println("[ERROR] Gagal load seminar: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<Seminar> getSeminarSaya(int idPanitia) {
        try { return seminarService.getMilikku(idPanitia); }
        catch (SQLException e) {
            System.err.println("[ERROR] Gagal load seminar: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /** @return Seminar atau null jika tidak ditemukan */
    public Seminar getDetail(int idSeminar) {
        try { return seminarService.getById(idSeminar); }
        catch (DataTidakDitemukanException e) {
            System.err.println("[INFO] " + e.getMessage());
            return null;
        } catch (SQLException e) {
            System.err.println("[ERROR] Gagal load detail seminar: " + e.getMessage());
            return null;
        }
    }

    public String tambahSeminar(User panitia, int idInstitusi, String judul, String deskripsi,
                                 LocalDateTime tanggalMulai, LocalDateTime tanggalSelesai,
                                 String lokasi, int kuota, double harga) {
        try {
            Seminar s = seminarService.tambah(panitia, idInstitusi, judul, deskripsi,
                                              tanggalMulai, tanggalSelesai, lokasi, kuota, harga);
            return "SUKSES|Seminar '" + s.getJudul() + "' berhasil dibuat. ID: " + s.getIdSeminar();
        } catch (InputKosongException e) {
            return "ERROR|Data tidak lengkap: " + e.getMessage();
        } catch (KuotaTidakValidException e) {
            return "ERROR|Kuota tidak valid: " + e.getMessage();
        } catch (HargaTidakValidException e) {
            return "ERROR|Harga tidak valid: " + e.getMessage();
        } catch (TanggalTidakValidException e) {
            return "ERROR|Tanggal tidak valid: " + e.getMessage();
        } catch (AksesDitolakException e) {
            return "ERROR|Akses ditolak: " + e.getMessage();
        } catch (SQLException e) {
            return "ERROR|Gagal menyimpan seminar ke database. Detail: " + e.getMessage();
        } catch (Exception e) {
            return "ERROR|Terjadi kesalahan tidak terduga: " + e.getMessage();
        }
    }

    public String editSeminar(User panitia, Seminar seminar) {
        try {
            boolean ok = seminarService.edit(panitia, seminar);
            return ok ? "SUKSES|Seminar berhasil diperbarui."
                      : "ERROR|Tidak ada perubahan yang tersimpan.";
        } catch (AksesDitolakException e) {
            return "ERROR|Akses ditolak: " + e.getMessage();
        } catch (InputKosongException e) {
            return "ERROR|Data tidak lengkap: " + e.getMessage();
        } catch (KuotaTidakValidException e) {
            return "ERROR|Kuota tidak valid: " + e.getMessage();
        } catch (HargaTidakValidException e) {
            return "ERROR|Harga tidak valid: " + e.getMessage();
        } catch (TanggalTidakValidException e) {
            return "ERROR|Tanggal tidak valid: " + e.getMessage();
        } catch (SQLException e) {
            return "ERROR|Gagal memperbarui seminar. Detail: " + e.getMessage();
        } catch (Exception e) {
            return "ERROR|Terjadi kesalahan tidak terduga: " + e.getMessage();
        }
    }

    public String hapusSeminar(User panitia, int idSeminar) {
        try {
            boolean ok = seminarService.hapus(panitia, idSeminar);
            return ok ? "SUKSES|Seminar berhasil dihapus."
                      : "ERROR|Gagal menghapus seminar.";
        } catch (AksesDitolakException e) {
            return "ERROR|Akses ditolak: " + e.getMessage();
        } catch (DataTidakDitemukanException e) {
            return "ERROR|" + e.getMessage();
        } catch (SQLException e) {
            return "ERROR|Gagal menghapus seminar. Detail: " + e.getMessage();
        } catch (Exception e) {
            return "ERROR|Terjadi kesalahan tidak terduga: " + e.getMessage();
        }
    }

    public String selesaikanSeminar(User panitia, int idSeminar) {
        try {
            boolean ok = seminarService.selesaikan(panitia, idSeminar);
            return ok ? "SUKSES|Seminar berhasil ditandai SELESAI. Laporan F2 kini dapat diakses."
                      : "ERROR|Gagal mengubah status seminar.";
        } catch (AksesDitolakException e) {
            return "ERROR|Akses ditolak: " + e.getMessage();
        } catch (DataTidakDitemukanException e) {
            return "ERROR|" + e.getMessage();
        } catch (SQLException e) {
            return "ERROR|Gagal mengubah status seminar. Detail: " + e.getMessage();
        } catch (Exception e) {
            return "ERROR|Terjadi kesalahan tidak terduga: " + e.getMessage();
        }
    }
}
