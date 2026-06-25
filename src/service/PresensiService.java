package service;

import dao.PendaftaranDAO;
import dao.PresensiDAO;
import enums.Role;
import enums.StatusHadir;
import enums.StatusPendaftaran;
import exception.*;
import model.Pendaftaran;
import model.User;

import java.sql.SQLException;

/**
 * PresensiService — business logic presensi.
 * Query D1 (insert/update) dan D2 (cek status).
 * FK presensi → id_pendaftaran (sesuai DB tim).
 */
public class PresensiService {

    private final PresensiDAO    presensiDAO;
    private final PendaftaranDAO pendaftaranDAO;

    public PresensiService(PresensiDAO presensiDAO, PendaftaranDAO pendaftaranDAO) {
        this.presensiDAO    = presensiDAO;
        this.pendaftaranDAO = pendaftaranDAO;
    }

    /**
     * D1: Catat presensi HADIR berdasarkan id_pendaftaran.
     */
    public StatusHadir scanPresensi(User panitia, int idPendaftaran)
            throws AksesDitolakException, DataTidakDitemukanException,
                   PresensiDuplikatException, SQLException {

        if (panitia.getRole() == Role.PESERTA) {
            throw new AksesDitolakException("Hanya Panitia/Admin yang dapat mencatat presensi.");
        }

        // Validasi pendaftaran ada dan CONFIRMED
        Pendaftaran p = pendaftaranDAO.getById(idPendaftaran);
        if (p == null || p.getStatusPendaftaran() != StatusPendaftaran.CONFIRMED) {
            throw new DataTidakDitemukanException("Pendaftaran", idPendaftaran);
        }

        // D2: cek apakah sudah HADIR
        StatusHadir statusLama = presensiDAO.getStatusByPendaftaran(idPendaftaran);
        if (statusLama == StatusHadir.HADIR) {
            throw new PresensiDuplikatException(idPendaftaran);
        }

        // D1: simpan atau update
        presensiDAO.simpanAtauUpdate(idPendaftaran, StatusHadir.HADIR);
        return StatusHadir.HADIR;
    }

    /**
     * D2: cek status kehadiran.
     */
    public StatusHadir cekStatus(int idPendaftaran) throws SQLException {
        return presensiDAO.getStatusByPendaftaran(idPendaftaran);
    }

    public int hitungHadir(int idSeminar) throws SQLException {
        return presensiDAO.hitungHadirBySeminar(idSeminar);
    }
}
