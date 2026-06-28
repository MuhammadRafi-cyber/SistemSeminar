package model;

public class Institusi {
    private int    idInstitusi;
    private String nama;
    private String alamat;
    private String logoPath;   // [TAMBAHAN v5]

    public Institusi(int idInstitusi, String nama, String alamat, String logoPath) {
        this.idInstitusi = idInstitusi;
        this.nama        = nama;
        this.alamat      = alamat;
        this.logoPath    = logoPath;
    }
    public Institusi(String nama, String alamat) {
        this.nama   = nama;
        this.alamat = alamat;
    }

    public int    getIdInstitusi() { return idInstitusi; }
    public String getNama()        { return nama; }
    public String getAlamat()      { return alamat; }
    public String getLogoPath()    { return logoPath; }

    public void setIdInstitusi(int id) { this.idInstitusi = id; }
    public void setNama(String n)      { this.nama = n; }
    public void setAlamat(String a)    { this.alamat = a; }
    public void setLogoPath(String lp) { this.logoPath = lp; }

    @Override public String toString() { return "[" + idInstitusi + "] " + nama; }
}
