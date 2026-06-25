package exception;

public class PresensiDuplikatException extends Exception {
    public PresensiDuplikatException(int idPendaftaran) {
        super("Pendaftaran #" + idPendaftaran + " sudah melakukan presensi sebelumnya.");
    }
}
