package exception;

public class PembayaranGagalException extends Exception {
    public PembayaranGagalException() {
        super("Pembayaran gagal. Silakan coba lagi.");
    }

    public PembayaranGagalException(String alasan) {
        super("Pembayaran gagal: " + alasan);
    }
}
