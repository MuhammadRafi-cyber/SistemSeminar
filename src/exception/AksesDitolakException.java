package exception;
public class AksesDitolakException extends Exception {
    public AksesDitolakException() {
        super("Akses ditolak. Anda tidak memiliki izin untuk operasi ini.");
    }
    public AksesDitolakException(String pesan) {
        super(pesan);
    }
}
