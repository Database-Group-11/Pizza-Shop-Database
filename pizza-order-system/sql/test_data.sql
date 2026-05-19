-- ============================================
-- 测试数据
-- ============================================

-- 关闭外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================
-- 1. 顾客数据
-- ============================================
INSERT INTO customers (name, phone, password, address) VALUES
                                                           ('Zhang San', '13800138000', '123456', 'Haidian District, Beijing'),
                                                           ('Li Si', '13900139000', '123456', 'Pudong District, Shanghai'),
                                                           ('Wang Wu', '13700137000', '123456', 'Tianhe District, Guangzhou');

-- ============================================
-- 2. 披萨数据
-- ============================================
INSERT INTO pizzas (name, description, base_price, category) VALUES
                                                                 ('Margherita', 'Classic tomato cheese pizza', 8.99, 'Classic'),
                                                                 ('Pepperoni', 'Italian spicy sausage pizza', 10.99, 'Classic'),
                                                                 ('Hawaiian', 'Ham and pineapple pizza', 11.99, 'Specialty'),
                                                                 ('Meat Lovers', 'Meat lovers pizza', 13.99, 'Specialty'),
                                                                 ('Veggie Supreme', 'Vegetarian supreme pizza', 12.99, 'Vegetarian');

-- ============================================
-- 3. 配料数据
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
-- 4. 订单数据
-- ============================================
INSERT INTO orders (order_no, order_time, total_amount, status, delivery_address, customer_id) VALUES
                                                                                                   ('ORD20260507001', '2026-05-07 10:30:00', 12.49, 'completed', 'Haidian District, Beijing', 1),
                                                                                                   ('ORD20260507002', '2026-05-07 11:45:00', 23.98, 'delivering', 'Pudong District, Shanghai', 2),
                                                                                                   ('ORD20260508001', '2026-05-08 09:15:00', 10.99, 'pending', 'Tianhe District, Guangzhou', 3),
                                                                                                   ('ORD20260508002', '2026-05-08 12:00:00', 15.99, 'paid', 'Haidian District, Beijing', 1),
                                                                                                   ('ORD20260508003', '2026-05-08 18:30:00', 13.99, 'preparing', 'Pudong District, Shanghai', 2);

-- ============================================
-- 5. 订单项数据
-- ============================================
INSERT INTO order_items (quantity, subtotal, order_id, pizza_id) VALUES
                                                                     (1, 10.49, 1, 1),   -- 订单1：1个Margherita + Extra Cheese
                                                                     (1, 13.99, 1, 4),   -- 订单1：1个Meat Lovers
                                                                     (2, 23.98, 2, 3),   -- 订单2：2个Hawaiian
                                                                     (1, 10.99, 3, 2),   -- 订单3：1个Pepperoni
                                                                     (1, 15.99, 4, 5),   -- 订单4：1个Veggie Supreme
                                                                     (1, 13.99, 5, 4);   -- 订单5：1个Meat Lovers

-- ============================================
-- 6. 订单配料关联数据
-- ============================================
INSERT INTO order_toppings (item_id, topping_id, quantity) VALUES
                                                               (1, 1, 1),   -- 订单项1加Extra Cheese
                                                               (2, 5, 1),   -- 订单项2加Bacon
                                                               (3, 6, 2),   -- 订单项3加2份Pineapple
                                                               (5, 3, 1),   -- 订单项5加Mushrooms
                                                               (5, 4, 1);   -- 订单项5加Onions

-- ============================================
-- 7. 支付数据
-- ============================================
INSERT INTO payments (payment_method, amount, payment_time, status, order_id) VALUES
                                                                                  ('Credit Card', 12.49, '2026-05-07 10:31:00', 'completed', 1),
                                                                                  ('Cash', 23.98, '2026-05-07 11:46:00', 'completed', 2),
                                                                                  ('Credit Card', 10.99, NULL, 'pending', 3),
                                                                                  ('Alipay', 15.99, '2026-05-08 12:01:00', 'completed', 4),
                                                                                  ('WeChat Pay', 13.99, NULL, 'pending', 5);

-- ============================================
-- 8. 配送数据
-- ============================================
INSERT INTO deliveries (rider_name, start_time, arrive_time, status, order_id) VALUES
                                                                                   ('Rider Wang', '2026-05-07 10:35:00', '2026-05-07 11:00:00', 'delivered', 1),
                                                                                   ('Rider Li', '2026-05-07 11:50:00', NULL, 'delivering', 2),
                                                                                   (NULL, NULL, NULL, 'pending', 3),
                                                                                   ('Rider Zhang', '2026-05-08 12:05:00', NULL, 'delivering', 4),
                                                                                   (NULL, NULL, NULL, 'preparing', 5);

-- 重新开启外键检查
SET FOREIGN_KEY_CHECKS = 1;