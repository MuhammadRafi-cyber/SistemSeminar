package model;

import enums.StatusPendaftaran;
import java.util.ArrayList;
import java.util.List;

/**
 * Pendaftaran — header transaksi, sesuai DB v4.
 * Kolom: id_pendaftaran, id_pemesan, id_seminar, kode_transaksi, status, total, tanggal_daftar
 */
public class Pendaftaran {
    private int                  idPendaftaran;
    private int                  idPemesan;       // FK -> user.id_user (bukan id_user lagi)
    private int                  idSeminar;
    private String               kodeTransaksi;   // UNIQUE
    private StatusPendaftaran    status;
    private double               total;
    private String               tanggalDaftar;
    private List<DetailPendaftaran> detailList = new ArrayList<>();

    // Constructor dari DB
    public Pendaftaran(int idPendaftaran, int idPemesan, int idSeminar,
                       String kodeTransaksi, StatusPendaftaran status,
                       double total, String tanggalDaftar) {
        this.idPendaftaran = idPendaftaran;
        this.idPemesan     = idPemesan;
        this.idSeminar     = idSeminar;
        this.kodeTransaksi = kodeTransaksi;
        this.status        = status;
        this.total         = total;
        this.tanggalDaftar = tanggalDaftar;
    }

    // Constructor untuk INSERT baru
    public Pendaftaran(int idPemesan, int idSeminar, String kodeTransaksi, double total) {
        this.idPemesan     = idPemesan;
        this.idSeminar     = idSeminar;
        this.kodeTransaksi = kodeTransaksi;
        this.status        = StatusPendaftaran.PENDING;
        this.total         = total;
    }

    // Getters
    public int                   getIdPendaftaran() { return idPendaftaran; }
    public int                   getIdPemesan()     { return idPemesan; }
    public int                   getIdSeminar()     { return idSeminar; }
    public String                getKodeTransaksi() { return kodeTransaksi; }
    public StatusPendaftaran     getStatus()        { return status; }
    public double                getTotal()         { return total; }
    public String                getTanggalDaftar() { return tanggalDaftar; }
    public List<DetailPendaftaran> getDetailList()  { return detailList; }

    // Setters
    public void setIdPendaftaran(int id)            { this.idPendaftaran = id; }
    public void setStatus(StatusPendaftaran s)      { this.status = s; }
    public void tambahDetail(DetailPendaftaran d)   { detailList.add(d); }
    public void setDetailList(List<DetailPendaftaran> l) { detailList = l; }

    @Override
    public String toString() {
        return "[" + idPendaftaran + "] " + kodeTransaksi
             + " | Seminar#" + idSeminar
             + " | " + status
             + " | Rp" + String.format("%,.0f", total);
    }
}
