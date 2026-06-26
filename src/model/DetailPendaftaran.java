package model;

/** DetailPendaftaran — tiket per peserta, FK ke pendaftaran. */
public class DetailPendaftaran {
    private int    idDetail;
    private int    idPendaftaran;
    private String namaPeserta;
    private String emailPeserta;
    private String noTelepon;
    private String kodeBooking;   // UNIQUE
    private String qrData;

    // Constructor dari DB
    public DetailPendaftaran(int idDetail, int idPendaftaran, String namaPeserta,
                              String emailPeserta, String noTelepon,
                              String kodeBooking, String qrData) {
        this.idDetail      = idDetail;
        this.idPendaftaran = idPendaftaran;
        this.namaPeserta   = namaPeserta;
        this.emailPeserta  = emailPeserta;
        this.noTelepon     = noTelepon;
        this.kodeBooking   = kodeBooking;
        this.qrData        = qrData;
    }

    // Constructor untuk INSERT baru (id_detail belum ada)
    public DetailPendaftaran(int idPendaftaran, String namaPeserta, String emailPeserta,
                              String noTelepon, String kodeBooking, String qrData) {
        this.idPendaftaran = idPendaftaran;
        this.namaPeserta   = namaPeserta;
        this.emailPeserta  = emailPeserta;
        this.noTelepon     = noTelepon;
        this.kodeBooking   = kodeBooking;
        this.qrData        = qrData;
    }

    public int    getIdDetail()      { return idDetail; }
    public int    getIdPendaftaran() { return idPendaftaran; }
    public String getNamaPeserta()   { return namaPeserta; }
    public String getEmailPeserta()  { return emailPeserta; }
    public String getNoTelepon()     { return noTelepon; }
    public String getKodeBooking()   { return kodeBooking; }
    public String getQrData()        { return qrData; }

    public void setIdDetail(int id)       { this.idDetail = id; }
    public void setNamaPeserta(String n)  { this.namaPeserta = n; }
    public void setEmailPeserta(String e) { this.emailPeserta = e; }

    @Override
    public String toString() {
        return "Tiket[" + kodeBooking + "] " + namaPeserta + " <" + emailPeserta + ">";
    }
}
