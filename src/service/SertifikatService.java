package service;

import dao.AuditLogDAO;
import dao.PendaftaranDAO;
import dao.PresensiDAO;
import dao.SertifikatDAO;
import enums.StatusHadir;
import exception.*;
import model.DetailPendaftaran;
import model.Sertifikat;
import util.KodeGenerator;
import util.Validator;
import java.sql.SQLException;
import java.util.List;

public class SertifikatService {
    private final SertifikatDAO  sertifikatDAO;
    private final PendaftaranDAO pendaftaranDAO;
    private final PresensiDAO    presensiDAO;
    private final AuditLogDAO    auditLogDAO;

    public SertifikatService(SertifikatDAO s, PendaftaranDAO p, PresensiDAO pr, AuditLogDAO a) {
        this.sertifikatDAO  = s;
        this.pendaftaranDAO = p;
        this.presensiDAO    = pr;
        this.auditLogDAO    = a;
    }

    /**
     * Generate atau regenerate sertifikat berdasarkan kode_booking.
     * Syarat: peserta harus berstatus HADIR (BR-06).
     * Jika sudah ada → regenerate (nomor baru, versi naik).
     */
    public Sertifikat generate(String kodeBooking)
            throws InputKosongException, KodeBookingTidakValidException,
                   SertifikatTidakTersediaException, SQLException {

        Validator.cekTidakKosong(kodeBooking, "Kode Booking");
        String kode = kodeBooking.trim().toUpperCase();

        // Cari tiket
        DetailPendaftaran tiket;
        try {
            tiket = pendaftaranDAO.getDetailByKodeBooking(kode);
        } catch (DataTidakDitemukanException e) {
            throw new KodeBookingTidakValidException(kode, "tiket tidak ditemukan");
        }

        // Cek status HADIR
        StatusHadir statusHadir = presensiDAO.getStatusByDetail(tiket.getIdDetail());
        if (statusHadir != StatusHadir.HADIR)
            throw new SertifikatTidakTersediaException();

        String nomorBaru    = KodeGenerator.generateNomorSertifikat();
        String filePathBaru = "/sertifikat/" + nomorBaru + ".pdf";

        Sertifikat existing = sertifikatDAO.getByDetail(tiket.getIdDetail());
        if (existing == null) {
            // Generate baru (versi 1)
            int idSertifikat = sertifikatDAO.insert(tiket.getIdDetail(), nomorBaru, filePathBaru);
            auditLogDAO.log(null, "TERBITKAN_SERTIFIKAT", "sertifikat",
                idSertifikat, "Sertifikat untuk tiket: " + kode);
            return new Sertifikat(idSertifikat, tiket.getIdDetail(), nomorBaru, null, 1, filePathBaru);
        } else {
            // Regenerate — nomor baru, versi naik
            sertifikatDAO.regenerate(tiket.getIdDetail(), nomorBaru, filePathBaru);
            existing.setNomorSertifikat(nomorBaru);
            existing.setVersi(existing.getVersi() + 1);
            existing.setFilePath(filePathBaru);
            auditLogDAO.log(null, "REGENERATE_SERTIFIKAT", "sertifikat",
                existing.getIdSertifikat(), "Regenerate v" + existing.getVersi() + " untuk: " + kode);
            return existing;
        }
    }

    public List<Object[]> getSertifikatPemesan(int idUser) throws SQLException {
        return sertifikatDAO.getSertifikatByUser(idUser);
    }

    public int hitungSertifikat(int idSeminar) throws SQLException {
        return sertifikatDAO.hitungBySeminar(idSeminar);
    }
}
