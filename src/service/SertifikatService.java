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

/**
 * SertifikatService — business logic penerbitan sertifikat.
 * DB v4: sertifikat FK ke id_detail, kolom nomor (UNIQUE), versi, file_path.
 * Jika sertifikat sudah ada → regenerate (nomor baru, versi naik).
 */
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
     * Syarat: peserta harus berstatus HADIR.
     *
     * Alur:
     *   1. Validasi kode_booking tidak kosong
     *   2. Cari tiket via kode_booking
     *   3. Cek status presensi harus HADIR
     *   4. Jika belum ada sertifikat → insert baru (versi 1)
     *   5. Jika sudah ada → regenerate (nomor baru, versi + 1)
     *
     * @return Sertifikat yang baru diterbitkan/diperbarui
     */
    public Sertifikat generate(String kodeBooking)
            throws InputKosongException, DataTidakDitemukanException,
                   SertifikatTidakTersediaException, SQLException {

        // 1. Validasi input
        Validator.cekTidakKosong(kodeBooking, "Kode Booking");
        String kode = kodeBooking.trim().toUpperCase();

        // 2. Cari tiket
        DetailPendaftaran tiket = pendaftaranDAO.getDetailByKodeBooking(kode);

        // 3. Cek presensi — harus HADIR
        StatusHadir statusHadir = presensiDAO.getStatusByDetail(tiket.getIdDetail());
        if (statusHadir != StatusHadir.HADIR)
            throw new SertifikatTidakTersediaException();

        // 4. Cek apakah sertifikat sudah ada
        Sertifikat existing = sertifikatDAO.getByDetail(tiket.getIdDetail());

        String nomorBaru   = KodeGenerator.generateNomorSertifikat();
        String filePathBaru = "/sertifikat/" + nomorBaru + ".pdf";

        if (existing == null) {
            // Insert baru (versi 1)
            int idSertifikat = sertifikatDAO.insert(tiket.getIdDetail(), nomorBaru, filePathBaru);
            auditLogDAO.log(null, "TERBITKAN_SERTIFIKAT", "sertifikat");
            return new Sertifikat(idSertifikat, tiket.getIdDetail(), nomorBaru, 1, filePathBaru, null);
        } else {
            // 5. Regenerate — nomor baru, versi naik
            sertifikatDAO.regenerate(tiket.getIdDetail(), nomorBaru, filePathBaru);
            existing.setNomor(nomorBaru);
            existing.setVersi(existing.getVersi() + 1);
            existing.setFilePath(filePathBaru);
            auditLogDAO.log(null, "REGENERATE_SERTIFIKAT", "sertifikat");
            return existing;
        }
    }

    /**
     * Daftar sertifikat milik seorang pemesan.
     * Return: List Object[]{nomor, tanggalTerbit, versi, filePath, judulSeminar, namaPeserta}
     */
    public List<Object[]> getSertifikatPemesan(int idUser) throws SQLException {
        return sertifikatDAO.getSertifikatByUser(idUser);
    }

    /** Hitung sertifikat terbit untuk laporan F2. */
    public int hitungSertifikat(int idSeminar) throws SQLException {
        return sertifikatDAO.hitungBySeminar(idSeminar);
    }
}
