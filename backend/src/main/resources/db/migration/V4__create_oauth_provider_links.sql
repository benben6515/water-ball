-- V4: Create oauth_provider_link table
-- Created: 2025-11-15
-- Purpose: Link users to OAuth providers (Google, Facebook) for authentication

CREATE TABLE oauth_provider_link (
    provider_link_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    provider_type VARCHAR(20) NOT NULL,  -- google, facebook
    provider_user_id VARCHAR(255) NOT NULL,  -- OAuth provider's unique user ID
    provider_email VARCHAR(255) NOT NULL,  -- Email from OAuth provider
    linked_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign key with RESTRICT to prevent deletion of users with OAuth links
    CONSTRAINT fk_oauth_user FOREIGN KEY (user_id)
        REFERENCES users(user_id) ON DELETE RESTRICT,

    -- Unique constraint: one provider account can only link to one user
    CONSTRAINT uk_oauth_provider_uid UNIQUE (provider_type, provider_user_id)
);

-- Add comments for documentation
COMMENT ON TABLE oauth_provider_link IS 'Links users to OAuth providers (Google, Facebook)';
COMMENT ON COLUMN oauth_provider_link.provider_type IS 'OAuth provider: google, facebook';
COMMENT ON COLUMN oauth_provider_link.provider_user_id IS 'Unique user ID from OAuth provider';
COMMENT ON COLUMN oauth_provider_link.provider_email IS 'Email address from OAuth provider for account matching';
