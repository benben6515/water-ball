-- Create orders table for tracking course purchases
-- Orders store the overall purchase information and payment status

CREATE TABLE orders (
    order_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    payment_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    payment_method VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT chk_payment_status CHECK (payment_status IN ('PENDING', 'PAID', 'CANCELLED', 'REFUNDED'))
);

-- Create index on user_id for faster lookups of user's order history
CREATE INDEX idx_orders_user_id ON orders(user_id);

-- Create index on payment_status for filtering
CREATE INDEX idx_orders_payment_status ON orders(payment_status);

-- Add comments
COMMENT ON TABLE orders IS 'Course purchase orders with payment tracking';
COMMENT ON COLUMN orders.payment_status IS 'Payment status: PENDING, PAID, CANCELLED, or REFUNDED';
COMMENT ON COLUMN orders.payment_method IS 'Payment method used: CREDIT_CARD, ATM, MOCK, etc.';
