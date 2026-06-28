package exception;

/**
 * KodeBookingTidakValidException — kode tidak ditemukan, dibatalkan,
 * atau di luar jendela waktu presensi (T-60 menit s/d seminar selesai).
 * Referensi PRD: Bagian 11 (daftar exception), BR-13, TC-12.
 */
public class KodeBookingTidakValidException extends Exception {
    public KodeBookingTidakValidException(String kode) {
        super("Kode booking '" + kode + "' tidak valid, tidak ditemukan, "
            + "sudah dibatalkan, atau di luar jendela waktu presensi.");
    }
    public KodeBookingTidakValidException(String kode, String alasan) {
        super("Kode booking '" + kode + "' ditolak: " + alasan);
    }
}
