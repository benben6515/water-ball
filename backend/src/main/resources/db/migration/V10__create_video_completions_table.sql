-- Create video_completions table to track user progress and exp awards
CREATE TABLE IF NOT EXISTS video_completions (
    completion_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    video_id BIGINT NOT NULL REFERENCES videos(video_id) ON DELETE CASCADE,
    exp_awarded INTEGER NOT NULL,
    completed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Ensure idempotent exp awards - one completion per user per video
    CONSTRAINT uq_user_video_completion UNIQUE (user_id, video_id)
);

-- Add indexes for common queries
CREATE INDEX idx_video_completions_user ON video_completions(user_id);
CREATE INDEX idx_video_completions_video ON video_completions(video_id);
CREATE INDEX idx_video_completions_completed_at ON video_completions(completed_at DESC);

-- Add comments
COMMENT ON TABLE video_completions IS 'Tracks video completions for exp rewards (idempotent)';
COMMENT ON COLUMN video_completions.exp_awarded IS 'Exp points awarded for this completion (typically 200)';
COMMENT ON CONSTRAINT uq_user_video_completion ON video_completions IS 'Prevents duplicate exp awards for same video';
