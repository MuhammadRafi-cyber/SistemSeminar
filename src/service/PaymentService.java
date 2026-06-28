package service;

import enums.StatusPembayaran;
import enums.StatusRefund;
import exception.PembayaranGagalException;

/**
 * PaymentService — Interface pembayaran simulasi (wajib UAS PBO).
 * Referensi PRD: Bagian 8.2 (Interface), Checklist UAS.
 *
 * Diimplementasikan oleh DummyPaymentService.
 * Tidak menyimpan data kartu, CVV, OTP, atau data finansial nyata (BR-18, NFR-07).
 */
public interface PaymentService {

    /**
     * Proses pembayaran simulasi.
     * @param idPendaftaran ID transaksi yang dibayar
     * @param nominal       Jumlah yang harus dibayar
     * @param metode        "QRIS" / "E-Wallet" / "Virtual Account"
     * @return StatusPembayaran.BERHASIL atau GAGAL
     * @throws PembayaranGagalException jika simulasi menolak transaksi
     */
    StatusPembayaran prosesPembayaran(int idPendaftaran, double nominal, String metode)
            throws PembayaranGagalException;

    /**
     * Validasi metode dan nominal sebelum proses.
     * @return true jika valid
     */
    boolean validasiPembayaran(double nominal, String metode);

    /**
     * Proses refund dummy — hanya mengubah status_refund, tidak ada uang nyata.
     * @return StatusRefund.SELESAI jika refund dummy berhasil dicatat
     */
    StatusRefund prosesRefund(int idPendaftaran);
}
