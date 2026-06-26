package model;

import enums.StatusHadir;

/**
 * Presensi — FK ke id_detail, sesuai DB v4.
 * Kolom DB: id_presensi, id_detail, status, waktu
 */
public class Presensi {
    private int         idPresensi;
    private int         idDetail;
    private StatusHadir status;    // kolom DB: status (bukan status_hadir)
    private String      waktu;     // kolom DB: waktu (bukan waktu_presensi)

    public Presensi(int idPresensi, int idDetail, StatusHadir status, String waktu) {
        this.idPresensi = idPresensi;
        this.idDetail   = idDetail;
        this.status     = status;
        this.waktu      = waktu;
    }

    public Presensi(int idDetail, StatusHadir status) {
        this.idDetail = idDetail;
        this.status   = status;
    }

    public int         getIdPresensi() { return idPresensi; }
    public int         getIdDetail()   { return idDetail; }
    public StatusHadir getStatus()     { return status; }
    public String      getWaktu()      { return waktu; }

    public void setIdPresensi(int id)      { this.idPresensi = id; }
    public void setStatus(StatusHadir s)   { this.status = s; }
    public void setWaktu(String w)         { this.waktu = w; }
}
