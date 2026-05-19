-- 创建数据库（如需要）
-- CREATE DATABASE IF NOT EXISTS pizza_shop DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- USE pizza_shop;

-- ============================================
-- 1. 顾客表 (Customer)
-- ============================================
CREATE TABLE customers (
                           customer_id INT PRIMARY KEY AUTO_INCREMENT,
                           name VARCHAR(100) NOT NULL,
                           phone VARCHAR(20) UNIQUE,
                           password VARCHAR(255) NOT NULL,
                           address VARCHAR(255),
                           create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ============================================
-- 2. 披萨表 (Pizza)
-- ============================================
CREATE TABLE pizzas (
                        pizza_id INT PRIMARY KEY AUTO_INCREMENT,
                        name VARCHAR(100) NOT NULL,
                        description TEXT,
                        base_price DECIMAL(10, 2) NOT NULL,
                        category VARCHAR(50),
                        image VARCHAR(255),
                        available BOOLEAN DEFAULT TRUE,
                        stock_quantity INT DEFAULT 0,
                        reorder_level INT DEFAULT 10,
                        last_restock_time TIMESTAMP NULL,
                        create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ============================================
-- 3. 配料表 (Topping)
-- ============================================
CREATE TABLE toppings (
                          topping_id INT PRIMARY KEY AUTO_INCREMENT,
                          name VARCHAR(100) NOT NULL,
                          price DECIMAL(10, 2) NOT NULL,
                          stock_quantity INT DEFAULT 0,
                          available BOOLEAN DEFAULT TRUE,
                          create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ============================================
-- 4. 订单表 (Order)
-- ============================================
CREATE TABLE orders (
                        order_id INT PRIMARY KEY AUTO_INCREMENT,
                        order_no VARCHAR(50) UNIQUE NOT NULL,
                        order_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                        total_amount DECIMAL(10, 2) NOT NULL,
                        status VARCHAR(20) DEFAULT 'pending',
                        delivery_address VARCHAR(255),
                        payment_method VARCHAR(50),
                        customer_id INT NOT NULL,
                        create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

-- ============================================
-- 5. 订单项表 (OrderItem)
-- ============================================
CREATE TABLE order_items (
                             item_id INT PRIMARY KEY AUTO_INCREMENT,
                             order_id INT NOT NULL,
                             pizza_id INT NOT NULL,
                             quantity INT NOT NULL,
                             unit_price DECIMAL(10, 2) NOT NULL,
                             subtotal DECIMAL(10, 2) NOT NULL,
                             FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
                             FOREIGN KEY (pizza_id) REFERENCES pizzas(pizza_id)
);

-- ============================================
-- 6. 订单配料关联表 (OrderTopping)
-- ============================================
CREATE TABLE order_toppings (
                                item_id INT NOT NULL,
                                topping_id INT NOT NULL,
                                quantity INT DEFAULT 1,
                                PRIMARY KEY (item_id, topping_id),
                                FOREIGN KEY (item_id) REFERENCES order_items(item_id) ON DELETE CASCADE,
                                FOREIGN KEY (topping_id) REFERENCES toppings(topping_id)
);

-- ============================================
-- 7. 支付表 (Payment)
-- ============================================
CREATE TABLE payments (
                          payment_id INT PRIMARY KEY AUTO_INCREMENT,
                          order_id INT NOT NULL UNIQUE,
                          payment_method VARCHAR(50) NOT NULL,
                          amount DECIMAL(10, 2) NOT NULL,
                          payment_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                          status VARCHAR(20) DEFAULT 'pending',
                          transaction_id VARCHAR(100),
                          create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE
);

-- ============================================
-- 8. 配送表 (Delivery)
-- ============================================
CREATE TABLE deliveries (
                            delivery_id INT PRIMARY KEY AUTO_INCREMENT,
                            order_id INT NOT NULL UNIQUE,
                            rider_name VARCHAR(100),
                            rider_phone VARCHAR(20),
                            start_time DATETIME,
                            arrive_time DATETIME,
                            status VARCHAR(20) DEFAULT 'preparing',
                            estimated_delivery_time DATETIME,
                            create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE
);