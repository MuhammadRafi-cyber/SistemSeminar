package controller;

import exception.*;
import model.DetailPendaftaran;
import model.Pembayaran;
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
     * STEP 1: Daftar seminar — buat transaksi PENDING.
     * Pembayaran belum diproses, kuota belum dikurangi.
     */
    public String daftar(int idPemesan, int idSeminar,
                          List<DetailPendaftaran> tiketList, String metodeBayar) {
        try {
            Pendaftaran p = pendaftaranService.daftar(idPemesan, idSeminar, tiketList, metodeBayar);
            StringBuilder sb = new StringBuilder();
            sb.append("SUKSES|Pendaftaran berhasil dibuat!\n");
            sb.append("  Kode Transaksi : ").append(p.getKodeTransaksi()).append("\n");
            sb.append("  Status         : ").append(p.getStatus()).append("\n");
            sb.append("  Total Tagihan  : Rp").append(String.format("%,.0f", p.getTotal())).append("\n");

            if (p.getStatus() == enums.StatusPendaftaran.CONFIRMED) {
                // Seminar gratis — langsung CONFIRMED
                sb.append("  [INFO] Seminar gratis, pendaftaran langsung CONFIRMED.\n");
                sb.append("  Tiket Anda:\n");
                for (DetailPendaftaran d : p.getDetailList())
                    sb.append("    → ").append(d.getNamaPeserta())
                      .append(" | Kode: ").append(d.getKodeBooking()).append("\n");
            } else {
                // Perlu konfirmasi pembayaran
                sb.append("  [INFO] Silakan lakukan pembayaran dengan memilih menu 'Konfirmasi Bayar'.\n");
                sb.append("  Metode tersedia: QRIS | E-Wallet | Virtual Account\n");
            }
            return sb.toString().trim();
        } catch (JumlahTiketTidakValidException | InputKosongException e) {
            return "ERROR|" + e.getMessage();
        } catch (DataTidakDitemukanException e) {
            return "ERROR|" + e.getMessage();
        } catch (KuotaPenuhException e) {
            return "ERROR|" + e.getMessage();
        } catch (SQLException e) {
            return "ERROR|Gagal memproses pendaftaran: " + e.getMessage();
        } catch (Exception e) {
            return "ERROR|Kesalahan tidak terduga: " + e.getMessage();
        }
    }

    /**
     * STEP 2: Konfirmasi pembayaran — user menekan "Bayar Sekarang".
     */
    public String konfirmasiBayar(String kodeTransaksi, String metodeBayar) {
        try {
            Pendaftaran p = pendaftaranService.konfirmasiBayar(kodeTransaksi, metodeBayar);
            StringBuilder sb = new StringBuilder();
            sb.append("SUKSES|Pembayaran berhasil!\n");
            sb.append("  Kode Transaksi : ").append(p.getKodeTransaksi()).append("\n");
            sb.append("  Status         : ").append(p.getStatus()).append("\n");
            sb.append("  Total Dibayar  : Rp").append(String.format("%,.0f", p.getTotal())).append("\n");
            sb.append("  [INFO] Tiket Anda sudah aktif. Gunakan kode booking untuk presensi.\n");
            return sb.toString().trim();
        } catch (DataTidakDitemukanException e) {
            return "ERROR|Transaksi tidak ditemukan: " + e.getMessage();
        } catch (AksesDitolakException e) {
            return "ERROR|" + e.getMessage();
        } catch (PembayaranGagalException e) {
            return "ERROR|Pembayaran gagal: " + e.getMessage()
                 + "\n  [INFO] Silakan coba lagi dengan menu 'Retry Bayar'.";
        } catch (InputKosongException e) {
            return "ERROR|" + e.getMessage();
        } catch (SQLException e) {
            return "ERROR|Gagal memproses pembayaran: " + e.getMessage();
        } catch (Exception e) {
            return "ERROR|Kesalahan tidak terduga: " + e.getMessage();
        }
    }

    /**
     * STEP 3: Retry bayar — coba lagi setelah GAGAL.
     */
    public String retryBayar(String kodeTransaksi, String metodeBayar) {
        try {
            Pendaftaran p = pendaftaranService.retryBayar(kodeTransaksi, metodeBayar);
            return "SUKSES|Retry pembayaran berhasil! Status: " + p.getStatus();
        } catch (DataTidakDitemukanException e) {
            return "ERROR|Transaksi tidak ditemukan: " + e.getMessage();
        } catch (AksesDitolakException e) {
            return "ERROR|" + e.getMessage();
        } catch (PembayaranGagalException e) {
            return "ERROR|Pembayaran gagal lagi: " + e.getMessage()
                 + "\n  [INFO] Coba metode pembayaran lain atau hubungi panitia.";
        } catch (InputKosongException e) {
            return "ERROR|" + e.getMessage();
        } catch (SQLException e) {
            return "ERROR|Gagal retry pembayaran: " + e.getMessage();
        } catch (Exception e) {
            return "ERROR|Kesalahan tidak terduga: " + e.getMessage();
        }
    }

    /**
     * Cek status pembayaran suatu transaksi.
     */
    public String cekStatusPembayaran(String kodeTransaksi) {
        try {
            Pembayaran b = pendaftaranService.getStatusPembayaran(kodeTransaksi);
            if (b == null) return "INFO|Belum ada data pembayaran untuk transaksi ini.";
            return "INFO|Status Pembayaran: " + b.getStatus()
                 + " | Metode: " + b.getMetode()
                 + " | Nominal: Rp" + String.format("%,.0f", b.getNominal())
                 + " | Refund: " + b.getStatusRefund();
        } catch (DataTidakDitemukanException | InputKosongException e) {
            return "ERROR|" + e.getMessage();
        } catch (SQLException e) {
            return "ERROR|Gagal cek status pembayaran: " + e.getMessage();
        } catch (Exception e) {
            return "ERROR|Kesalahan tidak terduga: " + e.getMessage();
        }
    }

    /**
     * Batalkan pendaftaran (dengan atau tanpa refund).
     */
    public String batalkan(int idPendaftaran) {
        try {
            boolean ok = pendaftaranService.batalkan(idPendaftaran);
            return ok ? "SUKSES|Pendaftaran #" + idPendaftaran + " berhasil dibatalkan."
                      : "ERROR|Gagal membatalkan pendaftaran.";
        } catch (DataTidakDitemukanException e) {
            return "ERROR|" + e.getMessage();
        } catch (AksesDitolakException e) {
            return "ERROR|" + e.getMessage();
        } catch (SQLException e) {
            return "ERROR|Gagal membatalkan: " + e.getMessage();
        } catch (Exception e) {
            return "ERROR|Kesalahan tidak terduga: " + e.getMessage();
        }
    }

    /** Riwayat pendaftaran pemesan. */
    public List<Object[]> getRiwayat(int idPemesan) {
        try { return pendaftaranService.getRiwayat(idPemesan); }
        catch (SQLException e) { System.err.println("[ERROR] " + e.getMessage()); return Collections.emptyList(); }
    }

    /** Daftar peserta seminar (untuk Panitia). */
    public List<Object[]> getPesertaSeminar(int idSeminar) {
        try { return pendaftaranService.getPesertaSeminar(idSeminar); }
        catch (SQLException e) { System.err.println("[ERROR] " + e.getMessage()); return Collections.emptyList(); }
    }

    /**
     * Detail tiket berdasarkan KODE TRANSAKSI (bukan ID pendaftaran).
     */
    public List<DetailPendaftaran> getDetailTiket(String kodeTransaksi) {
        try {
            return pendaftaranService.getDetailByKodeTransaksi(kodeTransaksi.trim().toUpperCase());
        } catch (DataTidakDitemukanException e) {
            System.err.println("[INFO] " + e.getMessage());
            return Collections.emptyList();
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
