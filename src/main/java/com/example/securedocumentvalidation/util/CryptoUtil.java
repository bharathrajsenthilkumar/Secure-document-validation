package com.example.securedocumentvalidation.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Arrays;

public class CryptoUtil {

    private static final String SECRET = "MY_SUPER_SECRET_KEY_123";

    private static SecretKeySpec getKey() {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] key = sha.digest(SECRET.getBytes());
            return new SecretKeySpec(Arrays.copyOf(key, 16), "AES");
        } catch (Exception e) {
            throw new RuntimeException("Failed to create AES key", e);
        }
    }

    public static byte[] encrypt(byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, getKey());
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    public static byte[] decrypt(byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, getKey());
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }
}
