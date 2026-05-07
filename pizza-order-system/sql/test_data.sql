-- ============================================
-- 顾客数据
-- ============================================
INSERT INTO customers (name, phone, password, address) VALUES
                                                           ('张三', '13800138000', '123456', '北京市海淀区'),
                                                           ('李四', '13900139000', '123456', '上海市浦东新区'),
                                                           ('王五', '13700137000', '123456', '广州市天河区');

-- ============================================
-- 披萨数据
-- ============================================
INSERT INTO pizzas (name, description, base_price, category) VALUES
                                                                 ('Margherita', '经典番茄芝士披萨', 8.99, 'Classic'),
                                                                 ('Pepperoni', '意式辣香肠披萨', 10.99, 'Classic'),
                                                                 ('Hawaiian', '夏威夷火腿菠萝披萨', 11.99, 'Specialty'),
                                                                 ('Meat Lovers', '肉食爱好者披萨', 13.99, 'Specialty'),
                                                                 ('Veggie Supreme', '素食至尊披萨', 12.99, 'Vegetarian');

-- ============================================
-- 配料数据
-- ============================================
INSERT INTO toppings (name, price, stock_quantity) VALUES
                                                       ('Extra Cheese', 1.50, 100),
                                                       ('Pepperoni', 2.00, 80),
                                                       ('Mushrooms', 1.00, 50),
                                                       ('Onions', 0.50, 60),
                                                       ('Bacon', 2.50, 40),
                                                       ('Pineapple', 1.00, 30),
                                                       ('Olives', 1.00, 45);

-- ============================================
-- 订单数据（示例）
-- ============================================
INSERT INTO orders (total_price, status, delivery_address, customer_id) VALUES
                                                                            (12.49, 'completed', '北京市海淀区', 1),
                                                                            (15.99, 'preparing', '上海市浦东新区', 2);

-- ============================================
-- 订单项数据
-- ============================================
INSERT INTO order_items (quantity, subtotal, order_id, pizza_id) VALUES
                                                                     (1, 10.49, 1, 1),  -- 订单1：1个Margherita + Extra Cheese
                                                                     (1, 13.99, 1, 4),  -- 订单1：1个Meat Lovers
                                                                     (2, 23.98, 2, 3);  -- 订单2：2个Hawaiian

-- ============================================
-- 订单配料关联数据
-- ============================================
INSERT INTO order_toppings (item_id, topping_id, quantity) VALUES
                                                               (1, 1, 1),  -- 订单项1加Extra Cheese
                                                               (2, 5, 1),  -- 订单项2加Bacon
                                                               (3, 6, 2);  -- 订单项3加2份Pineapple

-- ============================================
-- 支付数据
-- ============================================
INSERT INTO payments (payment_method, amount, status, order_id) VALUES
                                                                    ('Credit Card', 12.49, 'completed', 1),
                                                                    ('Cash', 23.98, 'pending', 2);

-- ============================================
-- 配送数据
-- ============================================
INSERT INTO deliveries (rider_name, start_time, status, order_id) VALUES
                                                                      ('骑手小王', '2026-05-07 10:00:00', 'delivered', 1),
                                                                      ('骑手小李', NULL, 'preparing', 2);
