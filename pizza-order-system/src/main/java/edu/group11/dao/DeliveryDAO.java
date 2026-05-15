package main.java.edu.group11.dao;

import main.java.edu.group11.util.DBUtil;
import org.json.JSONObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeliveryDAO {

    // 创建配送记录
    public int createDelivery(JSONObject jsonData) {
        String sql = "INSERT INTO deliveries (order_id, delivery_address, contact_phone, " +
                "delivery_status, estimated_delivery_time, driver_name, driver_phone, " +
                "delivery_fee, tracking_number, delivery_notes, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, jsonData.getInt("orderId"));
            pstmt.setString(2, jsonData.getString("deliveryAddress"));
            pstmt.setString(3, jsonData.getString("contactPhone"));
            pstmt.setString(4, jsonData.optString("deliveryStatus", "pending"));

            if (jsonData.has("estimatedDeliveryTime")) {
                pstmt.setTimestamp(5, Timestamp.valueOf(jsonData.getString("estimatedDeliveryTime")));
            } else {
                pstmt.setNull(5, Types.TIMESTAMP);
            }

            pstmt.setString(6, jsonData.optString("driverName", null));
            pstmt.setString(7, jsonData.optString("driverPhone", null));
            pstmt.setDouble(8, jsonData.optDouble("deliveryFee", 0.0));
            pstmt.setString(9, jsonData.optString("trackingNumber", null));
            pstmt.setString(10, jsonData.optString("deliveryNotes", null));

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

    // 根据订单ID获取配送信息
    public Map<String, Object> getDeliveryByOrderId(int orderId) {
        String sql = "SELECT delivery_id, order_id, delivery_address, contact_phone, " +
                "delivery_status, estimated_delivery_time, actual_delivery_time, " +
                "driver_name, driver_phone, delivery_fee, tracking_number, " +
                "delivery_notes, created_at, updated_at " +
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

    // 根据配送ID获取配送信息
    public Map<String, Object> getDeliveryById(int deliveryId) {
        String sql = "SELECT delivery_id, order_id, delivery_address, contact_phone, " +
                "delivery_status, estimated_delivery_time, actual_delivery_time, " +
                "driver_name, driver_phone, delivery_fee, tracking_number, " +
                "delivery_notes, created_at, updated_at " +
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

    // 获取所有进行中的配送
    public List<Map<String, Object>> getActiveDeliveries() {
        List<Map<String, Object>> deliveries = new ArrayList<>();
        String sql = "SELECT delivery_id, order_id, delivery_address, contact_phone, " +
                "delivery_status, estimated_delivery_time, actual_delivery_time, " +
                "driver_name, driver_phone, delivery_fee, tracking_number, " +
                "delivery_notes, created_at, updated_at " +
                "FROM deliveries WHERE delivery_status IN ('pending', 'preparing', 'out_for_delivery') " +
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

    // 获取指定骑手的配送任务
    public List<Map<String, Object>> getDeliveriesByDriver(String driverName) {
        List<Map<String, Object>> deliveries = new ArrayList<>();
        String sql = "SELECT delivery_id, order_id, delivery_address, contact_phone, " +
                "delivery_status, estimated_delivery_time, actual_delivery_time, " +
                "driver_name, driver_phone, delivery_fee, tracking_number, " +
                "delivery_notes, created_at, updated_at " +
                "FROM deliveries WHERE driver_name = ? " +
                "AND delivery_status IN ('preparing', 'out_for_delivery') " +
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

    // 更新配送状态
    public boolean updateDeliveryStatus(int deliveryId, String status) {
        String sql = "UPDATE deliveries SET delivery_status = ?, updated_at = NOW() WHERE delivery_id = ?";

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

    // 更新配送状态并记录实际送达时间
    public boolean completeDelivery(int deliveryId) {
        String sql = "UPDATE deliveries SET delivery_status = 'delivered', " +
                "actual_delivery_time = NOW(), updated_at = NOW() WHERE delivery_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, deliveryId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 取消配送
    public boolean cancelDelivery(int deliveryId) {
        String sql = "UPDATE deliveries SET delivery_status = 'cancelled', updated_at = NOW() WHERE delivery_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, deliveryId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 分配骑手
    public boolean assignDriver(int deliveryId, String driverName, String driverPhone) {
        String sql = "UPDATE deliveries SET driver_name = ?, driver_phone = ?, " +
                "delivery_status = 'preparing', updated_at = NOW() WHERE delivery_id = ?";

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

    // 标记为已出库（开始配送）
    public boolean startDelivery(int deliveryId) {
        String sql = "UPDATE deliveries SET delivery_status = 'out_for_delivery', updated_at = NOW() WHERE delivery_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, deliveryId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 更新配送追踪号
    public boolean updateTrackingNumber(int deliveryId, String trackingNumber) {
        String sql = "UPDATE deliveries SET tracking_number = ?, updated_at = NOW() WHERE delivery_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, trackingNumber);
            pstmt.setInt(2, deliveryId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 添加配送备注
    public boolean addDeliveryNotes(int deliveryId, String notes) {
        String sql = "UPDATE deliveries SET delivery_notes = CONCAT(IFNULL(delivery_notes, ''), ?, '\\n'), " +
                "updated_at = NOW() WHERE delivery_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String timestamp = new Timestamp(System.currentTimeMillis()).toString();
            String noteWithTime = "[" + timestamp + "] " + notes;
            pstmt.setString(1, noteWithTime);
            pstmt.setInt(2, deliveryId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 获取超时配送（超过预计送达时间还未送达）
    public List<Map<String, Object>> getOverdueDeliveries() {
        List<Map<String, Object>> deliveries = new ArrayList<>();
        String sql = "SELECT delivery_id, order_id, delivery_address, contact_phone, " +
                "delivery_status, estimated_delivery_time, actual_delivery_time, " +
                "driver_name, driver_phone, delivery_fee, tracking_number, " +
                "delivery_notes, created_at, updated_at " +
                "FROM deliveries WHERE delivery_status IN ('preparing', 'out_for_delivery') " +
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

    // 获取今日配送统计
    public Map<String, Object> getTodayDeliveryStatistics() {
        Map<String, Object> stats = new HashMap<>();
        String sql = "SELECT " +
                "COUNT(*) as total_deliveries, " +
                "SUM(CASE WHEN delivery_status = 'delivered' THEN 1 ELSE 0 END) as completed, " +
                "SUM(CASE WHEN delivery_status = 'cancelled' THEN 1 ELSE 0 END) as cancelled, " +
                "SUM(CASE WHEN delivery_status IN ('pending', 'preparing', 'out_for_delivery') THEN 1 ELSE 0 END) as active, " +
                "AVG(TIMESTAMPDIFF(MINUTE, created_at, actual_delivery_time)) as avg_delivery_time " +
                "FROM deliveries WHERE DATE(created_at) = CURDATE()";

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

    // 从ResultSet提取配送数据为Map
    private Map<String, Object> extractDeliveryFromResultSet(ResultSet rs) throws SQLException {
        Map<String, Object> delivery = new HashMap<>();
        delivery.put("deliveryId", rs.getInt("delivery_id"));
        delivery.put("orderId", rs.getInt("order_id"));
        delivery.put("deliveryAddress", rs.getString("delivery_address"));
        delivery.put("contactPhone", rs.getString("contact_phone"));
        delivery.put("deliveryStatus", rs.getString("delivery_status"));

        Timestamp estimatedTime = rs.getTimestamp("estimated_delivery_time");
        if (estimatedTime != null) {
            delivery.put("estimatedDeliveryTime", estimatedTime.toString());
        }

        Timestamp actualTime = rs.getTimestamp("actual_delivery_time");
        if (actualTime != null) {
            delivery.put("actualDeliveryTime", actualTime.toString());
        }

        delivery.put("driverName", rs.getString("driver_name"));
        delivery.put("driverPhone", rs.getString("driver_phone"));
        delivery.put("deliveryFee", rs.getDouble("delivery_fee"));
        delivery.put("trackingNumber", rs.getString("tracking_number"));
        delivery.put("deliveryNotes", rs.getString("delivery_notes"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            delivery.put("createdAt", createdAt.toString());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            delivery.put("updatedAt", updatedAt.toString());
        }

        return delivery;
    }
}