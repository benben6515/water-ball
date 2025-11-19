-- Add exp column to users table
-- Experience points system for gamification

ALTER TABLE users
ADD COLUMN IF NOT EXISTS exp INTEGER NOT NULL DEFAULT 0;

-- Add index on exp for leaderboard queries
CREATE INDEX IF NOT EXISTS idx_users_exp ON users(exp DESC);

-- Add comment
COMMENT ON COLUMN users.exp IS 'Total experience points accumulated by the user';
