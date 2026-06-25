package exception;

/**
 * PendaftaranDuplikatException — peserta sudah pernah daftar seminar ini.
 * Dicek via query C1.
 */
public class PendaftaranDuplikatException extends Exception {
    public PendaftaranDuplikatException(int idSeminar) {
        super("Anda sudah terdaftar di seminar #" + idSeminar + ". Tidak bisa mendaftar dua kali.");
    }
}
