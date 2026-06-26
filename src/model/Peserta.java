package model;

import enums.Role;
import java.util.ArrayList;
import java.util.List;

/** Peserta — extends User (Inheritance + Polymorphism). */
public class Peserta extends User {
    private List<Pendaftaran> riwayatPendaftaran = new ArrayList<>();

    public Peserta(int idUser, Integer idInstitusi, String nama, String email,
                   String passwordHash, String noTelepon, String tanggalDaftar) {
        super(idUser, idInstitusi, nama, email, passwordHash, Role.PESERTA, noTelepon, tanggalDaftar);
    }

    public Peserta(Integer idInstitusi, String nama, String email,
                   String passwordHash, String noTelepon) {
        super(idInstitusi, nama, email, passwordHash, Role.PESERTA, noTelepon);
    }

    @Override
    public String generateLaporan() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== LAPORAN RIWAYAT SEMINAR ===\n");
        sb.append("Peserta : ").append(getNama()).append(" <").append(getEmail()).append(">\n");
        sb.append("─".repeat(60)).append("\n");
        if (riwayatPendaftaran.isEmpty()) {
            sb.append("  Belum ada riwayat pendaftaran.\n");
        } else {
            for (Pendaftaran p : riwayatPendaftaran) {
                sb.append("  [").append(p.getIdPendaftaran()).append("] ")
                  .append(p.getKodeTransaksi())
                  .append(" | Seminar#").append(p.getIdSeminar())
                  .append(" | ").append(p.getStatus())
                  .append("\n");
            }
        }
        sb.append("─".repeat(60)).append("\n");
        return sb.toString();
    }

    public void tambahPendaftaran(Pendaftaran p)            { riwayatPendaftaran.add(p); }
    public List<Pendaftaran> getRiwayatPendaftaran()        { return riwayatPendaftaran; }
    public void setRiwayatPendaftaran(List<Pendaftaran> l)  { riwayatPendaftaran = l; }
}
