package model;

import enums.ModeSeminar;
import enums.StatusSeminar;
import java.time.LocalDateTime;

/**
 * Seminar — sesuai PRD tabel 9 dan DB v5.
 * Kolom baru v5: id_kategori, pembicara, mode (ONLINE/OFFLINE), banner_path.
 */
public class Seminar {
    private int           idSeminar;
    private int           idInstitusi;
    private int           idPanitia;
    private Integer       idKategori;     // nullable FK
    private String        judul;
    private String        deskripsi;
    private String        pembicara;      // [TAMBAHAN v5]
    private LocalDateTime tanggalMulai;
    private LocalDateTime tanggalSelesai;
    private ModeSeminar   mode;           // [TAMBAHAN v5] ONLINE/OFFLINE
    private String        lokasi;
    private int           kuota;
    private int           kuotaTerisi;
    private double        harga;
    private StatusSeminar status;
    private String        bannerPath;     // [TAMBAHAN v5]
    private String        dibuatPada;

    // Constructor dari DB (full)
    public Seminar(int idSeminar, int idInstitusi, int idPanitia, Integer idKategori,
                   String judul, String deskripsi, String pembicara,
                   LocalDateTime tanggalMulai, LocalDateTime tanggalSelesai,
                   ModeSeminar mode, String lokasi, int kuota, int kuotaTerisi,
                   double harga, StatusSeminar status, String bannerPath, String dibuatPada) {
        this.idSeminar      = idSeminar;
        this.idInstitusi    = idInstitusi;
        this.idPanitia      = idPanitia;
        this.idKategori     = idKategori;
        this.judul          = judul;
        this.deskripsi      = deskripsi;
        this.pembicara      = pembicara;
        this.tanggalMulai   = tanggalMulai;
        this.tanggalSelesai = tanggalSelesai;
        this.mode           = mode;
        this.lokasi         = lokasi;
        this.kuota          = kuota;
        this.kuotaTerisi    = kuotaTerisi;
        this.harga          = harga;
        this.status         = status;
        this.bannerPath     = bannerPath;
        this.dibuatPada     = dibuatPada;
    }

    // Constructor untuk INSERT baru
    public Seminar(int idInstitusi, int idPanitia, Integer idKategori,
                   String judul, String deskripsi, String pembicara,
                   LocalDateTime tanggalMulai, LocalDateTime tanggalSelesai,
                   ModeSeminar mode, String lokasi, int kuota, double harga) {
        this.idInstitusi    = idInstitusi;
        this.idPanitia      = idPanitia;
        this.idKategori     = idKategori;
        this.judul          = judul;
        this.deskripsi      = deskripsi;
        this.pembicara      = pembicara;
        this.tanggalMulai   = tanggalMulai;
        this.tanggalSelesai = tanggalSelesai;
        this.mode           = mode != null ? mode : ModeSeminar.OFFLINE;
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
    public Integer       getIdKategori()     { return idKategori; }
    public String        getJudul()          { return judul; }
    public String        getDeskripsi()      { return deskripsi; }
    public String        getPembicara()      { return pembicara; }
    public LocalDateTime getTanggalMulai()   { return tanggalMulai; }
    public LocalDateTime getTanggalSelesai() { return tanggalSelesai; }
    public ModeSeminar   getMode()           { return mode; }
    public String        getLokasi()         { return lokasi; }
    public int           getKuota()          { return kuota; }
    public int           getKuotaTerisi()    { return kuotaTerisi; }
    public int           getSisaKuota()      { return kuota - kuotaTerisi; }
    public double        getHarga()          { return harga; }
    public StatusSeminar getStatus()         { return status; }
    public String        getBannerPath()     { return bannerPath; }
    public String        getDibuatPada()     { return dibuatPada; }
    public boolean       kuotaPenuh()        { return kuotaTerisi >= kuota; }
    public boolean       isGratis()          { return harga <= 0; }

    // Setters
    public void setIdSeminar(int id)               { this.idSeminar = id; }
    public void setIdKategori(Integer id)          { this.idKategori = id; }
    public void setJudul(String j)                 { this.judul = j; }
    public void setDeskripsi(String d)             { this.deskripsi = d; }
    public void setPembicara(String p)             { this.pembicara = p; }
    public void setTanggalMulai(LocalDateTime t)   { this.tanggalMulai = t; }
    public void setTanggalSelesai(LocalDateTime t) { this.tanggalSelesai = t; }
    public void setMode(ModeSeminar m)             { this.mode = m; }
    public void setLokasi(String l)                { this.lokasi = l; }
    public void setKuota(int k)                    { this.kuota = k; }
    public void setKuotaTerisi(int kt)             { this.kuotaTerisi = kt; }
    public void setHarga(double h)                 { this.harga = h; }
    public void setStatus(StatusSeminar s)         { this.status = s; }
    public void setBannerPath(String bp)           { this.bannerPath = bp; }
    public void setIdInstitusi(int id)             { this.idInstitusi = id; }

    @Override
    public String toString() {
        return "[" + idSeminar + "] " + judul
             + " | " + (tanggalMulai != null ? tanggalMulai.toLocalDate() : "-")
             + " | " + mode
             + " | Sisa: " + getSisaKuota()
             + " | " + (isGratis() ? "GRATIS" : "Rp" + String.format("%,.0f", harga))
             + " | " + status;
    }
}
