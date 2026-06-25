package util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * KodeGenerator — generate kode unik untuk sertifikat.
 * Sesuai DB tim: hanya kode_sertifikat yang dibutuhkan.
 */
public class KodeGenerator {

    /**
     * Kode sertifikat: CERT-YYYY-XXXXX
     * Contoh: CERT-2026-A3F91
     * Dipakai di E1: INSERT INTO sertifikat (id_pendaftaran, kode_sertifikat)
     */
    public static String generateNomorSertifikat() {
        int tahun = LocalDateTime.now().getYear();
        String unik = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        return "CERT-" + tahun + "-" + unik;
    }
}
