-- ============================================
-- 视图文件：views.sql
-- 用途：定义常用业务视图，简化查询和报表
-- ============================================

-- 1. 订单详情视图（包含用户、订单、支付信息）
CREATE VIEW order_detail_view AS
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
    p.payment_status,
    p.payment_time,
    d.delivery_address,
    d.delivery_status,
    d.estimated_delivery_time
FROM orders o
         JOIN customers c ON o.customer_id = c.customer_id
         LEFT JOIN payments p ON o.order_id = p.order_id
         LEFT JOIN deliveries d ON o.order_id = d.order_id;

-- 2. 订单商品明细视图（用于购物车和订单详情展示）
CREATE VIEW order_item_detail_view AS
SELECT
    oi.order_item_id,
    oi.order_id,
    o.order_no,
    p.pizza_id,
    p.name AS pizza_name,
    p.price AS pizza_base_price,
    oi.quantity,
    oi.unit_price,
    (oi.quantity * oi.unit_price) AS subtotal,
    t.topping_id,
    t.name AS topping_name,
    t.price AS topping_price
FROM order_items oi
         JOIN orders o ON oi.order_id = o.order_id
         JOIN pizzas p ON oi.pizza_id = p.pizza_id
         LEFT JOIN order_item_toppings oit ON oi.order_item_id = oit.order_item_id
         LEFT JOIN toppings t ON oit.topping_id = t.topping_id;

-- 3. 库存告警视图（低于安全库存的披萨原料）
CREATE VIEW low_inventory_view AS
SELECT
    pizza_id,
    name,
    stock_quantity,
    reorder_level,
    (reorder_level - stock_quantity) AS shortage_amount,
    last_restock_time
FROM pizzas
WHERE stock_quantity <= reorder_level;

-- 4. 销售报表视图（按天统计）
CREATE VIEW daily_sales_report_view AS
SELECT
        DATE(o.order_time) AS sale_date,
        COUNT(DISTINCT o.order_id) AS total_orders,
        SUM(o.total_amount) AS total_revenue,
        AVG(o.total_amount) AS avg_order_value,
        COUNT(DISTINCT o.customer_id) AS unique_customers
        FROM orders o
        WHERE o.status = 'completed'
        GROUP BY DATE(o.order_time);

-- 5. 披萨销售排行视图（用于报表页面）
CREATE VIEW pizza_sales_ranking_view AS
SELECT
    p.pizza_id,
    p.name AS pizza_name,
    SUM(oi.quantity) AS total_quantity_sold,
    SUM(oi.quantity * oi.unit_price) AS total_revenue,
    COUNT(DISTINCT oi.order_id) AS order_count
FROM order_items oi
         JOIN pizzas p ON oi.pizza_id = p.pizza_id
         JOIN orders o ON oi.order_id = o.order_id
WHERE o.status = 'completed'
GROUP BY p.pizza_id, p.name
ORDER BY total_quantity_sold DESC;

-- 6. 用户消费汇总视图（便于查看客户价值）
CREATE VIEW customer_summary_view AS
SELECT
    c.customer_id,
    c.name,
    c.phone,
    COUNT(o.order_id) AS total_orders,
    SUM(o.total_amount) AS total_spent,
    AVG(o.total_amount) AS avg_order_value,
    MAX(o.order_time) AS last_order_time
FROM customers c
         LEFT JOIN orders o ON c.customer_id = o.customer_id AND o.status = 'completed'
GROUP BY c.customer_id, c.name, c.phone;