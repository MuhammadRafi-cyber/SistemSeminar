package model;

import enums.StatusHadir;

public class Presensi {
    private int         idPresensi;
    private int         idDetail;
    private StatusHadir status;
    private String      waktuPresensi;   // diganti dari "waktu"
    private Integer     dicatatOleh;

    public Presensi(int idPresensi, int idDetail, StatusHadir status,
                    String waktuPresensi, Integer dicatatOleh) {
        this.idPresensi    = idPresensi;
        this.idDetail      = idDetail;
        this.status        = status;
        this.waktuPresensi = waktuPresensi;
        this.dicatatOleh   = dicatatOleh;
    }
    public Presensi(int idDetail, StatusHadir status, Integer dicatatOleh) {
        this.idDetail    = idDetail;
        this.status      = status;
        this.dicatatOleh = dicatatOleh;
    }

    public int         getIdPresensi()    { return idPresensi; }
    public int         getIdDetail()      { return idDetail; }
    public StatusHadir getStatus()        { return status; }
    public String      getWaktuPresensi() { return waktuPresensi; }   // diganti dari getWaktu()
    public Integer     getDicatatOleh()   { return dicatatOleh; }

    public void setIdPresensi(int id)         { this.idPresensi = id; }
    public void setStatus(StatusHadir s)      { this.status = s; }
    public void setWaktuPresensi(String w)    { this.waktuPresensi = w; }  // diganti dari setWaktu()
    public void setDicatatOleh(Integer id)    { this.dicatatOleh = id; }
}