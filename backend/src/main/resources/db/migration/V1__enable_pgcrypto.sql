-- Enable pgcrypto extension for PII encryption
-- This extension provides cryptographic functions including AES-256 encryption

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Verify extension is enabled
SELECT * FROM pg_extension WHERE extname = 'pgcrypto';
