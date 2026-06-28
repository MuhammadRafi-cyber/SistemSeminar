package exception;

/**
 * PembayaranGagalException — dilempar saat simulasi pembayaran gagal.
 * Referensi PRD: Bagian 11 (daftar exception), TC-09.
 * Digunakan oleh PaymentService.prosesPembayaran().
 */
public class PembayaranGagalException extends Exception {
    public PembayaranGagalException() {
        super("Pembayaran gagal. Silakan coba lagi atau pilih metode pembayaran lain.");
    }
    public PembayaranGagalException(String alasan) {
        super("Pembayaran gagal: " + alasan);
    }
}
