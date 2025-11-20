-- Create order_items table for tracking individual courses in each order
-- Stores the course purchased and the price at the time of purchase

CREATE TABLE order_items (
    order_item_id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    CONSTRAINT fk_order_items_course FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE RESTRICT
);

-- Create index on order_id for faster lookups of items in an order
CREATE INDEX idx_order_items_order_id ON order_items(order_id);

-- Create index on course_id for analytics
CREATE INDEX idx_order_items_course_id ON order_items(course_id);

-- Add comments
COMMENT ON TABLE order_items IS 'Individual course items within each order';
COMMENT ON COLUMN order_items.price IS 'Price of the course at the time of purchase (may differ from current course price)';
