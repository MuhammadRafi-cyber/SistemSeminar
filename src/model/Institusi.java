package model;

/**
 * Institusi — sesuai DB v4.
 * Kolom: id_institusi, nama, alamat
 * (bukan nama_institusi/jenis_institusi/kota seperti v3)
 */
public class Institusi {
    private int    idInstitusi;
    private String nama;
    private String alamat;

    public Institusi(int idInstitusi, String nama, String alamat) {
        this.idInstitusi = idInstitusi;
        this.nama        = nama;
        this.alamat      = alamat;
    }

    public Institusi(String nama, String alamat) {
        this.nama   = nama;
        this.alamat = alamat;
    }

    public int    getIdInstitusi() { return idInstitusi; }
    public String getNama()        { return nama; }
    public String getAlamat()      { return alamat; }

    public void setIdInstitusi(int id) { this.idInstitusi = id; }
    public void setNama(String nama)   { this.nama = nama; }
    public void setAlamat(String a)    { this.alamat = a; }

    @Override
    public String toString() { return "[" + idInstitusi + "] " + nama; }
}
