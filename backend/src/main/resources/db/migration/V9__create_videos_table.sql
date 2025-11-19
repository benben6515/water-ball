-- Create videos table for learning content
CREATE TABLE IF NOT EXISTS videos (
    video_id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    duration_seconds INTEGER NOT NULL,
    video_url VARCHAR(500) NOT NULL,
    thumbnail_url VARCHAR(500),
    course_id BIGINT,
    chapter_number INTEGER,
    order_index INTEGER NOT NULL,
    exp_reward INTEGER NOT NULL DEFAULT 200,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Add indexes for common queries
CREATE INDEX idx_videos_course_id ON videos(course_id);
CREATE INDEX idx_videos_chapter ON videos(course_id, chapter_number);
CREATE INDEX idx_videos_order ON videos(course_id, chapter_number, order_index);

-- Add comments
COMMENT ON TABLE videos IS 'Learning videos that users can complete for experience points';
COMMENT ON COLUMN videos.exp_reward IS 'Experience points awarded upon completion (default: 200)';
COMMENT ON COLUMN videos.duration_seconds IS 'Video duration in seconds';
COMMENT ON COLUMN videos.order_index IS 'Display order within chapter/course';
