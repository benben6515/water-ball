-- Create user_video_progress table for tracking video watch progress
CREATE TABLE user_video_progress (
    progress_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    video_id BIGINT NOT NULL,
    watch_percentage DECIMAL(5, 2) NOT NULL DEFAULT 0.00,
    last_position_seconds INT NOT NULL DEFAULT 0,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_user_video_progress_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_user_video_progress_video FOREIGN KEY (video_id) REFERENCES videos(video_id) ON DELETE CASCADE,
    CONSTRAINT unique_user_video_progress UNIQUE (user_id, video_id),
    CONSTRAINT chk_watch_percentage CHECK (watch_percentage >= 0 AND watch_percentage <= 100),
    CONSTRAINT chk_last_position CHECK (last_position_seconds >= 0)
);

-- Create index for efficient progress lookups
CREATE INDEX idx_user_video_progress_user_id ON user_video_progress(user_id);
CREATE INDEX idx_user_video_progress_video_id ON user_video_progress(video_id);

-- Add comment
COMMENT ON TABLE user_video_progress IS 'Tracks user video watch progress for resume functionality';
COMMENT ON COLUMN user_video_progress.watch_percentage IS 'Percentage of video watched (0-100)';
COMMENT ON COLUMN user_video_progress.last_position_seconds IS 'Last watched position in seconds for resume';
