-- V6: Create indexes for oauth_provider_link table
-- Created: 2025-11-15
-- Purpose: Optimize OAuth provider link queries

-- Index for user_id lookups (fetch all OAuth providers for a user)
CREATE INDEX idx_oauth_user_id ON oauth_provider_link(user_id);

-- Index for provider email lookups (account merging by email)
CREATE INDEX idx_oauth_provider_email ON oauth_provider_link(provider_email);

-- Note: Unique index uk_oauth_provider_uid already created by unique constraint in V4
-- This index supports: findByProviderTypeAndProviderUserId queries

-- Add comments
COMMENT ON INDEX idx_oauth_user_id IS 'Fast lookup of all OAuth providers linked to a user';
COMMENT ON INDEX idx_oauth_provider_email IS 'Support account merging queries by email';
