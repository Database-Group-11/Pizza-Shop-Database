-- Customers searching
CREATE INDEX idx_customer_phone ON customers(phone);

-- Orders searching
CREATE INDEX idx_order_customer ON orders(customer_id);
CREATE INDEX idx_order_time ON orders(order_time);

-- Order items searching
CREATE INDEX idx_orderitem_order ON order_items(order_id);

-- Deliveries searching
CREATE INDEX idx_delivery_status ON deliveries(status);
