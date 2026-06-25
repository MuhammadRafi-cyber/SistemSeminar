package exception;

public class TanggalTidakValidException extends Exception {
    public TanggalTidakValidException() {
        super("Tanggal tidak valid. Tanggal mulai tidak boleh di masa lalu dan tanggal selesai harus setelah tanggal mulai.");
    }

    public TanggalTidakValidException(String pesan) {
        super(pesan);
    }
}
