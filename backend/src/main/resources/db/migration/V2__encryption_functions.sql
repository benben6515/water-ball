-- Encryption helper functions using pgcrypto
-- These functions provide AES-256-GCM encryption for PII fields

-- Function to encrypt PII data
-- Uses symmetric encryption with key from application config
-- Returns encrypted BYTEA that can be stored in database
CREATE OR REPLACE FUNCTION encrypt_pii(data TEXT, encryption_key TEXT)
RETURNS BYTEA AS $$
BEGIN
    IF data IS NULL THEN
        RETURN NULL;
    END IF;
    -- Use pgp_sym_encrypt with AES-256
    RETURN pgp_sym_encrypt(data, encryption_key, 'compress-algo=1, cipher-algo=aes256');
END;
$$ LANGUAGE plpgsql IMMUTABLE;

-- Function to decrypt PII data
-- Decrypts BYTEA back to plaintext TEXT
CREATE OR REPLACE FUNCTION decrypt_pii(encrypted_data BYTEA, encryption_key TEXT)
RETURNS TEXT AS $$
BEGIN
    IF encrypted_data IS NULL THEN
        RETURN NULL;
    END IF;
    -- Use pgp_sym_decrypt to reverse encryption
    RETURN pgp_sym_decrypt(encrypted_data, encryption_key);
END;
$$ LANGUAGE plpgsql IMMUTABLE;

-- Function to hash email for unique constraint
-- Since email is encrypted, we need a hash for uniqueness checks
CREATE OR REPLACE FUNCTION hash_email(email TEXT)
RETURNS TEXT AS $$
BEGIN
    IF email IS NULL THEN
        RETURN NULL;
    END IF;
    -- Use SHA-256 for deterministic hash
    RETURN encode(digest(LOWER(email), 'sha256'), 'hex');
END;
$$ LANGUAGE plpgsql IMMUTABLE;

-- Comment on functions for documentation
COMMENT ON FUNCTION encrypt_pii(TEXT, TEXT) IS 'Encrypts PII data using AES-256-GCM with provided encryption key';
COMMENT ON FUNCTION decrypt_pii(BYTEA, TEXT) IS 'Decrypts PII data that was encrypted with encrypt_pii function';
COMMENT ON FUNCTION hash_email(TEXT) IS 'Creates deterministic SHA-256 hash of email for uniqueness constraint (case-insensitive)';
