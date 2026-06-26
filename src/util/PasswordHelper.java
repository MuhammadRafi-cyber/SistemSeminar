package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * PasswordHelper — SHA-256 + salt hashing.
 * Format simpan di DB (kolom password_hash): "salt:hash"
 */
public class PasswordHelper {

    public static String hash(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty())
            throw new IllegalArgumentException("Password tidak boleh kosong saat di-hash.");
        try {
            byte[] saltBytes = new byte[16];
            new SecureRandom().nextBytes(saltBytes);
            String salt   = Base64.getEncoder().encodeToString(saltBytes);
            String hashed = sha256(salt + plainPassword);
            return salt + ":" + hashed;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 tidak tersedia di JVM ini.", e);
        }
    }

    public static boolean verify(String plainPassword, String storedValue) {
        if (plainPassword == null || storedValue == null) return false;
        try {
            String[] parts = storedValue.split(":", 2);
            if (parts.length != 2) return false;
            return sha256(parts[0] + plainPassword).equals(parts[1]);
        } catch (NoSuchAlgorithmException e) {
            return false;
        }
    }

    private static String sha256(String input) throws NoSuchAlgorithmException {
        byte[] bytes = MessageDigest.getInstance("SHA-256").digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
