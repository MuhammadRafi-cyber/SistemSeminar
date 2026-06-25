package model;

import enums.StatusSeminar;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Seminar — Model data seminar.
 * Menyimpan Collection ArrayList<Pendaftaran> untuk laporan Panitia.
 */
public class Seminar {

    private int          idSeminar;
    private String       judul;
    private String       deskripsi;
    private LocalDate    tanggalPelaksanaan;
    private LocalTime    waktuMulai;
    private LocalTime    waktuSelesai;
    private String       lokasi;
    private int          kuota;
    private int          kuotaTerisi;
    private double       harga;
    private StatusSeminar status;
    private int          idPanitia;
    private String       dibuatPada;

    // Collection peserta yang terdaftar (untuk laporan)
    private List<Pendaftaran> daftarPendaftaran;

    // === Constructor lengkap (dari DB) ===
    public Seminar(int idSeminar, String judul, String deskripsi,
                   LocalDate tanggalPelaksanaan, LocalTime waktuMulai, LocalTime waktuSelesai,
                   String lokasi, int kuota, int kuotaTerisi, double harga,
                   StatusSeminar status, int idPanitia, String dibuatPada) {
        this.idSeminar         = idSeminar;
        this.judul             = judul;
        this.deskripsi         = deskripsi;
        this.tanggalPelaksanaan = tanggalPelaksanaan;
        this.waktuMulai        = waktuMulai;
        this.waktuSelesai      = waktuSelesai;
        this.lokasi            = lokasi;
        this.kuota             = kuota;
        this.kuotaTerisi       = kuotaTerisi;
        this.harga             = harga;
        this.status            = status;
        this.idPanitia         = idPanitia;
        this.dibuatPada        = dibuatPada;
        this.daftarPendaftaran = new ArrayList<>();
    }

    // Constructor untuk insert baru
    public Seminar(String judul, String deskripsi, LocalDate tanggalPelaksanaan,
                   LocalTime waktuMulai, LocalTime waktuSelesai, String lokasi,
                   int kuota, double harga, int idPanitia) {
        this.judul             = judul;
        this.deskripsi         = deskripsi;
        this.tanggalPelaksanaan = tanggalPelaksanaan;
        this.waktuMulai        = waktuMulai;
        this.waktuSelesai      = waktuSelesai;
        this.lokasi            = lokasi;
        this.kuota             = kuota;
        this.kuotaTerisi       = 0;
        this.harga             = harga;
        this.status            = StatusSeminar.DIBUKA;
        this.idPanitia         = idPanitia;
        this.daftarPendaftaran = new ArrayList<>();
    }

    // === Getters ===
    public int          getIdSeminar()          { return idSeminar; }
    public String       getJudul()              { return judul; }
    public String       getDeskripsi()          { return deskripsi; }
    public LocalDate    getTanggalPelaksanaan() { return tanggalPelaksanaan; }
    public LocalTime    getWaktuMulai()         { return waktuMulai; }
    public LocalTime    getWaktuSelesai()       { return waktuSelesai; }
    public String       getLokasi()             { return lokasi; }
    public int          getKuota()              { return kuota; }
    public int          getKuotaTerisi()        { return kuotaTerisi; }
    public int          getSisaKuota()          { return kuota - kuotaTerisi; }
    public double       getHarga()              { return harga; }
    public StatusSeminar getStatus()            { return status; }
    public int          getIdPanitia()          { return idPanitia; }
    public String       getDibuatPada()         { return dibuatPada; }
    public List<Pendaftaran> getDaftarPendaftaran() { return daftarPendaftaran; }

    // === Setters ===
    public void setIdSeminar(int id)                      { this.idSeminar = id; }
    public void setJudul(String judul)                    { this.judul = judul; }
    public void setDeskripsi(String deskripsi)            { this.deskripsi = deskripsi; }
    public void setTanggalPelaksanaan(LocalDate tgl)      { this.tanggalPelaksanaan = tgl; }
    public void setWaktuMulai(LocalTime waktu)            { this.waktuMulai = waktu; }
    public void setWaktuSelesai(LocalTime waktu)          { this.waktuSelesai = waktu; }
    public void setLokasi(String lokasi)                  { this.lokasi = lokasi; }
    public void setKuota(int kuota)                       { this.kuota = kuota; }
    public void setKuotaTerisi(int kt)                    { this.kuotaTerisi = kt; }
    public void setHarga(double harga)                    { this.harga = harga; }
    public void setStatus(StatusSeminar status)           { this.status = status; }
    public void setIdPanitia(int id)                      { this.idPanitia = id; }
    public void tambahPendaftaran(Pendaftaran p)          { this.daftarPendaftaran.add(p); }

    public boolean kuotaPenuh() { return kuotaTerisi >= kuota; }

    @Override
    public String toString() {
        return "[" + idSeminar + "] " + judul + " | " + tanggalPelaksanaan
             + " | Sisa: " + getSisaKuota() + " | " + status;
    }
}
