package model;

import enums.StatusPembayaran;
import enums.StatusRefund;

/**
 * Pembayaran — sesuai DB v5.
 * Kolom baru v5: status_refund (TIDAK_ADA/DIMINTA/DIPROSES/SELESAI).
 * Alur refund: peserta batalkan → status_refund = DIMINTA → DIPROSES → SELESAI.
 */
public class Pembayaran {
    private int              idPembayaran;
    private int              idPendaftaran;
    private String           metode;
    private StatusPembayaran status;
    private double           nominal;
    private String           waktuBayar;
    private StatusRefund     statusRefund;   // [TAMBAHAN v5]

    public Pembayaran(int idPembayaran, int idPendaftaran, String metode,
                      StatusPembayaran status, double nominal,
                      String waktuBayar, StatusRefund statusRefund) {
        this.idPembayaran  = idPembayaran;
        this.idPendaftaran = idPendaftaran;
        this.metode        = metode;
        this.status        = status;
        this.nominal       = nominal;
        this.waktuBayar    = waktuBayar;
        this.statusRefund  = statusRefund;
    }
    public Pembayaran(int idPendaftaran, String metode, double nominal) {
        this.idPendaftaran = idPendaftaran;
        this.metode        = metode;
        this.nominal       = nominal;
        this.status        = StatusPembayaran.PENDING;
        this.statusRefund  = StatusRefund.TIDAK_ADA;
    }

    public int              getIdPembayaran()  { return idPembayaran; }
    public int              getIdPendaftaran() { return idPendaftaran; }
    public String           getMetode()        { return metode; }
    public StatusPembayaran getStatus()        { return status; }
    public double           getNominal()       { return nominal; }
    public String           getWaktuBayar()    { return waktuBayar; }
    public StatusRefund     getStatusRefund()  { return statusRefund; }

    public void setIdPembayaran(int id)          { this.idPembayaran = id; }
    public void setStatus(StatusPembayaran s)    { this.status = s; }
    public void setStatusRefund(StatusRefund sr) { this.statusRefund = sr; }
    public void setWaktuBayar(String w)          { this.waktuBayar = w; }

    @Override public String toString() {
        return "[" + idPembayaran + "] " + metode
             + " | Rp" + String.format("%,.0f", nominal)
             + " | " + status
             + " | Refund: " + statusRefund;
    }
}
