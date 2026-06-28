package model;

import enums.StatusHadir;

/**
 * Presensi — FK ke id_detail, sesuai DB v5.
 * Kolom baru v5: dicatat_oleh (FK user → Panitia yang scan, nullable).
 */
public class Presensi {
    private int         idPresensi;
    private int         idDetail;
    private StatusHadir status;
    private String      waktu;
    private Integer     dicatatOleh;   // [TAMBAHAN v5] FK user.id_user (Panitia)

    public Presensi(int idPresensi, int idDetail, StatusHadir status,
                    String waktu, Integer dicatatOleh) {
        this.idPresensi  = idPresensi;
        this.idDetail    = idDetail;
        this.status      = status;
        this.waktu       = waktu;
        this.dicatatOleh = dicatatOleh;
    }
    public Presensi(int idDetail, StatusHadir status, Integer dicatatOleh) {
        this.idDetail    = idDetail;
        this.status      = status;
        this.dicatatOleh = dicatatOleh;
    }

    public int         getIdPresensi()  { return idPresensi; }
    public int         getIdDetail()    { return idDetail; }
    public StatusHadir getStatus()      { return status; }
    public String      getWaktu()       { return waktu; }
    public Integer     getDicatatOleh() { return dicatatOleh; }

    public void setIdPresensi(int id)      { this.idPresensi = id; }
    public void setStatus(StatusHadir s)   { this.status = s; }
    public void setWaktu(String w)         { this.waktu = w; }
    public void setDicatatOleh(Integer id) { this.dicatatOleh = id; }
}
