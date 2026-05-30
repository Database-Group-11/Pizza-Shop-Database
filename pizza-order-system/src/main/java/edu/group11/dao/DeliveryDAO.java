package main.java.edu.group11.dao;

import main.java.edu.group11.util.DBUtil;
import org.json.JSONObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeliveryDAO {

    // Create delivery record
    public int createDelivery(JSONObject jsonData) {
        String sql = "INSERT INTO deliveries (order_id, status) VALUES (?, 'pending')";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, jsonData.getInt("orderId"));

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Get delivery by order ID
    public Map<String, Object> getDeliveryByOrderId(int orderId) {
        String sql = "SELECT delivery_id, order_id, rider_name, rider_phone, " +
                "status, estimated_delivery_time, start_time, arrive_time, " +
                "create_time, update_time " +
                "FROM deliveries WHERE order_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractDeliveryFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Get delivery by delivery ID
    public Map<String, Object> getDeliveryById(int deliveryId) {
        String sql = "SELECT delivery_id, order_id, rider_name, rider_phone, " +
                "status, estimated_delivery_time, start_time, arrive_time, " +
                "create_time, update_time " +
                "FROM deliveries WHERE delivery_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, deliveryId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractDeliveryFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Get all active deliveries
    public List<Map<String, Object>> getActiveDeliveries() {
        List<Map<String, Object>> deliveries = new ArrayList<>();
        String sql = "SELECT delivery_id, order_id, rider_name, rider_phone, " +
                "status, estimated_delivery_time, start_time, arrive_time, " +
                "create_time, update_time " +
                "FROM deliveries WHERE status IN ('pending', 'preparing', 'out_for_delivery') " +
                "ORDER BY estimated_delivery_time ASC";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                deliveries.add(extractDeliveryFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return deliveries;
    }

    // Get deliveries by driver name
    public List<Map<String, Object>> getDeliveriesByDriver(String driverName) {
        List<Map<String, Object>> deliveries = new ArrayList<>();
        String sql = "SELECT delivery_id, order_id, rider_name, rider_phone, " +
                "status, estimated_delivery_time, start_time, arrive_time, " +
                "create_time, update_time " +
                "FROM deliveries WHERE rider_name = ? " +
                "AND status IN ('preparing', 'out_for_delivery') " +
                "ORDER BY estimated_delivery_time ASC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, driverName);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    deliveries.add(extractDeliveryFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return deliveries;
    }

    // Update delivery status
    public boolean updateDeliveryStatus(int deliveryId, String status) {
        String sql = "UPDATE deliveries SET status = ?, update_time = NOW() WHERE delivery_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, deliveryId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Complete delivery
    public boolean completeDelivery(int deliveryId) {
        String sql = "UPDATE deliveries SET status = 'delivered', " +
                "arrive_time = NOW(), update_time = NOW() WHERE delivery_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, deliveryId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cancel delivery
    public boolean cancelDelivery(int deliveryId) {
        String sql = "UPDATE deliveries SET status = 'cancelled', update_time = NOW() WHERE delivery_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, deliveryId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Assign driver to delivery
    public boolean assignDriver(int deliveryId, String driverName, String driverPhone) {
        String sql = "UPDATE deliveries SET rider_name = ?, rider_phone = ?, " +
                "status = 'preparing', update_time = NOW() WHERE delivery_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, driverName);
            pstmt.setString(2, driverPhone);
            pstmt.setInt(3, deliveryId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Mark delivery as started
    public boolean startDelivery(int deliveryId) {
        String sql = "UPDATE deliveries SET status = 'delivering', start_time = NOW(), " +
                "update_time = NOW() WHERE delivery_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, deliveryId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get overdue deliveries
    public List<Map<String, Object>> getOverdueDeliveries() {
        List<Map<String, Object>> deliveries = new ArrayList<>();
        String sql = "SELECT delivery_id, order_id, rider_name, rider_phone, " +
                "status, estimated_delivery_time, start_time, arrive_time, " +
                "create_time, update_time " +
                "FROM deliveries WHERE status IN ('preparing', 'out_for_delivery') " +
                "AND estimated_delivery_time < NOW() " +
                "ORDER BY estimated_delivery_time ASC";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                deliveries.add(extractDeliveryFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return deliveries;
    }

    // Get today's delivery statistics
    public Map<String, Object> getTodayDeliveryStatistics() {
        Map<String, Object> stats = new HashMap<>();
        String sql = "SELECT " +
                "COUNT(*) as total_deliveries, " +
                "SUM(CASE WHEN status = 'delivered' THEN 1 ELSE 0 END) as completed, " +
                "SUM(CASE WHEN status = 'cancelled' THEN 1 ELSE 0 END) as cancelled, " +
                "SUM(CASE WHEN status IN ('pending', 'preparing', 'out_for_delivery') THEN 1 ELSE 0 END) as active, " +
                "AVG(TIMESTAMPDIFF(MINUTE, create_time, arrive_time)) as avg_delivery_time " +
                "FROM deliveries WHERE DATE(create_time) = CURDATE()";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                stats.put("totalDeliveries", rs.getInt("total_deliveries"));
                stats.put("completedDeliveries", rs.getInt("completed"));
                stats.put("cancelledDeliveries", rs.getInt("cancelled"));
                stats.put("activeDeliveries", rs.getInt("active"));
                double avgTime = rs.getDouble("avg_delivery_time");
                stats.put("averageDeliveryTime", avgTime);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    // Extract delivery data from ResultSet
    private Map<String, Object> extractDeliveryFromResultSet(ResultSet rs) throws SQLException {
        Map<String, Object> delivery = new HashMap<>();
        delivery.put("deliveryId", rs.getInt("delivery_id"));
        delivery.put("orderId", rs.getInt("order_id"));
        delivery.put("riderName", rs.getString("rider_name"));
        delivery.put("riderPhone", rs.getString("rider_phone"));
        delivery.put("status", rs.getString("status"));

        Timestamp estimatedTime = rs.getTimestamp("estimated_delivery_time");
        if (estimatedTime != null) {
            delivery.put("estimatedDeliveryTime", estimatedTime.toString());
        }

        Timestamp startTime = rs.getTimestamp("start_time");
        if (startTime != null) {
            delivery.put("startTime", startTime.toString());
        }

        Timestamp arriveTime = rs.getTimestamp("arrive_time");
        if (arriveTime != null) {
            delivery.put("arriveTime", arriveTime.toString());
        }

        Timestamp createdAt = rs.getTimestamp("create_time");
        if (createdAt != null) {
            delivery.put("createdAt", createdAt.toString());
        }

        Timestamp updatedAt = rs.getTimestamp("update_time");
        if (updatedAt != null) {
            delivery.put("updatedAt", updatedAt.toString());
        }

        return delivery;
    }
}
