package exception;
public class SertifikatTidakTersediaException extends Exception {
    public SertifikatTidakTersediaException() {
        super("Sertifikat tidak tersedia. Peserta harus berstatus HADIR terlebih dahulu.");
    }
}
