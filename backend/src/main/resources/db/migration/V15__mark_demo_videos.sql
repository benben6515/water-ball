-- Mark the first video of each dungeon as a demo video
-- Demo videos can be watched by anyone for preview purposes

-- Update the first video (order_index = 0) of each dungeon to be a demo
UPDATE videos
SET is_demo = TRUE
WHERE order_index = 0;

-- Add comment
COMMENT ON TABLE videos IS 'Learning videos with demo/preview support - first video of each dungeon is marked as demo';
