-- Create courses table for top-level learning paths
CREATE TABLE IF NOT EXISTS courses (
    course_id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    cover_image_url VARCHAR(500),
    instructor_name VARCHAR(100),
    instructor_avatar_url VARCHAR(500),
    price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    is_published BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Add indexes
CREATE INDEX idx_courses_published ON courses(is_published);
CREATE INDEX idx_courses_created_at ON courses(created_at DESC);

-- Add comments
COMMENT ON TABLE courses IS 'Top-level learning paths with videos organized in dungeons';
COMMENT ON COLUMN courses.price IS 'Course price in TWD (0.00 for free courses)';
COMMENT ON COLUMN courses.is_published IS 'Only published courses are visible to users';
