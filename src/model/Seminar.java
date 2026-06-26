package model;

import enums.StatusSeminar;
import java.time.LocalDateTime;

/**
 * Seminar — sesuai DB v4.
 * Kolom baru/berubah:
 *   - id_institusi (FK, wajib)
 *   - tanggal_mulai DATETIME (menggantikan tanggal_pelaksanaan + waktu_mulai)
 *   - tanggal_selesai DATETIME (menggantikan waktu_selesai)
 *   - harga DECIMAL (ada di seminar, bukan dihitung terpisah)
 */
public class Seminar {
    private int            idSeminar;
    private int            idInstitusi;
    private int            idPanitia;
    private String         judul;
    private String         deskripsi;
    private LocalDateTime  tanggalMulai;
    private LocalDateTime  tanggalSelesai;
    private String         lokasi;
    private int            kuota;
    private int            kuotaTerisi;
    private double         harga;
    private StatusSeminar  status;
    private String         dibuatPada;

    // Constructor dari DB
    public Seminar(int idSeminar, int idInstitusi, int idPanitia, String judul, String deskripsi,
                   LocalDateTime tanggalMulai, LocalDateTime tanggalSelesai, String lokasi,
                   int kuota, int kuotaTerisi, double harga, StatusSeminar status, String dibuatPada) {
        this.idSeminar      = idSeminar;
        this.idInstitusi    = idInstitusi;
        this.idPanitia      = idPanitia;
        this.judul          = judul;
        this.deskripsi      = deskripsi;
        this.tanggalMulai   = tanggalMulai;
        this.tanggalSelesai = tanggalSelesai;
        this.lokasi         = lokasi;
        this.kuota          = kuota;
        this.kuotaTerisi    = kuotaTerisi;
        this.harga          = harga;
        this.status         = status;
        this.dibuatPada     = dibuatPada;
    }

    // Constructor untuk INSERT baru
    public Seminar(int idInstitusi, int idPanitia, String judul, String deskripsi,
                   LocalDateTime tanggalMulai, LocalDateTime tanggalSelesai,
                   String lokasi, int kuota, double harga) {
        this.idInstitusi    = idInstitusi;
        this.idPanitia      = idPanitia;
        this.judul          = judul;
        this.deskripsi      = deskripsi;
        this.tanggalMulai   = tanggalMulai;
        this.tanggalSelesai = tanggalSelesai;
        this.lokasi         = lokasi;
        this.kuota          = kuota;
        this.kuotaTerisi    = 0;
        this.harga          = harga;
        this.status         = StatusSeminar.DIBUKA;
    }

    // Getters
    public int           getIdSeminar()      { return idSeminar; }
    public int           getIdInstitusi()    { return idInstitusi; }
    public int           getIdPanitia()      { return idPanitia; }
    public String        getJudul()          { return judul; }
    public String        getDeskripsi()      { return deskripsi; }
    public LocalDateTime getTanggalMulai()   { return tanggalMulai; }
    public LocalDateTime getTanggalSelesai() { return tanggalSelesai; }
    public String        getLokasi()         { return lokasi; }
    public int           getKuota()          { return kuota; }
    public int           getKuotaTerisi()    { return kuotaTerisi; }
    public int           getSisaKuota()      { return kuota - kuotaTerisi; }
    public double        getHarga()          { return harga; }
    public StatusSeminar getStatus()         { return status; }
    public String        getDibuatPada()     { return dibuatPada; }
    public boolean       kuotaPenuh()        { return kuotaTerisi >= kuota; }

    // Setters
    public void setIdSeminar(int id)               { this.idSeminar = id; }
    public void setIdInstitusi(int id)             { this.idInstitusi = id; }
    public void setJudul(String j)                 { this.judul = j; }
    public void setDeskripsi(String d)             { this.deskripsi = d; }
    public void setTanggalMulai(LocalDateTime t)   { this.tanggalMulai = t; }
    public void setTanggalSelesai(LocalDateTime t) { this.tanggalSelesai = t; }
    public void setLokasi(String l)                { this.lokasi = l; }
    public void setKuota(int k)                    { this.kuota = k; }
    public void setKuotaTerisi(int kt)             { this.kuotaTerisi = kt; }
    public void setHarga(double h)                 { this.harga = h; }
    public void setStatus(StatusSeminar s)         { this.status = s; }

    @Override
    public String toString() {
        return "[" + idSeminar + "] " + judul
             + " | " + (tanggalMulai != null ? tanggalMulai.toLocalDate() : "-")
             + " | Sisa: " + getSisaKuota()
             + " | Rp" + String.format("%,.0f", harga)
             + " | " + status;
    }
}
