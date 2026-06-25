package service;

import dao.PendaftaranDAO;
import dao.PresensiDAO;
import dao.SertifikatDAO;
import enums.StatusHadir;
import exception.*;
import model.Pendaftaran;
import model.Sertifikat;
import util.KodeGenerator;

import java.sql.SQLException;
import java.util.List;

/**
 * SertifikatService — business logic sertifikat.
 * Query E1 (insert) dan E2 (lihat milik peserta).
 * Syarat: status_hadir = 'HADIR' (dicek via D2).
 */
public class SertifikatService {

    private final SertifikatDAO  sertifikatDAO;
    private final PendaftaranDAO pendaftaranDAO;
    private final PresensiDAO    presensiDAO;

    public SertifikatService(SertifikatDAO sertifikatDAO, PendaftaranDAO pendaftaranDAO,
                              PresensiDAO presensiDAO) {
        this.sertifikatDAO  = sertifikatDAO;
        this.pendaftaranDAO = pendaftaranDAO;
        this.presensiDAO    = presensiDAO;
    }

    /**
     * Generate sertifikat berdasarkan id_pendaftaran.
     * Jika sudah ada → return yang lama (tidak duplikat).
     */
    public Sertifikat generate(int idPendaftaran)
            throws SertifikatTidakTersediaException, DataTidakDitemukanException, SQLException {

        // Validasi pendaftaran ada
        Pendaftaran p = pendaftaranDAO.getById(idPendaftaran);
        if (p == null) throw new DataTidakDitemukanException("Pendaftaran", idPendaftaran);

        // D2: wajib HADIR
        StatusHadir statusHadir = presensiDAO.getStatusByPendaftaran(idPendaftaran);
        if (statusHadir != StatusHadir.HADIR) {
            throw new SertifikatTidakTersediaException();
        }

        // Cek apakah sertifikat sudah ada
        Sertifikat existing = sertifikatDAO.getByPendaftaran(idPendaftaran);
        if (existing != null) return existing; // kembalikan yang sudah ada

        // E1: insert sertifikat baru
        String kodeBaru = KodeGenerator.generateNomorSertifikat();
        int idSertifikat = sertifikatDAO.insert(idPendaftaran, kodeBaru);
        return new Sertifikat(idSertifikat, idPendaftaran, kodeBaru, null);
    }

    // E2: daftar sertifikat peserta
    // return: List Object[]{kodeSertifikat, tanggalTerbit, judulSeminar}
    public List<Object[]> getSertifikatPeserta(int idUser) throws SQLException {
        return sertifikatDAO.getSertifikatByUser(idUser);
    }

    public int hitungSertifikat(int idSeminar) throws SQLException {
        return sertifikatDAO.hitungSertifikatBySeminar(idSeminar);
    }
}
