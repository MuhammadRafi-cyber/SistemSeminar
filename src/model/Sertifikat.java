package model;

/**
 * Sertifikat — FK ke id_detail, sesuai DB v5.
 * Kolom DB: nomor_sertifikat (PRD tabel 9), versi, file_path.
 */
public class Sertifikat {
    private int    idSertifikat;
    private int    idDetail;
    private String nomorSertifikat;
    private String tanggalTerbit;
    private int    versi;
    private String filePath;

    public Sertifikat(int idSertifikat, int idDetail, String nomorSertifikat,
                      String tanggalTerbit, int versi, String filePath) {
        this.idSertifikat    = idSertifikat;
        this.idDetail        = idDetail;
        this.nomorSertifikat = nomorSertifikat;
        this.tanggalTerbit   = tanggalTerbit;
        this.versi           = versi;
        this.filePath        = filePath;
    }
    public Sertifikat(int idDetail, String nomorSertifikat, String filePath) {
        this.idDetail        = idDetail;
        this.nomorSertifikat = nomorSertifikat;
        this.versi           = 1;
        this.filePath        = filePath;
    }

    public int    getIdSertifikat()    { return idSertifikat; }
    public int    getIdDetail()        { return idDetail; }
    public String getNomorSertifikat() { return nomorSertifikat; }
    public String getTanggalTerbit()   { return tanggalTerbit; }
    public int    getVersi()           { return versi; }
    public String getFilePath()        { return filePath; }

    public void setIdSertifikat(int id)       { this.idSertifikat = id; }
    public void setNomorSertifikat(String n)  { this.nomorSertifikat = n; }
    public void setVersi(int v)               { this.versi = v; }
    public void setFilePath(String fp)        { this.filePath = fp; }

    @Override public String toString() {
        return "Sertifikat[" + nomorSertifikat + "] v" + versi + " | Detail#" + idDetail;
    }
}
