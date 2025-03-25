package com.application.config.mail;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class EmailEncryptionUtil {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";
    private static final String BASE64_ENCODED_KEY = "SUpzS3lXQnhxNThQbWhlcQ=="; // Example key

    private static SecretKeySpec getKey() {
        byte[] decodedKey = Base64.getDecoder().decode(BASE64_ENCODED_KEY);
        return new SecretKeySpec(decodedKey, ALGORITHM);
    }

    public static String encrypt(String input) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, getKey());
        byte[] encryptedBytes = cipher.doFinal(input.getBytes());
        return Base64.getUrlEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String encryptedInput) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, getKey());
        byte[] decryptedBytes = cipher.doFinal(Base64.getUrlDecoder().decode(encryptedInput));
        return new String(decryptedBytes);
    }
}

