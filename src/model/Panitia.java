package model;

import enums.Role;

import java.util.ArrayList;
import java.util.List;

/**
 * Panitia — turunan User.
 * Konsep PBO: Inheritance, Polymorphism (override generateLaporan), Collection (ArrayList).
 */
public class Panitia extends User {

    // === Collection seminar yang dikelola panitia ini ===
    private List<Seminar> daftarSeminar;

    // === Constructor lengkap (dari DB) ===
    public Panitia(int idUser, String nama, String email, String passwordHash,
                   String noTelepon, String tanggalDaftar) {
        super(idUser, nama, email, passwordHash, Role.PANITIA, noTelepon, tanggalDaftar);
        this.daftarSeminar = new ArrayList<>();
    }

    // Constructor untuk insert baru
    public Panitia(String nama, String email, String passwordHash, String noTelepon) {
        super(nama, email, passwordHash, Role.PANITIA, noTelepon);
        this.daftarSeminar = new ArrayList<>();
    }

    // === Polymorphism: override generateLaporan ===
    @Override
    public String generateLaporan() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== LAPORAN SEMINAR PANITIA ===\n");
        sb.append("Panitia : ").append(getNama()).append("\n");
        sb.append("Email   : ").append(getEmail()).append("\n");
        sb.append("-------------------------------\n");

        if (daftarSeminar.isEmpty()) {
            sb.append("Belum ada seminar yang dikelola.\n");
        } else {
            int no = 1;
            for (Seminar s : daftarSeminar) {
                sb.append(no++).append(". ")
                  .append(s.getJudul())
                  .append(" | Kuota: ").append(s.getKuotaTerisi()).append("/").append(s.getKuota())
                  .append(" | Status: ").append(s.getStatus())
                  .append("\n");
            }
        }
        return sb.toString();
    }

    // === Collection helpers ===
    public void tambahSeminar(Seminar s) {
        daftarSeminar.add(s);
    }

    public List<Seminar> getDaftarSeminar() {
        return daftarSeminar;
    }

    public void setDaftarSeminar(List<Seminar> list) {
        this.daftarSeminar = list;
    }
}
