package exception;
public class JumlahTiketTidakValidException extends Exception {
    public JumlahTiketTidakValidException() {
        super("Jumlah tiket harus antara 1 sampai 4 per transaksi.");
    }
}
