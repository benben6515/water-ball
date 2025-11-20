-- Update all video URLs to use local test video
-- This migration updates all existing video URLs to point to a local test video file

UPDATE videos
SET video_url = '/videos/coffee1.mov'
WHERE video_url LIKE 'https://example.com/%';
