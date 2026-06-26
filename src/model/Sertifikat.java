package model;

/**
 * Sertifikat — FK ke id_detail, sesuai DB v4.
 * Kolom DB: id_sertifikat, id_detail, nomor (UNIQUE), versi, file_path, tanggal_terbit
 */
public class Sertifikat {
    private int    idSertifikat;
    private int    idDetail;
    private String nomor;          // kolom DB: nomor (bukan kode_sertifikat)
    private int    versi;          // baru di v4
    private String filePath;       // baru di v4
    private String tanggalTerbit;

    // Constructor dari DB
    public Sertifikat(int idSertifikat, int idDetail, String nomor,
                      int versi, String filePath, String tanggalTerbit) {
        this.idSertifikat = idSertifikat;
        this.idDetail     = idDetail;
        this.nomor        = nomor;
        this.versi        = versi;
        this.filePath     = filePath;
        this.tanggalTerbit = tanggalTerbit;
    }

    // Constructor untuk INSERT baru
    public Sertifikat(int idDetail, String nomor, String filePath) {
        this.idDetail  = idDetail;
        this.nomor     = nomor;
        this.versi     = 1;
        this.filePath  = filePath;
    }

    public int    getIdSertifikat()  { return idSertifikat; }
    public int    getIdDetail()      { return idDetail; }
    public String getNomor()         { return nomor; }
    public int    getVersi()         { return versi; }
    public String getFilePath()      { return filePath; }
    public String getTanggalTerbit() { return tanggalTerbit; }

    public void setIdSertifikat(int id)  { this.idSertifikat = id; }
    public void setNomor(String n)       { this.nomor = n; }
    public void setVersi(int v)          { this.versi = v; }
    public void setFilePath(String fp)   { this.filePath = fp; }

    @Override
    public String toString() {
        return "Sertifikat[" + nomor + "] v" + versi + " | Detail#" + idDetail;
    }
}
