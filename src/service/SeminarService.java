package service;

import dao.AuditLogDAO;
import dao.SeminarDAO;
import enums.ModeSeminar;
import enums.Role;
import enums.StatusSeminar;
import exception.*;
import model.Seminar;
import model.User;
import util.Validator;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class SeminarService {
    private final SeminarDAO  seminarDAO;
    private final AuditLogDAO auditLogDAO;

    public SeminarService(SeminarDAO seminarDAO, AuditLogDAO auditLogDAO) {
        this.seminarDAO  = seminarDAO;
        this.auditLogDAO = auditLogDAO;
    }

    public List<Seminar> getAll()               throws SQLException { return seminarDAO.getAll(); }
    public List<Seminar> getDibuka()            throws SQLException { return seminarDAO.getDibuka(); }
    public List<Seminar> getMilikku(int id)     throws SQLException { return seminarDAO.getByPanitia(id); }
    public List<Seminar> getByInstitusi(int id) throws SQLException { return seminarDAO.getByInstitusi(id); }
    public List<Seminar> getByKategori(int id)  throws SQLException { return seminarDAO.getByKategori(id); }

    public Seminar getById(int id) throws DataTidakDitemukanException, SQLException {
        return seminarDAO.getById(id);
    }

    public Seminar tambah(User panitia, int idInstitusi, Integer idKategori,
                           String judul, String deskripsi, String pembicara,
                           LocalDateTime tanggalMulai, LocalDateTime tanggalSelesai,
                           ModeSeminar mode, String lokasi, int kuota, double harga)
            throws InputKosongException, KuotaTidakValidException, HargaTidakValidException,
                   TanggalTidakValidException, AksesDitolakException, SQLException {

        if (panitia.getRole() == Role.PESERTA)
            throw new AksesDitolakException("Hanya Panitia/Admin yang dapat membuat seminar.");
        Validator.cekTidakKosong(judul, "Judul Seminar");
        Validator.cekTidakKosong(lokasi, "Lokasi");
        Validator.cekKuota(kuota);
        Validator.cekHarga(harga);
        Validator.cekTanggal(tanggalMulai, tanggalSelesai);

        Seminar s = new Seminar(idInstitusi, panitia.getIdUser(), idKategori,
            judul, deskripsi, pembicara, tanggalMulai, tanggalSelesai, mode, lokasi, kuota, harga);
        seminarDAO.insert(s);
        auditLogDAO.log(panitia.getIdUser(), "TAMBAH_SEMINAR", "seminar",
            s.getIdSeminar(), "Buat seminar: " + judul);
        return s;
    }

    public boolean edit(User panitia, Seminar seminar)
            throws AksesDitolakException, InputKosongException, KuotaTidakValidException,
                   HargaTidakValidException, TanggalTidakValidException, SQLException {
        cekKepemilikan(panitia, seminar);
        if (seminar.getStatus() == StatusSeminar.SELESAI)
            throw new AksesDitolakException("Seminar yang sudah SELESAI tidak dapat diedit.");
        Validator.cekTidakKosong(seminar.getJudul(), "Judul Seminar");
        Validator.cekKuota(seminar.getKuota());
        Validator.cekHarga(seminar.getHarga());
        Validator.cekTanggal(seminar.getTanggalMulai(), seminar.getTanggalSelesai());
        boolean ok = seminarDAO.update(seminar);
        if (ok) auditLogDAO.log(panitia.getIdUser(), "EDIT_SEMINAR", "seminar",
            seminar.getIdSeminar(), "Edit seminar: " + seminar.getJudul());
        return ok;
    }

    /**
     * Hapus seminar.
     * BR-07 & 9.1: Jika ada peserta aktif → soft delete (status CANCELLED).
     *              Jika belum ada peserta → hard delete.
     */
    public boolean hapus(User panitia, int idSeminar)
            throws AksesDitolakException, DataTidakDitemukanException, SQLException {
        Seminar s = getById(idSeminar);
        cekKepemilikan(panitia, s);
        boolean ok;
        String jenisHapus;
        if (seminarDAO.punyaPeserta(idSeminar)) {
            ok = seminarDAO.softDelete(idSeminar);
            jenisHapus = "CANCELLED (soft delete — ada peserta)";
        } else {
            ok = seminarDAO.hardDelete(idSeminar);
            jenisHapus = "dihapus permanen (tidak ada peserta)";
        }
        if (ok) auditLogDAO.log(panitia.getIdUser(), "HAPUS_SEMINAR", "seminar",
            idSeminar, "Seminar '" + s.getJudul() + "' " + jenisHapus);
        return ok;
    }

    public boolean selesaikan(User panitia, int idSeminar)
            throws AksesDitolakException, DataTidakDitemukanException, SQLException {
        Seminar s = getById(idSeminar);
        cekKepemilikan(panitia, s);
        if (s.getStatus() == StatusSeminar.SELESAI)
            throw new AksesDitolakException("Seminar sudah berstatus SELESAI.");
        boolean ok = seminarDAO.selesaikan(idSeminar);
        if (ok) auditLogDAO.log(panitia.getIdUser(), "SELESAIKAN_SEMINAR", "seminar",
            idSeminar, "Seminar '" + s.getJudul() + "' ditandai SELESAI");
        return ok;
    }

    private void cekKepemilikan(User panitia, Seminar s) throws AksesDitolakException {
        if (panitia.getRole() == Role.ADMIN) return;
        // BR-05: Panitia hanya bisa kelola seminar institusinya atau yang dibuatnya
        if (s.getIdPanitia() != panitia.getIdUser()
                && (panitia.getIdInstitusi() == null
                    || s.getIdInstitusi() != panitia.getIdInstitusi()))
            throw new AksesDitolakException(
                "Anda tidak berhak mengelola seminar milik panitia/institusi lain.");
    }
}
