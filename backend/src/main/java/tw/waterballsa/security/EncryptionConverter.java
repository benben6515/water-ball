package tw.waterballsa.security;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * JPA AttributeConverter for encrypting PII fields using AES-256-GCM.
 * This converter handles encryption/decryption of sensitive data before storing in PostgreSQL BYTEA columns.
 *
 * Encryption format: [12-byte IV][encrypted data][16-byte GCM tag]
 *
 * @author Water Ball SA
 */
@Component
@Converter
public class EncryptionConverter implements AttributeConverter<String, byte[]> {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128; // bits
    private static final int IV_LENGTH = 12; // bytes (96 bits recommended for GCM)

    private static String encryptionKey;

    /**
     * Inject encryption key from application configuration.
     * The key is expected to be a base64-encoded 256-bit (32-byte) key.
     */
    @Value("${app.encryption.key}")
    public void setEncryptionKey(String key) {
        EncryptionConverter.encryptionKey = key;
    }

    /**
     * Convert String attribute to encrypted byte array for database storage.
     *
     * @param attribute the plaintext string to encrypt
     * @return encrypted byte array (IV + ciphertext + GCM tag), or null if input is null
     */
    @Override
    public byte[] convertToDatabaseColumn(String attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }

        try {
            SecretKey key = getSecretKey();
            Cipher cipher = Cipher.getInstance(ALGORITHM);

            // Generate random IV for each encryption (GCM requires unique IV per encryption)
            byte[] iv = new byte[IV_LENGTH];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);

            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);

            byte[] encryptedData = cipher.doFinal(attribute.getBytes(StandardCharsets.UTF_8));

            // Combine IV and encrypted data: [IV][ciphertext+tag]
            ByteBuffer byteBuffer = ByteBuffer.allocate(IV_LENGTH + encryptedData.length);
            byteBuffer.put(iv);
            byteBuffer.put(encryptedData);

            return byteBuffer.array();

        } catch (Exception e) {
            throw new RuntimeException("Error encrypting data", e);
        }
    }

    /**
     * Convert encrypted byte array from database to plaintext String.
     *
     * @param dbData encrypted byte array from database (IV + ciphertext + GCM tag)
     * @return decrypted plaintext string, or null if input is null
     */
    @Override
    public String convertToEntityAttribute(byte[] dbData) {
        if (dbData == null || dbData.length == 0) {
            return null;
        }

        try {
            SecretKey key = getSecretKey();
            Cipher cipher = Cipher.getInstance(ALGORITHM);

            // Extract IV and encrypted data
            ByteBuffer byteBuffer = ByteBuffer.wrap(dbData);
            byte[] iv = new byte[IV_LENGTH];
            byteBuffer.get(iv);

            byte[] encryptedData = new byte[byteBuffer.remaining()];
            byteBuffer.get(encryptedData);

            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);

            byte[] decryptedData = cipher.doFinal(encryptedData);

            return new String(decryptedData, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new RuntimeException("Error decrypting data", e);
        }
    }

    /**
     * Derive AES-256 secret key from the configured encryption key.
     * Uses SHA-256 to ensure the key is exactly 256 bits.
     *
     * @return SecretKey for AES-256 encryption
     */
    private SecretKey getSecretKey() {
        try {
            if (encryptionKey == null || encryptionKey.isEmpty()) {
                throw new IllegalStateException(
                    "Encryption key not configured. Set app.encryption.key in application.yml or APP_ENCRYPTION_KEY environment variable."
                );
            }

            // Hash the key to ensure it's exactly 256 bits (32 bytes)
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = digest.digest(encryptionKey.getBytes(StandardCharsets.UTF_8));

            return new SecretKeySpec(keyBytes, "AES");

        } catch (Exception e) {
            throw new RuntimeException("Error creating secret key", e);
        }
    }
}
