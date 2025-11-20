-- Create dungeons table (副本) for organizing videos within courses
CREATE TABLE IF NOT EXISTS dungeons (
    dungeon_id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL REFERENCES courses(course_id) ON DELETE CASCADE,
    dungeon_number INTEGER NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    difficulty INTEGER NOT NULL DEFAULT 1 CHECK (difficulty >= 1 AND difficulty <= 4),
    order_index INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Ensure unique dungeon numbers within a course
    CONSTRAINT uq_course_dungeon_number UNIQUE (course_id, dungeon_number)
);

-- Create user course ownership table for e-commerce
CREATE TABLE IF NOT EXISTS user_course_ownership (
    ownership_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    course_id BIGINT NOT NULL REFERENCES courses(course_id) ON DELETE CASCADE,
    purchased_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Ensure users can't purchase the same course twice
    CONSTRAINT uq_user_course_ownership UNIQUE (user_id, course_id)
);

-- Add foreign key from videos to dungeons
ALTER TABLE videos
    ADD COLUMN dungeon_id BIGINT REFERENCES dungeons(dungeon_id) ON DELETE SET NULL;

-- Add indexes for dungeons
CREATE INDEX idx_dungeons_course ON dungeons(course_id);
CREATE INDEX idx_dungeons_order ON dungeons(course_id, order_index);

-- Add indexes for ownership
CREATE INDEX idx_ownership_user ON user_course_ownership(user_id);
CREATE INDEX idx_ownership_course ON user_course_ownership(course_id);
CREATE INDEX idx_ownership_purchased_at ON user_course_ownership(purchased_at DESC);

-- Add indexes for videos-dungeons relationship
CREATE INDEX idx_videos_dungeon ON videos(dungeon_id);

-- Add comments
COMMENT ON TABLE dungeons IS 'Dungeons (副本) organize videos within courses with difficulty ratings';
COMMENT ON COLUMN dungeons.dungeon_number IS 'Dungeon number within course (0-7 for main story dungeons)';
COMMENT ON COLUMN dungeons.difficulty IS 'Difficulty rating: 1=★, 2=★★, 3=★★★, 4=★★★★';
COMMENT ON TABLE user_course_ownership IS 'Tracks which courses users have purchased/unlocked';
COMMENT ON CONSTRAINT uq_user_course_ownership ON user_course_ownership IS 'Prevents duplicate course purchases';
