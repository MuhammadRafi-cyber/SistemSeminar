package util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * KodeGenerator — menghasilkan kode unik untuk transaksi, booking, dan sertifikat.
 * Sesuai kolom DB v4:
 *   - pendaftaran.kode_transaksi
 *   - detail_pendaftaran.kode_booking
 *   - sertifikat.nomor
 */
public class KodeGenerator {

    private static final DateTimeFormatter FMT_TANGGAL = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter FMT_WAKTU   = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /** Kode transaksi: TRX-YYYYMMDDHHMMSS-XXXXX */
    public static String generateKodeTransaksi() {
        String waktu = LocalDateTime.now().format(FMT_WAKTU);
        String unik  = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        return "TRX-" + waktu + "-" + unik;
    }

    /** Kode booking tiket: BOOK-YYYYMMDD-XXXXX */
    public static String generateKodeBooking() {
        String tgl  = LocalDateTime.now().format(FMT_TANGGAL);
        String unik = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        return "BOOK-" + tgl + "-" + unik;
    }

    /** Nomor sertifikat: CERT-YYYY-XXXXXXXX */
    public static String generateNomorSertifikat() {
        int    tahun = LocalDateTime.now().getYear();
        String unik  = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "CERT-" + tahun + "-" + unik;
    }

    /** QR data — gabungan kode booking dan email peserta */
    public static String generateQrData(String kodeBooking, String emailPeserta) {
        return kodeBooking + "|" + emailPeserta;
    }
}
