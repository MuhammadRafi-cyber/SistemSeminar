package exception;
public class PresensiDuplikatException extends Exception {
    public PresensiDuplikatException(String kodeBooking) {
        super("Tiket '" + kodeBooking + "' sudah melakukan presensi sebelumnya.");
    }
}
