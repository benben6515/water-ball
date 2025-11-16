-- V5: Create indexes for users table
-- Created: 2025-11-15
-- Purpose: Optimize user queries (email lookup, level sorting, creation date sorting)

-- Index for email hash lookups (used for finding existing users by email during OAuth)
CREATE INDEX idx_user_email_hash ON users(email_hash);

-- Index for level-based queries (leaderboards, level filtering)
CREATE INDEX idx_user_level ON users(level DESC);

-- Index for creation date sorting (newest users, registration reports)
CREATE INDEX idx_user_created_at ON users(created_at DESC);

-- Composite index for common query pattern: active users by level
CREATE INDEX idx_user_level_created ON users(level DESC, created_at DESC);

-- Add comments
COMMENT ON INDEX idx_user_email_hash IS 'Fast email lookups during OAuth registration/login';
COMMENT ON INDEX idx_user_level IS 'Support leaderboard and level-based queries';
COMMENT ON INDEX idx_user_created_at IS 'Support newest users queries and date filtering';
COMMENT ON INDEX idx_user_level_created IS 'Composite index for level+date queries';
