package service;

import dao.PendaftaranDAO;
import dao.SeminarDAO;
import enums.StatusPendaftaran;
import exception.*;
import model.Pendaftaran;
import model.Seminar;

import java.sql.SQLException;
import java.util.List;

/**
 * PendaftaranService — business logic pendaftaran seminar.
 * Sesuai DB tim: INSERT pendaftaran(id_user, id_seminar) + UPDATE kuota_terisi + 1
 */
public class PendaftaranService {

    private final PendaftaranDAO pendaftaranDAO;
    private final SeminarDAO     seminarDAO;

    public PendaftaranService(PendaftaranDAO pendaftaranDAO, SeminarDAO seminarDAO) {
        this.pendaftaranDAO = pendaftaranDAO;
        this.seminarDAO     = seminarDAO;
    }

    /**
     * Daftar seminar.
     * Alur: cek duplikat (C1) → cek kuota → insert (C2) → update kuota_terisi (C3)
     */
    public Pendaftaran daftar(int idUser, int idSeminar)
            throws PendaftaranDuplikatException, KuotaPenuhException,
                   DataTidakDitemukanException, SQLException {

        // Cek kuota dari DB
        Seminar seminar = seminarDAO.getById(idSeminar);
        if (seminar == null) throw new DataTidakDitemukanException("Seminar", idSeminar);
        if (seminar.kuotaPenuh()) throw new KuotaPenuhException();

        // C1: cek duplikat
        if (pendaftaranDAO.sudahDaftar(idUser, idSeminar)) {
            throw new PendaftaranDuplikatException(idSeminar);
        }

        // C2: insert pendaftaran
        Pendaftaran p = new Pendaftaran(idUser, idSeminar);
        pendaftaranDAO.insert(p);

        // C3: tambah kuota_terisi + 1
        seminarDAO.tambahKuotaTerisi(idSeminar);

        // Set CONFIRMED langsung (tidak ada payment gateway)
        pendaftaranDAO.updateStatus(p.getIdPendaftaran(), StatusPendaftaran.CONFIRMED);
        p.setStatusPendaftaran(StatusPendaftaran.CONFIRMED);

        return p;
    }

    /**
     * Batalkan pendaftaran → kuota dikembalikan.
     */
    public boolean batalkan(int idPendaftaran)
            throws DataTidakDitemukanException, SQLException {
        Pendaftaran p = pendaftaranDAO.getById(idPendaftaran);
        if (p == null) throw new DataTidakDitemukanException("Pendaftaran", idPendaftaran);
        pendaftaranDAO.updateStatus(idPendaftaran, StatusPendaftaran.CANCELLED);
        seminarDAO.kurangiKuotaTerisi(p.getIdSeminar());
        return true;
    }

    // C5: riwayat pendaftaran peserta (judul, tgl, status, id)
    public List<Object[]> getRiwayatPeserta(int idUser) throws SQLException {
        return pendaftaranDAO.getRiwayatPeserta(idUser);
    }

    // C4: daftar peserta seminar (untuk Panitia)
    public List<Object[]> getPesertaSeminar(int idSeminar) throws SQLException {
        return pendaftaranDAO.getPesertaBySeminar(idSeminar);
    }

    public Pendaftaran getById(int idPendaftaran) throws SQLException {
        return pendaftaranDAO.getById(idPendaftaran);
    }
}
