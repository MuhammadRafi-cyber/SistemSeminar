package controller;

import enums.ModeSeminar;
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
    public SeminarController(SeminarService s) { this.seminarService = s; }

    public List<Seminar> getSemuaSeminar()          { try { return seminarService.getAll(); }         catch (SQLException e) { err(e); return Collections.emptyList(); } }
    public List<Seminar> getSeminarDibuka()         { try { return seminarService.getDibuka(); }      catch (SQLException e) { err(e); return Collections.emptyList(); } }
    public List<Seminar> getSeminarSaya(int id)     { try { return seminarService.getMilikku(id); }  catch (SQLException e) { err(e); return Collections.emptyList(); } }
    public List<Seminar> getByKategori(int id)      { try { return seminarService.getByKategori(id);}catch (SQLException e) { err(e); return Collections.emptyList(); } }

    public Seminar getDetail(int id) {
        try { return seminarService.getById(id); }
        catch (DataTidakDitemukanException e) { System.err.println("[INFO] " + e.getMessage()); return null; }
        catch (SQLException e) { err(e); return null; }
    }

    public String tambahSeminar(User panitia, int idInstitusi, Integer idKategori,
                                 String judul, String deskripsi, String pembicara,
                                 LocalDateTime tanggalMulai, LocalDateTime tanggalSelesai,
                                 ModeSeminar mode, String lokasi, int kuota, double harga) {
        try {
            Seminar s = seminarService.tambah(panitia, idInstitusi, idKategori,
                judul, deskripsi, pembicara, tanggalMulai, tanggalSelesai, mode, lokasi, kuota, harga);
            return "SUKSES|Seminar '" + s.getJudul() + "' berhasil dibuat. ID: " + s.getIdSeminar();
        } catch (InputKosongException e)      { return "ERROR|Data tidak lengkap: " + e.getMessage();
        } catch (KuotaTidakValidException e)  { return "ERROR|Kuota tidak valid: " + e.getMessage();
        } catch (HargaTidakValidException e)  { return "ERROR|Harga tidak valid: " + e.getMessage();
        } catch (TanggalTidakValidException e){ return "ERROR|Tanggal tidak valid: " + e.getMessage();
        } catch (AksesDitolakException e)     { return "ERROR|Akses ditolak: " + e.getMessage();
        } catch (SQLException e)              { return "ERROR|Gagal simpan seminar: " + e.getMessage();
        } catch (Exception e)                 { return "ERROR|Kesalahan tidak terduga: " + e.getMessage(); }
    }

    public String editSeminar(User panitia, Seminar seminar) {
        try {
            boolean ok = seminarService.edit(panitia, seminar);
            return ok ? "SUKSES|Seminar berhasil diperbarui." : "ERROR|Tidak ada perubahan.";
        } catch (AksesDitolakException e)     { return "ERROR|Akses ditolak: " + e.getMessage();
        } catch (InputKosongException e)      { return "ERROR|Data tidak lengkap: " + e.getMessage();
        } catch (KuotaTidakValidException e)  { return "ERROR|" + e.getMessage();
        } catch (HargaTidakValidException e)  { return "ERROR|" + e.getMessage();
        } catch (TanggalTidakValidException e){ return "ERROR|" + e.getMessage();
        } catch (SQLException e)              { return "ERROR|Gagal update seminar: " + e.getMessage();
        } catch (Exception e)                 { return "ERROR|Kesalahan tidak terduga: " + e.getMessage(); }
    }

    public String hapusSeminar(User panitia, int idSeminar) {
        try {
            boolean ok = seminarService.hapus(panitia, idSeminar);
            return ok ? "SUKSES|Seminar berhasil dihapus/dibatalkan." : "ERROR|Gagal hapus seminar.";
        } catch (AksesDitolakException | DataTidakDitemukanException e) { return "ERROR|" + e.getMessage();
        } catch (SQLException e) { return "ERROR|Gagal hapus seminar: " + e.getMessage();
        } catch (Exception e) { return "ERROR|Kesalahan tidak terduga: " + e.getMessage(); }
    }

    public String selesaikanSeminar(User panitia, int idSeminar) {
        try {
            boolean ok = seminarService.selesaikan(panitia, idSeminar);
            return ok ? "SUKSES|Seminar ditandai SELESAI." : "ERROR|Gagal ubah status seminar.";
        } catch (AksesDitolakException | DataTidakDitemukanException e) { return "ERROR|" + e.getMessage();
        } catch (SQLException e) { return "ERROR|Gagal ubah status: " + e.getMessage();
        } catch (Exception e) { return "ERROR|Kesalahan tidak terduga: " + e.getMessage(); }
    }

    private void err(SQLException e) { System.err.println("[ERROR] " + e.getMessage()); }
}
