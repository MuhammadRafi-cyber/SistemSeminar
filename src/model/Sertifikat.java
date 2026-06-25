package model;

/**
 * Sertifikat — sesuai DB tim.
 * Tabel: sertifikat(id_sertifikat, id_pendaftaran, kode_sertifikat, tanggal_terbit)
 * FK ke pendaftaran (bukan ke detail_pendaftaran). Tidak ada kolom versi.
 */
public class Sertifikat {

    private int    idSertifikat;
    private int    idPendaftaran;    // FK ke pendaftaran.id_pendaftaran
    private String kodeSertifikat;  // nama kolom di DB: kode_sertifikat
    private String tanggalTerbit;

    // Constructor dari DB
    public Sertifikat(int idSertifikat, int idPendaftaran,
                      String kodeSertifikat, String tanggalTerbit) {
        this.idSertifikat  = idSertifikat;
        this.idPendaftaran = idPendaftaran;
        this.kodeSertifikat = kodeSertifikat;
        this.tanggalTerbit  = tanggalTerbit;
    }

    // Constructor untuk INSERT baru (E1: INSERT INTO sertifikat (id_pendaftaran, kode_sertifikat))
    public Sertifikat(int idPendaftaran, String kodeSertifikat) {
        this.idPendaftaran  = idPendaftaran;
        this.kodeSertifikat = kodeSertifikat;
    }

    // Getters
    public int    getIdSertifikat()   { return idSertifikat; }
    public int    getIdPendaftaran()  { return idPendaftaran; }
    public String getKodeSertifikat() { return kodeSertifikat; }
    public String getTanggalTerbit()  { return tanggalTerbit; }

    // Setters
    public void setIdSertifikat(int id)        { this.idSertifikat = id; }
    public void setKodeSertifikat(String kode) { this.kodeSertifikat = kode; }

    @Override
    public String toString() {
        return "Sertifikat[" + kodeSertifikat + "] Pendaftaran#" + idPendaftaran;
    }
}
