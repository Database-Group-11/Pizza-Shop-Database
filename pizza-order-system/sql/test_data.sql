-- Test data

SET FOREIGN_KEY_CHECKS = 0;

-- 1. Customers data
INSERT INTO customers (name, phone, password, address) VALUES
                                                           ('Zhang San', '13800138000', '123456', 'Haidian District, Beijing'),
                                                           ('Li Si', '13900139000', '123456', 'Pudong District, Shanghai'),
                                                           ('Wang Wu', '13700137000', '123456', 'Tianhe District, Guangzhou');

-- 2. Pizzas data
INSERT INTO pizzas (name, description, base_price, category, image, available, stock_quantity, reorder_level) VALUES
                                                                                                                  ('Margherita', 'Classic tomato cheese pizza', 8.99, 'Classic', NULL, 1, 100, 10),
                                                                                                                  ('Pepperoni', 'Italian spicy sausage pizza', 10.99, 'Classic', NULL, 1, 80, 10),
                                                                                                                  ('Hawaiian', 'Ham and pineapple pizza', 11.99, 'Specialty', NULL, 1, 60, 10),
                                                                                                                  ('Meat Lovers', 'Meat lovers pizza', 13.99, 'Specialty', NULL, 1, 50, 10),
                                                                                                                  ('Veggie Supreme', 'Vegetarian supreme pizza', 12.99, 'Vegetarian', NULL, 1, 70, 10),
                                                                                                                  ('BBQ Chicken', 'Grilled chicken with smoky BBQ sauce', 12.99, 'Meat', NULL, 1, 60, 10),
                                                                                                                  ('Seafood Deluxe', 'Shrimp, squid and mussels', 14.99, 'Seafood', NULL, 1, 40, 10);

-- 3. Toppings data
INSERT INTO toppings (name, price, stock_quantity, available) VALUES
                                                                  ('Extra Cheese', 1.50, 100, 1),
                                                                  ('Pepperoni', 2.00, 80, 1),
                                                                  ('Mushrooms', 1.00, 50, 1),
                                                                  ('Onions', 0.50, 60, 1),
                                                                  ('Bacon', 2.50, 40, 1),
                                                                  ('Pineapple', 1.00, 30, 1),
                                                                  ('Olives', 1.00, 45, 1);

-- 4. Orders data
INSERT INTO orders (order_no, order_time, total_amount, status, delivery_address, payment_method, customer_id) VALUES
                                                                                                                   ('ORD20260507001', '2026-05-07 10:30:00', 12.49, 'completed', 'Haidian District, Beijing', 'Credit Card', 1),
                                                                                                                   ('ORD20260507002', '2026-05-07 11:45:00', 23.98, 'delivering', 'Pudong District, Shanghai', 'Cash', 2),
                                                                                                                   ('ORD20260508001', '2026-05-08 09:15:00', 10.99, 'pending', 'Tianhe District, Guangzhou', 'Credit Card', 3),
                                                                                                                   ('ORD20260508002', '2026-05-08 12:00:00', 15.99, 'paid', 'Haidian District, Beijing', 'Alipay', 1),
                                                                                                                   ('ORD20260508003', '2026-05-08 18:30:00', 13.99, 'preparing', 'Pudong District, Shanghai', 'WeChat Pay', 2);

-- 5. Order items data
INSERT INTO order_items (order_id, pizza_id, quantity, unit_price, subtotal) VALUES
                                                                                 (1, 1, 1, 8.99, 8.99),
                                                                                 (1, 4, 1, 13.99, 13.99),
                                                                                 (2, 3, 2, 11.99, 23.98),
                                                                                 (3, 2, 1, 10.99, 10.99),
                                                                                 (4, 5, 1, 15.99, 15.99),
                                                                                 (5, 4, 1, 13.99, 13.99);

-- 6. Data of order toppings
INSERT INTO order_toppings (item_id, topping_id, quantity) VALUES
                                                               (1, 1, 1),
                                                               (2, 5, 1),
                                                               (3, 6, 2),
                                                               (5, 3, 1),
                                                               (5, 4, 1);

-- 7. Payments data
INSERT INTO payments (payment_method, amount, payment_time, status, order_id) VALUES
                                                                                  ('Credit Card', 12.49, '2026-05-07 10:31:00', 'completed', 1),
                                                                                  ('Cash', 23.98, '2026-05-07 11:46:00', 'completed', 2),
                                                                                  ('Credit Card', 10.99, NULL, 'pending', 3),
                                                                                  ('Alipay', 15.99, '2026-05-08 12:01:00', 'completed', 4),
                                                                                  ('WeChat Pay', 13.99, NULL, 'pending', 5);

-- 8. Deliveries data
INSERT INTO deliveries (rider_name, start_time, arrive_time, status, order_id) VALUES
                                                                                   ('Rider Wang', '2026-05-07 10:35:00', '2026-05-07 11:00:00', 'delivered', 1),
                                                                                   ('Rider Li', '2026-05-07 11:50:00', NULL, 'delivering', 2),
                                                                                   (NULL, NULL, NULL, 'pending', 3),
                                                                                   ('Rider Zhang', '2026-05-08 12:05:00', NULL, 'delivering', 4),
                                                                                   (NULL, NULL, NULL, 'preparing', 5);

SET FOREIGN_KEY_CHECKS = 1;