-- Clear all course ownership and progress data for testing
-- This allows users to test the purchasing flow from scratch

-- Clear video progress
DELETE FROM user_video_progress;

-- Clear video completions
DELETE FROM video_completions;

-- Clear course ownership
DELETE FROM user_course_ownership;

-- Reset sequences if needed
ALTER SEQUENCE user_course_ownership_ownership_id_seq RESTART WITH 1;
