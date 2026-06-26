package model;

import enums.StatusPembayaran;

/**
 * Pembayaran — sesuai DB v4.
 * Kolom DB: id_pembayaran, id_pendaftaran (UNIQUE), metode, status, nominal, tanggal_bayar
 * UNIQUE(id_pendaftaran): maksimal 1 pembayaran per transaksi.
 */
public class Pembayaran {
    private int              idPembayaran;
    private int              idPendaftaran;
    private String           metode;
    private StatusPembayaran status;     // kolom DB: status (bukan status_pembayaran)
    private double           nominal;   // kolom DB: nominal (bukan jumlah)
    private String           tanggalBayar;

    // Constructor dari DB
    public Pembayaran(int idPembayaran, int idPendaftaran, String metode,
                      StatusPembayaran status, double nominal, String tanggalBayar) {
        this.idPembayaran  = idPembayaran;
        this.idPendaftaran = idPendaftaran;
        this.metode        = metode;
        this.status        = status;
        this.nominal       = nominal;
        this.tanggalBayar  = tanggalBayar;
    }

    // Constructor untuk INSERT baru
    public Pembayaran(int idPendaftaran, String metode, double nominal) {
        this.idPendaftaran = idPendaftaran;
        this.metode        = metode;
        this.nominal       = nominal;
        this.status        = StatusPembayaran.PENDING;
    }

    public int              getIdPembayaran()  { return idPembayaran; }
    public int              getIdPendaftaran() { return idPendaftaran; }
    public String           getMetode()        { return metode; }
    public StatusPembayaran getStatus()        { return status; }
    public double           getNominal()       { return nominal; }
    public String           getTanggalBayar()  { return tanggalBayar; }

    public void setIdPembayaran(int id)         { this.idPembayaran = id; }
    public void setStatus(StatusPembayaran s)   { this.status = s; }
    public void setTanggalBayar(String t)       { this.tanggalBayar = t; }

    @Override
    public String toString() {
        return "[" + idPembayaran + "] " + metode
             + " | Rp" + String.format("%,.0f", nominal)
             + " | " + status;
    }
}
