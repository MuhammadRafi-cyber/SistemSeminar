package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * PasswordHelper — SHA-256 + salt hashing untuk konteks akademik.
 * Format simpan di DB: salt:hash (dipisah titik dua)
 */
public class PasswordHelper {

    /**
     * Hash password dengan salt acak.
     * @return String format "salt:hash" untuk disimpan ke DB
     */
    public static String hash(String plainPassword) {
        try {
            SecureRandom random = new SecureRandom();
            byte[] saltBytes = new byte[16];
            random.nextBytes(saltBytes);
            String salt = Base64.getEncoder().encodeToString(saltBytes);

            String hashed = sha256(salt + plainPassword);
            return salt + ":" + hashed;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 tidak tersedia.", e);
        }
    }

    /**
     * Verifikasi password plain terhadap nilai hash yang tersimpan di DB.
     * @param plainPassword   Password yang diinput user
     * @param storedValue     Nilai dari DB format "salt:hash"
     */
    public static boolean verify(String plainPassword, String storedValue) {
        try {
            String[] parts = storedValue.split(":", 2);
            if (parts.length != 2) return false;

            String salt   = parts[0];
            String stored = parts[1];
            String hashed = sha256(salt + plainPassword);
            return hashed.equals(stored);
        } catch (NoSuchAlgorithmException e) {
            return false;
        }
    }

    private static String sha256(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = digest.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
