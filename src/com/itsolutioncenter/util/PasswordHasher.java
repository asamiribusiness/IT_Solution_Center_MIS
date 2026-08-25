package com.itsolutioncenter.util;

/**
 *
 * @author Ahmad Shafiq Amiri
 */
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {
private static final int BCRYPT_ROUNDS = 16;   
    // Generate a random salt (to prevent rainbow table attacks)
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16]; // 16 bytes = 128 bits
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
   
    // Hash password with SHA-256 and salt
    public static String hashPassword(String password, String salt) {
        try {
            // Create SHA-256 MessageDigest
            MessageDigest md = MessageDigest.getInstance("SHA-256");
           
            // Add salt to the digest
            md.update(salt.getBytes());
           
            // Add password and compute hash
            byte[] hashedBytes = md.digest(password.getBytes());
           
            // Convert bytes to hexadecimal string
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                // Convert byte to hex (0-9, a-f)
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
           
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is always available in Java, but we handle it anyway
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
   
    // Verify password (compare input with stored hash)
    public static boolean verifyPassword(String inputPassword, String storedHash, String storedSalt) {
        // Hash the input with the stored salt
        String hashedInput = hashPassword(inputPassword, storedSalt);
       
        // Compare with stored hash (time-safe comparison)
       return MessageDigest.isEqual(hexStringToByteArray(hashedInput),hexStringToByteArray(storedHash)   
        );
    }
   
    // Helper: Convert hex string to byte array
    private static byte[] hexStringToByteArray(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                                + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
       //////////-------------Password Valiedation----------------
    public static String hashPassword(String plainPassword) {
            
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));//(BCRYPT_ROUNDS)
    }
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
    public static boolean isPasswordStrong(String password) {
        if (password.length() < 8) return false;
        if (!password.matches(".*[A-Z].*")) return false;
        if (!password.matches(".*[a-z].*")) return false;
        if (!password.matches(".*\\d.*")) return false;
        if (!password.matches(".*[!@#$%^&*()].*")) return false;
        return true;
    }
}
