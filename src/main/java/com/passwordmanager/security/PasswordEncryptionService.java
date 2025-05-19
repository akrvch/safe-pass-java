package com.passwordmanager.security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordEncryptionService {
    private static final String AES_MODE = "AES/CBC/PKCS5Padding";
    private static final int KEY_SIZE = 256;
    private static final String KEY_ALGORITHM = "AES";
    private static final PasswordEncryptionService INSTANCE = new PasswordEncryptionService();
    private static final String KEY_STRING = "12345678901234567890123456789012";
    private static final String IV_STRING = "1234567890123456";
    private static final SecretKey masterKey = new SecretKeySpec(KEY_STRING.getBytes(), KEY_ALGORITHM);
    private static final byte[] iv = IV_STRING.getBytes();

    private PasswordEncryptionService() {
    }

    public static PasswordEncryptionService getInstance() {
        return INSTANCE;
    }

    public String encryptPassword(String password) {
        try {
            Cipher cipher = Cipher.getInstance(AES_MODE);
            cipher.init(Cipher.ENCRYPT_MODE, masterKey, new IvParameterSpec(iv));
            byte[] encrypted = cipher.doFinal(password.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt password", e);
        }
    }

    public String decryptPassword(String encryptedPassword) {
        try {
            Cipher cipher = Cipher.getInstance(AES_MODE);
            cipher.init(Cipher.DECRYPT_MODE, masterKey, new IvParameterSpec(iv));
            byte[] decoded = Base64.getDecoder().decode(encryptedPassword);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt password", e);
        }
    }

    public byte[] getMasterKey() {
        return masterKey.getEncoded();
    }
}
