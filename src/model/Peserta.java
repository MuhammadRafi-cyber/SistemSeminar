package model;

import enums.Role;
import java.util.ArrayList;
import java.util.List;

/**
 * Peserta — turunan User.
 * Konsep PBO: Inheritance, Polymorphism (override generateLaporan), Collection.
 * generateLaporan() menggunakan query F1 via service (inject dari luar).
 */
public class Peserta extends User {

    private List<Pendaftaran> riwayatPendaftaran;

    // Constructor dari DB
    public Peserta(int idUser, String nama, String email, String passwordHash,
                   String noTelepon, String tanggalDaftar) {
        super(idUser, nama, email, passwordHash, Role.PESERTA, noTelepon, tanggalDaftar);
        this.riwayatPendaftaran = new ArrayList<>();
    }

    // Constructor untuk registrasi baru
    public Peserta(String nama, String email, String passwordHash, String noTelepon) {
        super(nama, email, passwordHash, Role.PESERTA, noTelepon);
        this.riwayatPendaftaran = new ArrayList<>();
    }

    @Override
    public String generateLaporan() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== LAPORAN RIWAYAT SEMINAR PESERTA ===\n");
        sb.append("Nama   : ").append(getNama()).append("\n");
        sb.append("Email  : ").append(getEmail()).append("\n");
        sb.append("---------------------------------------\n");
        if (riwayatPendaftaran.isEmpty()) {
            sb.append("Belum ada pendaftaran seminar.\n");
        } else {
            int no = 1;
            for (Pendaftaran p : riwayatPendaftaran) {
                sb.append(no++).append(". Seminar#").append(p.getIdSeminar())
                  .append(" | ").append(p.getStatusPendaftaran())
                  .append(" | ").append(p.getTanggalDaftar()).append("\n");
            }
        }
        return sb.toString();
    }

    public void tambahPendaftaran(Pendaftaran p)         { riwayatPendaftaran.add(p); }
    public List<Pendaftaran> getRiwayatPendaftaran()     { return riwayatPendaftaran; }
    public void setRiwayatPendaftaran(List<Pendaftaran> l) { this.riwayatPendaftaran = l; }
}
