package model;

import enums.Role;
import java.util.ArrayList;
import java.util.List;

/** Peserta — extends User (Inheritance + Polymorphism). */
public class Peserta extends User {
    private List<Pendaftaran> riwayatPendaftaran = new ArrayList<>();

    public Peserta(int idUser, Integer idInstitusi, String nama, String username,
                   String email, String passwordHash, String noTelepon, String tanggalDaftar) {
        super(idUser, idInstitusi, nama, username, email, passwordHash, Role.PESERTA, noTelepon, tanggalDaftar);
    }
    public Peserta(Integer idInstitusi, String nama, String username,
                   String email, String passwordHash, String noTelepon) {
        super(idInstitusi, nama, username, email, passwordHash, Role.PESERTA, noTelepon);
    }

    @Override
    public String generateLaporan() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== LAPORAN RIWAYAT SEMINAR PESERTA ===\n");
        sb.append("Peserta  : ").append(getNama()).append(" <").append(getEmail()).append(">\n");
        sb.append("Username : @").append(getUsername() != null ? getUsername() : "-").append("\n");
        sb.append("─".repeat(65)).append("\n");
        if (riwayatPendaftaran.isEmpty()) {
            sb.append("  Belum ada riwayat pendaftaran seminar.\n");
        } else {
            sb.append(String.format("  %-20s %-15s %-12s %s%n",
                "Kode Transaksi","Seminar#","Status","Total"));
            for (Pendaftaran p : riwayatPendaftaran) {
                sb.append(String.format("  %-20s %-15d %-12s Rp%,.0f%n",
                    p.getKodeTransaksi(), p.getIdSeminar(),
                    p.getStatus(), p.getTotal()));
            }
        }
        sb.append("─".repeat(65)).append("\n");
        return sb.toString();
    }

    public void tambahPendaftaran(Pendaftaran p)            { riwayatPendaftaran.add(p); }
    public List<Pendaftaran> getRiwayatPendaftaran()        { return riwayatPendaftaran; }
    public void setRiwayatPendaftaran(List<Pendaftaran> l)  { riwayatPendaftaran = l; }
}
