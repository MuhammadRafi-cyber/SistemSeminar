package exception;
public class KuotaPenuhException extends Exception {
    public KuotaPenuhException() {
        super("Kuota seminar sudah penuh. Tidak dapat mendaftar.");
    }
    public KuotaPenuhException(int sisa) {
        super("Kuota tidak mencukupi. Sisa slot: " + sisa + ".");
    }
}
