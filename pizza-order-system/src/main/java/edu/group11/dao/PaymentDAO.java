package main.java.edu.group11.dao;

import main.java.edu.group11.model.Payment;
import main.java.edu.group11.util.DBUtil;

import java.sql.*;

public class PaymentDAO {

    /**
     * 创建支付记录
     */
    public int createPayment(Payment payment) {
        String sql = "INSERT INTO payments (order_id, payment_method, amount, payment_status, transaction_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, payment.getOrderId());
            ps.setString(2, payment.getPaymentMethod());
            ps.setDouble(3, payment.getAmount());
            ps.setString(4, "success");
            ps.setString(5, payment.getTransactionId());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 根据订单ID查询支付记录
     */
    public Payment findByOrderId(int orderId) {
        String sql = "SELECT * FROM payments WHERE order_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Payment payment = new Payment();
                payment.setPaymentId(rs.getInt("payment_id"));
                payment.setOrderId(rs.getInt("order_id"));
                payment.setPaymentMethod(rs.getString("payment_method"));
                payment.setAmount(rs.getDouble("amount"));
                payment.setPaymentStatus(rs.getString("payment_status"));
                payment.setTransactionId(rs.getString("transaction_id"));
                payment.setPaymentTime(rs.getTimestamp("payment_time"));
                payment.setCreateTime(rs.getTimestamp("create_time"));
                return payment;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 更新支付状态
     */
    public boolean updateStatus(int paymentId, String status) {
        String sql = "UPDATE payments SET payment_status = ? WHERE payment_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, paymentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
