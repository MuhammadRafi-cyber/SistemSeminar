package service;

import dao.AuditLogDAO;
import dao.SeminarDAO;
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
    public List<Seminar> getMilikku(int idPan)  throws SQLException { return seminarDAO.getByPanitia(idPan); }
    public List<Seminar> getByInstitusi(int id) throws SQLException { return seminarDAO.getByInstitusi(id); }

    public Seminar getById(int id) throws DataTidakDitemukanException, SQLException {
        return seminarDAO.getById(id);  // DAO sudah throw DataTidakDitemukanException
    }

    /**
     * Tambah seminar baru.
     * @throws AksesDitolakException jika role PESERTA mencoba membuat seminar
     * @throws InputKosongException  jika judul/lokasi kosong
     * @throws KuotaTidakValidException jika kuota <= 0
     * @throws HargaTidakValidException jika harga negatif
     * @throws TanggalTidakValidException jika tanggal tidak valid
     */
    public Seminar tambah(User panitia, int idInstitusi, String judul, String deskripsi,
                           LocalDateTime tanggalMulai, LocalDateTime tanggalSelesai,
                           String lokasi, int kuota, double harga)
            throws InputKosongException, KuotaTidakValidException, HargaTidakValidException,
                   TanggalTidakValidException, AksesDitolakException, SQLException {

        if (panitia.getRole() == Role.PESERTA)
            throw new AksesDitolakException("Hanya Panitia/Admin yang dapat membuat seminar.");

        Validator.cekTidakKosong(judul, "Judul Seminar");
        Validator.cekTidakKosong(lokasi, "Lokasi");
        Validator.cekKuota(kuota);
        Validator.cekHarga(harga);
        Validator.cekTanggal(tanggalMulai, tanggalSelesai);

        Seminar s = new Seminar(idInstitusi, panitia.getIdUser(), judul, deskripsi,
                                tanggalMulai, tanggalSelesai, lokasi, kuota, harga);
        seminarDAO.insert(s);
        auditLogDAO.log(panitia.getIdUser(), "TAMBAH_SEMINAR", "seminar");
        return s;
    }

    /**
     * Edit seminar — hanya panitia pemilik atau Admin.
     * Seminar berstatus SELESAI tidak dapat diedit.
     */
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
        if (ok) auditLogDAO.log(panitia.getIdUser(), "EDIT_SEMINAR", "seminar");
        return ok;
    }

    /**
     * Hapus seminar (hard delete, sesuai DB v4 tidak ada soft delete).
     * Hanya panitia pemilik atau Admin.
     */
    public boolean hapus(User panitia, int idSeminar)
            throws AksesDitolakException, DataTidakDitemukanException, SQLException {
        Seminar s = getById(idSeminar);
        cekKepemilikan(panitia, s);
        boolean ok = seminarDAO.hapus(idSeminar);
        if (ok) auditLogDAO.log(panitia.getIdUser(), "HAPUS_SEMINAR", "seminar");
        return ok;
    }

    /**
     * Tandai seminar sebagai SELESAI.
     * Setelah SELESAI, laporan F2 dan sertifikat dapat diakses.
     */
    public boolean selesaikan(User panitia, int idSeminar)
            throws AksesDitolakException, DataTidakDitemukanException, SQLException {
        Seminar s = getById(idSeminar);
        cekKepemilikan(panitia, s);
        if (s.getStatus() == StatusSeminar.SELESAI)
            throw new AksesDitolakException("Seminar sudah berstatus SELESAI.");
        boolean ok = seminarDAO.selesaikan(idSeminar);
        if (ok) auditLogDAO.log(panitia.getIdUser(), "SELESAIKAN_SEMINAR", "seminar");
        return ok;
    }

    private void cekKepemilikan(User panitia, Seminar seminar) throws AksesDitolakException {
        if (panitia.getRole() == Role.ADMIN) return;
        if (seminar.getIdPanitia() != panitia.getIdUser())
            throw new AksesDitolakException("Anda tidak berhak mengelola seminar milik panitia lain.");
    }
}
