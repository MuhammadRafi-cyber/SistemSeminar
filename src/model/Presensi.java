package model;

import enums.StatusHadir;

/**
 * Presensi — sesuai DB tim.
 * Tabel: presensi(id_presensi, id_pendaftaran, status_hadir, waktu_presensi)
 * FK ke pendaftaran (bukan ke detail_pendaftaran).
 */
public class Presensi {

    private int         idPresensi;
    private int         idPendaftaran;   // FK ke pendaftaran.id_pendaftaran
    private StatusHadir statusHadir;
    private String      waktuPresensi;

    // Constructor dari DB
    public Presensi(int idPresensi, int idPendaftaran,
                    StatusHadir statusHadir, String waktuPresensi) {
        this.idPresensi    = idPresensi;
        this.idPendaftaran = idPendaftaran;
        this.statusHadir   = statusHadir;
        this.waktuPresensi = waktuPresensi;
    }

    // Constructor untuk INSERT baru
    public Presensi(int idPendaftaran, StatusHadir statusHadir) {
        this.idPendaftaran = idPendaftaran;
        this.statusHadir   = statusHadir;
    }

    // Getters
    public int         getIdPresensi()    { return idPresensi; }
    public int         getIdPendaftaran() { return idPendaftaran; }
    public StatusHadir getStatusHadir()   { return statusHadir; }
    public String      getWaktuPresensi() { return waktuPresensi; }

    // Setters
    public void setIdPresensi(int id)         { this.idPresensi = id; }
    public void setStatusHadir(StatusHadir s)  { this.statusHadir = s; }
    public void setWaktuPresensi(String w)     { this.waktuPresensi = w; }
}
