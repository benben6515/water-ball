-- V3: Create users table with PII encryption
-- Created: 2025-11-15
-- Purpose: Store user profile data with encrypted PII fields

CREATE TABLE users (
    user_id BIGSERIAL PRIMARY KEY,
    nickname VARCHAR(50) NOT NULL,
    email_encrypted BYTEA NOT NULL,  -- Encrypted with pgcrypto
    email_hash VARCHAR(64) NOT NULL UNIQUE,  -- SHA-256 hash for uniqueness constraint
    gender VARCHAR(20),  -- 男性, 女性, 其他, 不透露
    birthday_encrypted BYTEA,  -- Encrypted date
    location_encrypted BYTEA,  -- Encrypted location string
    occupation VARCHAR(100),
    github_link VARCHAR(255),
    level INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Add comment for documentation
COMMENT ON TABLE users IS 'User profile data with encrypted PII fields (email, birthday, location)';
COMMENT ON COLUMN users.email_encrypted IS 'AES-256-GCM encrypted email using pgcrypto';
COMMENT ON COLUMN users.email_hash IS 'SHA-256 hash of email for uniqueness constraint and lookups';
COMMENT ON COLUMN users.birthday_encrypted IS 'AES-256-GCM encrypted birthday';
COMMENT ON COLUMN users.location_encrypted IS 'AES-256-GCM encrypted location';
