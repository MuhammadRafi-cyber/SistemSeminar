package service;

import dao.AuditLogDAO;
import dao.PendaftaranDAO;
import dao.PresensiDAO;
import dao.SeminarDAO;
import enums.Role;
import enums.StatusHadir;
import enums.StatusPendaftaran;
import exception.*;
import model.DetailPendaftaran;
import model.Pendaftaran;
import model.Seminar;
import model.User;
import util.Validator;
import java.sql.SQLException;

public class PresensiService {
    private final PresensiDAO    presensiDAO;
    private final PendaftaranDAO pendaftaranDAO;
    private final SeminarDAO     seminarDAO;
    private final AuditLogDAO    auditLogDAO;

    public PresensiService(PresensiDAO pr, PendaftaranDAO pd, SeminarDAO sd, AuditLogDAO al) {
        this.presensiDAO    = pr;
        this.pendaftaranDAO = pd;
        this.seminarDAO     = sd;
        this.auditLogDAO    = al;
    }

    /**
     * Catat presensi HADIR berdasarkan kode_booking.
     *
     * Alur validasi:
     *   1. Hak akses → harus PANITIA/ADMIN
     *   2. Kode booking tidak kosong
     *   3. Cari tiket via kode_booking → KodeBookingTidakValidException jika tidak ada
     *   4. Pendaftaran harus CONFIRMED → KodeBookingTidakValidException jika bukan
     *   5. Validasi jendela waktu presensi T-60 s/d selesai (BR-13)
     *   6. Cek tidak duplikat → PresensiDuplikatException
     *   7. Simpan presensi dengan dicatat_oleh = id panitia
     */
    public StatusHadir scanPresensi(User panitia, String kodeBooking)
            throws AksesDitolakException, InputKosongException, KodeBookingTidakValidException,
                   PresensiDuplikatException, DataTidakDitemukanException, SQLException {

        // 1. Hak akses
        if (panitia.getRole() == Role.PESERTA)
            throw new AksesDitolakException("Hanya Panitia/Admin yang dapat mencatat presensi.");

        // 2. Validasi input
        Validator.cekTidakKosong(kodeBooking, "Kode Booking");
        String kode = kodeBooking.trim().toUpperCase();

        // 3. Cari tiket
        DetailPendaftaran tiket;
        try {
            tiket = pendaftaranDAO.getDetailByKodeBooking(kode);
        } catch (DataTidakDitemukanException e) {
            throw new KodeBookingTidakValidException(kode, "tiket tidak ditemukan di sistem");
        }

        // 4. Pendaftaran harus CONFIRMED
        Pendaftaran pendaftaran = pendaftaranDAO.getById(tiket.getIdPendaftaran());
        if (pendaftaran.getStatus() != StatusPendaftaran.CONFIRMED)
            throw new KodeBookingTidakValidException(kode,
                "status pendaftaran adalah " + pendaftaran.getStatus() + ", bukan CONFIRMED");

        // 5. Validasi jendela waktu presensi (BR-13)
        Seminar seminar = seminarDAO.getById(pendaftaran.getIdSeminar());
        Validator.cekJendelaPresensi(seminar.getTanggalMulai(), seminar.getTanggalSelesai(), kode);

        // 6. Cek duplikat
        StatusHadir statusLama = presensiDAO.getStatusByDetail(tiket.getIdDetail());
        if (statusLama == StatusHadir.HADIR)
            throw new PresensiDuplikatException(kode);

        // 7. Simpan presensi dengan dicatat_oleh
        presensiDAO.simpanAtauUpdate(tiket.getIdDetail(), StatusHadir.HADIR, panitia.getIdUser());
        auditLogDAO.log(panitia.getIdUser(), "CATAT_PRESENSI", "presensi",
            tiket.getIdDetail(), "Presensi HADIR: " + kode);

        return StatusHadir.HADIR;
    }

    /**
     * Cek status presensi tiket tertentu.
     * @return StatusHadir atau null jika belum presensi
     */
    public StatusHadir cekStatus(String kodeBooking)
            throws InputKosongException, KodeBookingTidakValidException,
                   DataTidakDitemukanException, SQLException {

        Validator.cekTidakKosong(kodeBooking, "Kode Booking");
        String kode = kodeBooking.trim().toUpperCase();
        try {
            DetailPendaftaran tiket = pendaftaranDAO.getDetailByKodeBooking(kode);
            return presensiDAO.getStatusByDetail(tiket.getIdDetail());
        } catch (DataTidakDitemukanException e) {
            throw new KodeBookingTidakValidException(kode, "tiket tidak ditemukan");
        }
    }

    public int hitungHadir(int idSeminar) throws SQLException {
        return presensiDAO.hitungHadirBySeminar(idSeminar);
    }
}
