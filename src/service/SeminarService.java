package service;

import dao.SeminarDAO;
import enums.Role;
import enums.StatusSeminar;
import exception.*;
import model.Seminar;
import model.User;
import util.Validator;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * SeminarService — business logic CRUD seminar.
 * B5 sekarang hard delete (sesuai DB tim).
 */
public class SeminarService {

    private final SeminarDAO seminarDAO;

    public SeminarService(SeminarDAO seminarDAO) {
        this.seminarDAO = seminarDAO;
    }

    public List<Seminar> getAll()                throws SQLException { return seminarDAO.getAll(); }
    public List<Seminar> getDibuka()             throws SQLException { return seminarDAO.getDibuka(); }
    public List<Seminar> getMilikku(int idPan)   throws SQLException { return seminarDAO.getByPanitia(idPan); }

    public Seminar getById(int id) throws DataTidakDitemukanException, SQLException {
        Seminar s = seminarDAO.getById(id);
        if (s == null) throw new DataTidakDitemukanException("Seminar", id);
        return s;
    }

    public Seminar tambahSeminar(User panitia, String judul, String deskripsi,
                                  LocalDate tanggal, LocalTime mulai, LocalTime selesai,
                                  String lokasi, int kuota, double harga)
            throws InputKosongException, KuotaTidakValidException,
                   TanggalTidakValidException, AksesDitolakException, SQLException {

        if (panitia.getRole() == Role.PESERTA)
            throw new AksesDitolakException("Hanya Panitia/Admin yang bisa membuat seminar.");

        Validator.cekTidakKosong(judul, "Judul Seminar");
        Validator.cekTidakKosong(lokasi, "Lokasi");
        Validator.cekKuota(kuota);
        Validator.cekTanggal(tanggal, tanggal);

        Seminar s = new Seminar(judul, deskripsi, tanggal, mulai, selesai,
                                lokasi, kuota, harga, panitia.getIdUser());
        seminarDAO.insert(s);
        return s;
    }

    public boolean editSeminar(User panitia, Seminar seminar)
            throws AksesDitolakException, TanggalTidakValidException,
                   KuotaTidakValidException, InputKosongException, SQLException {
        validasiKepemilikan(panitia, seminar);
        if (seminar.getStatus() == StatusSeminar.SELESAI)
            throw new AksesDitolakException("Seminar yang sudah selesai tidak dapat diedit.");
        Validator.cekTidakKosong(seminar.getJudul(), "Judul Seminar");
        Validator.cekKuota(seminar.getKuota());
        Validator.cekTanggal(seminar.getTanggalPelaksanaan(), seminar.getTanggalPelaksanaan());
        return seminarDAO.update(seminar);
    }

    // B5: hard delete (sesuai DB tim)
    public boolean hapusSeminar(User panitia, int idSeminar)
            throws AksesDitolakException, DataTidakDitemukanException, SQLException {
        Seminar s = getById(idSeminar);
        validasiKepemilikan(panitia, s);
        return seminarDAO.hapus(idSeminar);
    }

    /**
     * Tandai seminar sebagai SELESAI.
     * Hanya bisa dilakukan oleh panitia pemilik seminar atau Admin.
     */
    public boolean selesaikanSeminar(User panitia, int idSeminar)
            throws AksesDitolakException, DataTidakDitemukanException, SQLException {
        Seminar s = getById(idSeminar);
        validasiKepemilikan(panitia, s);
        if (s.getStatus() == StatusSeminar.SELESAI)
            throw new AksesDitolakException("Seminar sudah berstatus SELESAI.");
        return seminarDAO.selesaikan(idSeminar);
    }

    private void validasiKepemilikan(User panitia, Seminar seminar) throws AksesDitolakException {
        if (panitia.getRole() == Role.ADMIN) return;
        if (seminar.getIdPanitia() != panitia.getIdUser())
            throw new AksesDitolakException("Anda tidak berhak mengelola seminar ini.");
    }
}


