package exception;

public class KodeBookingTidakValidException extends Exception {
    public KodeBookingTidakValidException() {
        super("Kode booking tidak ditemukan atau tidak valid.");
    }

    public KodeBookingTidakValidException(String kode) {
        super("Kode booking '" + kode + "' tidak ditemukan, sudah dibatalkan, atau di luar jendela waktu presensi.");
    }
}
