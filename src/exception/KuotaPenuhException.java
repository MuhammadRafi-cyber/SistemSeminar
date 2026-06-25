package exception;

public class KuotaPenuhException extends Exception {
    public KuotaPenuhException() {
        super("Kuota seminar sudah penuh. Tidak dapat mendaftar.");
    }

    public KuotaPenuhException(int sisaKuota) {
        super("Kuota tidak mencukupi. Sisa kuota: " + sisaKuota + " tiket.");
    }
}
