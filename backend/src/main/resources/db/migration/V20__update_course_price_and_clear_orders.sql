-- Update course price to 3980 NT$ for testing purchasing flow
UPDATE courses
SET price = 3980.00
WHERE course_id = 1;

-- Clear all orders and order items for testing
DELETE FROM order_items;
DELETE FROM orders;

-- Reset the order sequence
ALTER SEQUENCE orders_order_id_seq RESTART WITH 1;
