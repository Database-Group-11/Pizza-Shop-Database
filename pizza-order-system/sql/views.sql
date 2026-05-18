-- ============================================
-- 视图文件：views.sql
-- 用途：定义常用业务视图，简化查询和报表
-- ============================================

-- ============================================
-- 1. 订单详情视图（包含用户、订单、支付信息）
-- ============================================
CREATE OR REPLACE VIEW order_detail_view AS
SELECT
    o.order_id,
    o.order_no,
    c.customer_id,
    c.name AS customer_name,
    c.phone,
    o.order_time,
    o.total_amount,
    o.status AS order_status,
    p.payment_method,
    p.status AS payment_status,
    p.payment_time,
    o.delivery_address,
    d.status AS delivery_status,
    d.start_time,
    d.arrive_time
FROM orders o
         JOIN customers c ON o.customer_id = c.customer_id
         LEFT JOIN payments p ON o.order_id = p.order_id
         LEFT JOIN deliveries d ON o.order_id = d.order_id;

-- ============================================
-- 2. 订单商品明细视图（用于订单详情展示）
-- ============================================
CREATE OR REPLACE VIEW order_item_detail_view AS
SELECT
    oi.item_id AS order_item_id,
    oi.order_id,
    o.order_no,
    p.pizza_id,
    p.name AS pizza_name,
    p.base_price AS pizza_base_price,
    oi.quantity,
    oi.subtotal / oi.quantity AS unit_price,
    oi.subtotal,
    t.topping_id,
    t.name AS topping_name,
    t.price AS topping_price,
    ot.quantity AS topping_quantity
FROM order_items oi
         JOIN orders o ON oi.order_id = o.order_id
         JOIN pizzas p ON oi.pizza_id = p.pizza_id
         LEFT JOIN order_toppings ot ON oi.item_id = ot.item_id
         LEFT JOIN toppings t ON ot.topping_id = t.topping_id;

-- ============================================
-- 3. 库存告警视图（低于安全库存的披萨）
-- ============================================
CREATE OR REPLACE VIEW low_inventory_view AS
SELECT
    pizza_id,
    name,
    stock_quantity,
    reorder_level,
    (reorder_level - stock_quantity) AS shortage_amount,
    last_restock_time
FROM pizzas
WHERE stock_quantity <= reorder_level;

-- ============================================
-- 4. 销售报表视图（按天统计）
-- ============================================
CREATE OR REPLACE VIEW daily_sales_report_view AS
SELECT
    DATE(o.order_time) AS sale_date,
    COUNT(DISTINCT o.order_id) AS total_orders,
    SUM(o.total_amount) AS total_revenue,
    ROUND(AVG(o.total_amount), 2) AS avg_order_value,
    COUNT(DISTINCT o.customer_id) AS unique_customers
FROM orders o
WHERE o.status = 'completed'
GROUP BY DATE(o.order_time);

-- ============================================
-- 5. 披萨销售排行视图（用于报表页面）
-- ============================================
CREATE OR REPLACE VIEW pizza_sales_ranking_view AS
SELECT
    p.pizza_id,
    p.name AS pizza_name,
    SUM(oi.quantity) AS total_quantity_sold,
    SUM(oi.subtotal) AS total_revenue,
    COUNT(DISTINCT oi.order_id) AS order_count
FROM order_items oi
         JOIN pizzas p ON oi.pizza_id = p.pizza_id
         JOIN orders o ON oi.order_id = o.order_id
WHERE o.status = 'completed'
GROUP BY p.pizza_id, p.name
ORDER BY total_quantity_sold DESC;

-- ============================================
-- 6. 用户消费汇总视图（便于查看客户价值）
-- ============================================
CREATE OR REPLACE VIEW customer_summary_view AS
SELECT
    c.customer_id,
    c.name,
    c.phone,
    COUNT(o.order_id) AS total_orders,
    COALESCE(SUM(o.total_amount), 0) AS total_spent,
    ROUND(COALESCE(AVG(o.total_amount), 0), 2) AS avg_order_value,
    MAX(o.order_time) AS last_order_time
FROM customers c
         LEFT JOIN orders o ON c.customer_id = o.customer_id AND o.status = 'completed'
GROUP BY c.customer_id, c.name, c.phone;

-- ============================================
-- 7. 月度销售汇总视图
-- ============================================
CREATE OR REPLACE VIEW monthly_sales_summary_view AS
SELECT
    YEAR(o.order_time) AS year,
    MONTH(o.order_time) AS month,
    DATE_FORMAT(o.order_time, '%Y-%m') AS month_str,
    COUNT(DISTINCT o.order_id) AS total_orders,
    SUM(o.total_amount) AS total_revenue,
    ROUND(AVG(o.total_amount), 2) AS avg_order_value
FROM orders o
WHERE o.status = 'completed'
GROUP BY YEAR(o.order_time), MONTH(o.order_time), DATE_FORMAT(o.order_time, '%Y-%m')
ORDER BY year DESC, month DESC;