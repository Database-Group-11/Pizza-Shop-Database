package main.java.edu.group11.dao;

import main.java.edu.group11.model.Order;
import main.java.edu.group11.model.OrderItem;
import main.java.edu.group11.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    private OrderItemDAO orderItemDAO = new OrderItemDAO();

    // Create order (including order item)
    public int createOrder(Order order, List<OrderItem> items) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int orderId = -1;

        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            String orderSql = "INSERT INTO orders (order_no, customer_id, total_amount, status, delivery_address, payment_method) VALUES (?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, order.getOrderNo());
            ps.setInt(2, order.getCustomerId());
            ps.setDouble(3, order.getTotalAmount());
            ps.setString(4, "pending");
            ps.setString(5, order.getDeliveryAddress());
            ps.setString(6, order.getPaymentMethod());

            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                orderId = rs.getInt(1);
            } else {
                throw new SQLException("Failed to create order");
            }

            for (OrderItem item : items) {
                item.setOrderId(orderId);
                orderItemDAO.createOrderItem(conn, item);
            }

            conn.commit();
            return orderId;

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            e.printStackTrace();
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    // Find order list by customer ID
    public List<Order> findByCustomerId(int customerId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE customer_id = ? ORDER BY order_time DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Order order = extractOrderFromResultSet(rs);
                order.setItems(orderItemDAO.findByOrderId(order.getOrderId()));
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    // Find order details by order ID
    public Order findById(int orderId) {
        String sql = "SELECT * FROM orders WHERE order_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Order order = extractOrderFromResultSet(rs);
                order.setItems(orderItemDAO.findByOrderId(order.getOrderId()));
                return order;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //  Update order status
    public boolean updateStatus(int orderId, String status) {
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Order extractOrderFromResultSet(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setOrderId(rs.getInt("order_id"));
        order.setOrderNo(rs.getString("order_no"));
        order.setCustomerId(rs.getInt("customer_id"));
        order.setOrderTime(rs.getTimestamp("order_time"));
        order.setTotalAmount(rs.getDouble("total_amount"));
        order.setStatus(rs.getString("status"));
        order.setDeliveryAddress(rs.getString("delivery_address"));
        order.setPaymentMethod(rs.getString("payment_method"));
        order.setCreateTime(rs.getTimestamp("create_time"));
        order.setUpdateTime(rs.getTimestamp("update_time"));
        return order;
    }
}
