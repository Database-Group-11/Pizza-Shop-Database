package main.java.edu.group11.dao;

import main.java.edu.group11.util.DBUtil;
import org.json.JSONObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToppingDAO {

    // 获取所有配料
    public List<Map<String, Object>> getAllToppings() {
        List<Map<String, Object>> toppings = new ArrayList<>();
        String sql = "SELECT topping_id, name, price, stock_quantity, category, image, available FROM toppings";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                toppings.add(extractToppingFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toppings;
    }

    // 获取可用配料
    public List<Map<String, Object>> getAvailableToppings() {
        List<Map<String, Object>> toppings = new ArrayList<>();
        String sql = "SELECT topping_id, name, price, stock_quantity, category, image, available " +
                "FROM toppings WHERE available = 1 AND stock_quantity > 0 ORDER BY category, name";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                toppings.add(extractToppingFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toppings;
    }

    // 根据分类获取配料
    public List<Map<String, Object>> getToppingsByCategory(String category) {
        List<Map<String, Object>> toppings = new ArrayList<>();
        String sql = "SELECT topping_id, name, price, stock_quantity, category, image, available " +
                "FROM toppings WHERE category = ? AND available = 1 ORDER BY name";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, category);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    toppings.add(extractToppingFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toppings;
    }

    // 根据ID获取配料
    public Map<String, Object> getToppingById(int toppingId) {
        String sql = "SELECT topping_id, name, price, stock_quantity, category, image, available " +
                "FROM toppings WHERE topping_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, toppingId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractToppingFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 获取热门配料（使用频率最高的）
    public List<Map<String, Object>> getPopularToppings(int limit) {
        List<Map<String, Object>> toppings = new ArrayList<>();
        String sql = "SELECT t.topping_id, t.name, t.price, t.stock_quantity, t.category, t.image, " +
                "COUNT(pt.pizza_id) as usage_count " +
                "FROM toppings t " +
                "LEFT JOIN pizza_toppings pt ON t.topping_id = pt.topping_id " +
                "WHERE t.available = 1 " +
                "GROUP BY t.topping_id " +
                "ORDER BY usage_count DESC " +
                "LIMIT ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> topping = extractToppingFromResultSet(rs);
                    topping.put("usageCount", rs.getInt("usage_count"));
                    toppings.add(topping);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toppings;
    }

    // 获取低库存配料（库存低于10）
    public List<Map<String, Object>> getLowStockToppings() {
        List<Map<String, Object>> toppings = new ArrayList<>();
        String sql = "SELECT topping_id, name, price, stock_quantity, category, image, available " +
                "FROM toppings WHERE stock_quantity < 10 AND available = 1 " +
                "ORDER BY stock_quantity ASC";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                toppings.add(extractToppingFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toppings;
    }

    // 获取指定披萨的配料
    public List<Map<String, Object>> getToppingsByPizzaId(int pizzaId) {
        List<Map<String, Object>> toppings = new ArrayList<>();
        String sql = "SELECT t.topping_id, t.name, t.price, t.stock_quantity, t.category, t.image, " +
                "pt.quantity, pt.pizza_topping_id " +
                "FROM toppings t " +
                "JOIN pizza_toppings pt ON t.topping_id = pt.topping_id " +
                "WHERE pt.pizza_id = ? AND t.available = 1";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, pizzaId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> topping = extractToppingFromResultSet(rs);
                    topping.put("quantity", rs.getInt("quantity"));
                    topping.put("pizzaToppingId", rs.getInt("pizza_topping_id"));
                    toppings.add(topping);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toppings;
    }

    // 创建配料
    public int createTopping(JSONObject jsonData) {
        String sql = "INSERT INTO toppings (name, price, stock_quantity, category, image, available) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, jsonData.getString("name"));
            pstmt.setDouble(2, jsonData.optDouble("price", 0.0));
            pstmt.setInt(3, jsonData.optInt("stockQuantity", 0));
            pstmt.setString(4, jsonData.optString("category", "other"));
            pstmt.setString(5, jsonData.optString("image", null));
            pstmt.setBoolean(6, jsonData.optBoolean("available", true));

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

    // 更新配料
    public boolean updateTopping(int toppingId, JSONObject jsonData) {
        StringBuilder sql = new StringBuilder("UPDATE toppings SET ");
        List<Object> params = new ArrayList<>();

        if (jsonData.has("name")) {
            sql.append("name = ?, ");
            params.add(jsonData.getString("name"));
        }
        if (jsonData.has("price")) {
            sql.append("price = ?, ");
            params.add(jsonData.getDouble("price"));
        }
        if (jsonData.has("stockQuantity")) {
            sql.append("stock_quantity = ?, ");
            params.add(jsonData.getInt("stockQuantity"));
        }
        if (jsonData.has("category")) {
            sql.append("category = ?, ");
            params.add(jsonData.getString("category"));
        }
        if (jsonData.has("image")) {
            sql.append("image = ?, ");
            params.add(jsonData.getString("image"));
        }
        if (jsonData.has("available")) {
            sql.append("available = ?, ");
            params.add(jsonData.getBoolean("available"));
        }

        if (params.isEmpty()) {
            return false;
        }

        sql.append("updated_at = NOW() WHERE topping_id = ?");
        params.add(toppingId);

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 删除配料
    public boolean deleteTopping(int toppingId) {
        // 先检查是否有关联的披萨
        String checkSql = "SELECT COUNT(*) FROM pizza_toppings WHERE topping_id = ?";
        String deleteSql = "DELETE FROM toppings WHERE topping_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setInt(1, toppingId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    // 有关联的披萨，不能删除
                    return false;
                }
            }

            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, toppingId);
                return deleteStmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 更新配料库存（减少库存）
    public boolean updateStock(int toppingId, int quantity) {
        String sql = "UPDATE toppings SET stock_quantity = stock_quantity - ?, " +
                "updated_at = NOW() WHERE topping_id = ? AND stock_quantity >= ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, quantity);
            pstmt.setInt(2, toppingId);
            pstmt.setInt(3, quantity);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 设置配料库存（直接设置）
    public boolean setStock(int toppingId, int stock) {
        String sql = "UPDATE toppings SET stock_quantity = ?, updated_at = NOW() WHERE topping_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, stock);
            pstmt.setInt(2, toppingId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 为披萨分配配料
    public boolean assignToppingToPizza(int pizzaId, int toppingId, int quantity) {
        String sql = "INSERT INTO pizza_toppings (pizza_id, topping_id, quantity) VALUES (?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, pizzaId);
            pstmt.setInt(2, toppingId);
            pstmt.setInt(3, quantity);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 移除披萨的配料
    public boolean removePizzaTopping(int pizzaToppingId) {
        String sql = "DELETE FROM pizza_toppings WHERE pizza_topping_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, pizzaToppingId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 检查配料库存是否充足
    public boolean checkStock(int toppingId, int requiredQuantity) {
        String sql = "SELECT stock_quantity FROM toppings WHERE topping_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, toppingId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("stock_quantity") >= requiredQuantity;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 从ResultSet提取配料数据为Map
    private Map<String, Object> extractToppingFromResultSet(ResultSet rs) throws SQLException {
        Map<String, Object> topping = new HashMap<>();
        topping.put("toppingId", rs.getInt("topping_id"));
        topping.put("name", rs.getString("name"));
        topping.put("price", rs.getDouble("price"));
        topping.put("stockQuantity", rs.getInt("stock_quantity"));

        try {
            topping.put("category", rs.getString("category"));
        } catch (SQLException e) {
            topping.put("category", "other");
        }

        try {
            topping.put("image", rs.getString("image"));
        } catch (SQLException e) {
            topping.put("image", null);
        }

        try {
            topping.put("available", rs.getBoolean("available"));
        } catch (SQLException e) {
            topping.put("available", true);
        }

        return topping;
    }
}