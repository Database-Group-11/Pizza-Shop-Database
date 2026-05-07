-- 加速顾客查询
CREATE INDEX idx_customer_phone ON customers(phone);

-- 加速订单查询（按顾客查订单历史）
CREATE INDEX idx_order_customer ON orders(customer_id);
CREATE INDEX idx_order_date ON orders(order_date);

-- 加速订单项查询
CREATE INDEX idx_orderitem_order ON order_items(order_id);

-- 加速配送状态查询
CREATE INDEX idx_delivery_status ON deliveries(status);
