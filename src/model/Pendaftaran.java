package model;

import enums.StatusPendaftaran;

/**
 * Pendaftaran — sesuai DB tim.
 * Tabel: pendaftaran(id_pendaftaran, id_user, id_seminar, tanggal_daftar, status_pendaftaran)
 */
public class Pendaftaran {

    private int               idPendaftaran;
    private int               idUser;
    private int               idSeminar;
    private String            tanggalDaftar;
    private StatusPendaftaran statusPendaftaran;

    // Constructor dari DB (SELECT)
    public Pendaftaran(int idPendaftaran, int idUser, int idSeminar,
                       String tanggalDaftar, StatusPendaftaran statusPendaftaran) {
        this.idPendaftaran     = idPendaftaran;
        this.idUser            = idUser;
        this.idSeminar         = idSeminar;
        this.tanggalDaftar     = tanggalDaftar;
        this.statusPendaftaran = statusPendaftaran;
    }

    // Constructor untuk INSERT baru (C2: INSERT INTO pendaftaran (id_user, id_seminar))
    public Pendaftaran(int idUser, int idSeminar) {
        this.idUser            = idUser;
        this.idSeminar         = idSeminar;
        this.statusPendaftaran = StatusPendaftaran.PENDING;
    }

    // Getters
    public int               getIdPendaftaran()     { return idPendaftaran; }
    public int               getIdUser()            { return idUser; }
    public int               getIdSeminar()         { return idSeminar; }
    public String            getTanggalDaftar()     { return tanggalDaftar; }
    public StatusPendaftaran getStatusPendaftaran() { return statusPendaftaran; }

    // Setters
    public void setIdPendaftaran(int id)              { this.idPendaftaran = id; }
    public void setStatusPendaftaran(StatusPendaftaran s) { this.statusPendaftaran = s; }

    @Override
    public String toString() {
        return "[" + idPendaftaran + "] Seminar#" + idSeminar
             + " | " + statusPendaftaran + " | " + tanggalDaftar;
    }
}
