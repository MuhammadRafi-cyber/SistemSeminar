package model;

import enums.Role;
import java.util.ArrayList;
import java.util.List;

/** Panitia — extends User (Inheritance + Polymorphism). */
public class Panitia extends User {
    private List<Seminar> daftarSeminar = new ArrayList<>();

    public Panitia(int idUser, Integer idInstitusi, String nama, String email,
                   String passwordHash, String noTelepon, String tanggalDaftar) {
        super(idUser, idInstitusi, nama, email, passwordHash, Role.PANITIA, noTelepon, tanggalDaftar);
    }

    public Panitia(Integer idInstitusi, String nama, String email,
                   String passwordHash, String noTelepon) {
        super(idInstitusi, nama, email, passwordHash, Role.PANITIA, noTelepon);
    }

    @Override
    public String generateLaporan() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== LAPORAN SEMINAR PANITIA ===\n");
        sb.append("Panitia : ").append(getNama()).append("\n");
        sb.append("─".repeat(60)).append("\n");
        if (daftarSeminar.isEmpty()) {
            sb.append("  Belum ada seminar yang dikelola.\n");
        } else {
            for (Seminar s : daftarSeminar) {
                sb.append("  [").append(s.getIdSeminar()).append("] ")
                  .append(s.getJudul())
                  .append(" | Terisi: ").append(s.getKuotaTerisi())
                  .append("/").append(s.getKuota())
                  .append(" | ").append(s.getStatus())
                  .append("\n");
            }
        }
        sb.append("─".repeat(60)).append("\n");
        return sb.toString();
    }

    public void tambahSeminar(Seminar s)            { daftarSeminar.add(s); }
    public List<Seminar> getDaftarSeminar()         { return daftarSeminar; }
    public void setDaftarSeminar(List<Seminar> l)   { daftarSeminar = l; }
}
