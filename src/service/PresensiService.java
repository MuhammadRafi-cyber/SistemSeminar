package service;

import dao.AuditLogDAO;
import dao.PendaftaranDAO;
import dao.PresensiDAO;
import enums.Role;
import enums.StatusHadir;
import enums.StatusPendaftaran;
import exception.*;
import model.DetailPendaftaran;
import model.Pendaftaran;
import model.User;
import util.Validator;
import java.sql.SQLException;

/**
 * PresensiService — business logic pencatatan kehadiran.
 * DB v4: presensi FK ke id_detail, kolom status (bukan status_hadir), waktu (bukan waktu_presensi).
 */
public class PresensiService {
    private final PresensiDAO    presensiDAO;
    private final PendaftaranDAO pendaftaranDAO;
    private final AuditLogDAO    auditLogDAO;

    public PresensiService(PresensiDAO pr, PendaftaranDAO pd, AuditLogDAO al) {
        this.presensiDAO    = pr;
        this.pendaftaranDAO = pd;
        this.auditLogDAO    = al;
    }

    /**
     * Catat presensi HADIR berdasarkan kode_booking tiket.
     * Alur:
     *   1. Validasi hak akses (harus Panitia/Admin)
     *   2. Validasi kode_booking tidak kosong
     *   3. Cari tiket via kode_booking
     *   4. Cek pendaftaran harus berstatus CONFIRMED
     *   5. Cek tidak duplikat (belum HADIR sebelumnya)
     *   6. Simpan atau update presensi
     *
     * @param panitia      User yang mencatat (harus PANITIA/ADMIN)
     * @param kodeBooking  Kode unik pada tiket peserta
     * @return StatusHadir.HADIR jika berhasil
     */
    public StatusHadir scanPresensi(User panitia, String kodeBooking)
            throws AksesDitolakException, InputKosongException,
                   DataTidakDitemukanException, PresensiDuplikatException, SQLException {

        // 1. Hak akses
        if (panitia.getRole() == Role.PESERTA)
            throw new AksesDitolakException("Hanya Panitia/Admin yang dapat mencatat presensi.");

        // 2. Validasi input
        Validator.cekTidakKosong(kodeBooking, "Kode Booking");
        String kode = kodeBooking.trim().toUpperCase();

        // 3. Cari tiket
        DetailPendaftaran tiket = pendaftaranDAO.getDetailByKodeBooking(kode);
        // DAO sudah throw DataTidakDitemukanException jika tidak ada

        // 4. Cek pendaftaran harus CONFIRMED
        Pendaftaran pendaftaran = pendaftaranDAO.getById(tiket.getIdPendaftaran());
        if (pendaftaran.getStatus() != StatusPendaftaran.CONFIRMED)
            throw new AksesDitolakException(
                "Tiket '" + kode + "' tidak dapat dipresensi. "
                + "Status pendaftaran: " + pendaftaran.getStatus()
                + ". Harus CONFIRMED.");

        // 5. Cek duplikat presensi
        StatusHadir statusLama = presensiDAO.getStatusByDetail(tiket.getIdDetail());
        if (statusLama == StatusHadir.HADIR)
            throw new PresensiDuplikatException(kode);

        // 6. Simpan presensi
        presensiDAO.simpanAtauUpdate(tiket.getIdDetail(), StatusHadir.HADIR);
        auditLogDAO.log(panitia.getIdUser(), "UBAH_PRESENSI", "presensi");

        return StatusHadir.HADIR;
    }

    /**
     * Cek status kehadiran tiket tertentu.
     * @return StatusHadir atau null jika belum pernah presensi
     */
    public StatusHadir cekStatus(String kodeBooking)
            throws InputKosongException, DataTidakDitemukanException, SQLException {

        Validator.cekTidakKosong(kodeBooking, "Kode Booking");
        String kode = kodeBooking.trim().toUpperCase();

        DetailPendaftaran tiket = pendaftaranDAO.getDetailByKodeBooking(kode);
        return presensiDAO.getStatusByDetail(tiket.getIdDetail());
    }

    /** Hitung jumlah peserta HADIR di satu seminar (untuk laporan F2). */
    public int hitungHadir(int idSeminar) throws SQLException {
        return presensiDAO.hitungHadirBySeminar(idSeminar);
    }
}
