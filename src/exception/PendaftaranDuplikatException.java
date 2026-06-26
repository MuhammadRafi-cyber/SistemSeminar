package exception;
public class PendaftaranDuplikatException extends Exception {
    public PendaftaranDuplikatException(String kodeTransaksi) {
        super("Kode transaksi '" + kodeTransaksi + "' sudah digunakan. Coba lagi.");
    }
}
