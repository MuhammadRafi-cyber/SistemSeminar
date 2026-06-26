package controller;

import exception.*;
import model.DetailPendaftaran;
import model.Pendaftaran;
import service.PendaftaranService;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class PendaftaranController {
    private final PendaftaranService pendaftaranService;

    public PendaftaranController(PendaftaranService pendaftaranService) {
        this.pendaftaranService = pendaftaranService;
    }

    /**
     * Daftar seminar.
     * Return SUKSES juga menyertakan daftar kode booking tiket.
     */
    public String daftar(int idPemesan, int idSeminar,
                          List<DetailPendaftaran> tiketList, String metodeBayar) {
        try {
            Pendaftaran p = pendaftaranService.daftar(idPemesan, idSeminar, tiketList, metodeBayar);
            StringBuilder sb = new StringBuilder();
            sb.append("SUKSES|Pendaftaran berhasil!\n");
            sb.append("  Kode Transaksi : ").append(p.getKodeTransaksi()).append("\n");
            sb.append("  Status         : ").append(p.getStatus()).append("\n");
            sb.append("  Total          : Rp").append(String.format("%,.0f", p.getTotal())).append("\n");
            if (p.getStatus() == enums.StatusPendaftaran.CONFIRMED) {
                sb.append("  Tiket Anda:\n");
                for (DetailPendaftaran d : p.getDetailList()) {
                    sb.append("    → ").append(d.getNamaPeserta())
                      .append(" | Kode: ").append(d.getKodeBooking()).append("\n");
                }
            } else {
                sb.append("  [!] Pembayaran gagal. Status PENDING. Silakan coba metode lain.\n");
            }
            return sb.toString().trim();
        } catch (JumlahTiketTidakValidException e) {
            return "ERROR|Jumlah tiket tidak valid: " + e.getMessage();
        } catch (InputKosongException e) {
            return "ERROR|Data tidak lengkap: " + e.getMessage();
        } catch (DataTidakDitemukanException e) {
            return "ERROR|" + e.getMessage();
        } catch (KuotaPenuhException e) {
            return "ERROR|Kuota tidak mencukupi: " + e.getMessage();
        } catch (SQLException e) {
            return "ERROR|Gagal memproses pendaftaran. Detail: " + e.getMessage();
        } catch (Exception e) {
            return "ERROR|Terjadi kesalahan tidak terduga: " + e.getMessage();
        }
    }

    public String batalkan(int idPendaftaran) {
        try {
            boolean ok = pendaftaranService.batalkan(idPendaftaran);
            return ok ? "SUKSES|Pendaftaran #" + idPendaftaran + " berhasil dibatalkan. Kuota telah dikembalikan."
                      : "ERROR|Gagal membatalkan pendaftaran.";
        } catch (DataTidakDitemukanException e) {
            return "ERROR|" + e.getMessage();
        } catch (AksesDitolakException e) {
            return "ERROR|" + e.getMessage();
        } catch (SQLException e) {
            return "ERROR|Gagal membatalkan pendaftaran. Detail: " + e.getMessage();
        } catch (Exception e) {
            return "ERROR|Terjadi kesalahan tidak terduga: " + e.getMessage();
        }
    }

    /** C5: riwayat pendaftaran pemesan [{id, kodeTransaksi, judul, tanggalMulai, status, total}] */
    public List<Object[]> getRiwayat(int idPemesan) {
        try { return pendaftaranService.getRiwayat(idPemesan); }
        catch (SQLException e) {
            System.err.println("[ERROR] Gagal load riwayat: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /** C4: daftar peserta seminar [{id, nama, email, kodeTransaksi, tgl, status, total}] */
    public List<Object[]> getPesertaSeminar(int idSeminar) {
        try { return pendaftaranService.getPesertaSeminar(idSeminar); }
        catch (SQLException e) {
            System.err.println("[ERROR] Gagal load peserta: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<DetailPendaftaran> getDetailTiket(int idPendaftaran) {
        try { return pendaftaranService.getDetailTiket(idPendaftaran); }
        catch (SQLException e) {
            System.err.println("[ERROR] Gagal load detail tiket: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
