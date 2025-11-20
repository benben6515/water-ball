-- Add is_demo column to videos table to support preview/demo videos
-- Demo videos can be watched by anyone without owning the course

ALTER TABLE videos ADD COLUMN is_demo BOOLEAN NOT NULL DEFAULT FALSE;

-- Add comment
COMMENT ON COLUMN videos.is_demo IS 'Whether this video is a demo/preview that can be watched by anyone without course ownership';
