package main.java.edu.group11.dao;

import main.java.edu.group11.model.Pizza;
import main.java.edu.group11.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PizzaDAO {
    // 获取所有披萨
    public List<Pizza> getAllPizzas() {
        List<Pizza> pizzas = new ArrayList<>();
        String sql = "SELECT pizza_id, name, description, base_price, category, image, available FROM pizzas WHERE available = 1";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Pizza pizza = new Pizza();
                pizza.setPizzaId(rs.getInt("pizza_id"));
                pizza.setName(rs.getString("name"));
                pizza.setDescription(rs.getString("description"));
                pizza.setBasePrice(rs.getDouble("base_price"));
                pizza.setCategory(rs.getString("category"));
                pizza.setImage(rs.getString("image"));
                pizza.setAvailable(rs.getBoolean("available"));
                pizzas.add(pizza);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pizzas;
    }

    // 根据ID获取披萨
    public Pizza getPizzaById(int pizzaId) {
        String sql = "SELECT pizza_id, name, description, base_price, category, image, available FROM pizzas WHERE pizza_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, pizzaId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Pizza pizza = new Pizza();
                    pizza.setPizzaId(rs.getInt("pizza_id"));
                    pizza.setName(rs.getString("name"));
                    pizza.setDescription(rs.getString("description"));
                    pizza.setBasePrice(rs.getDouble("base_price"));
                    pizza.setCategory(rs.getString("category"));
                    pizza.setImage(rs.getString("image"));
                    pizza.setAvailable(rs.getBoolean("available"));
                    return pizza;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 根据分类获取披萨
    public List<Pizza> getPizzasByCategory(String category) {
        List<Pizza> pizzas = new ArrayList<>();
        String sql = "SELECT pizza_id, name, description, base_price, category, image, available FROM pizzas WHERE category = ? AND available = 1";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, category);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Pizza pizza = new Pizza();
                    pizza.setPizzaId(rs.getInt("pizza_id"));
                    pizza.setName(rs.getString("name"));
                    pizza.setDescription(rs.getString("description"));
                    pizza.setBasePrice(rs.getDouble("base_price"));
                    pizza.setCategory(rs.getString("category"));
                    pizza.setImage(rs.getString("image"));
                    pizza.setAvailable(rs.getBoolean("available"));
                    pizzas.add(pizza);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pizzas;
    }

    // 管理员添加披萨
    public boolean addPizza(Pizza pizza) {
        String sql = "INSERT INTO pizzas (name, description, base_price, category, image, available) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, pizza.getName());
            pstmt.setString(2, pizza.getDescription());
            pstmt.setDouble(3, pizza.getBasePrice());
            pstmt.setString(4, pizza.getCategory());
            pstmt.setString(5, pizza.getImage());
            pstmt.setBoolean(6, pizza.isAvailable());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 管理员更新披萨
    public boolean updatePizza(Pizza pizza) {
        String sql = "UPDATE pizzas SET name = ?, description = ?, base_price = ?, category = ?, image = ?, available = ? WHERE pizza_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, pizza.getName());
            pstmt.setString(2, pizza.getDescription());
            pstmt.setDouble(3, pizza.getBasePrice());
            pstmt.setString(4, pizza.getCategory());
            pstmt.setString(5, pizza.getImage());
            pstmt.setBoolean(6, pizza.isAvailable());
            pstmt.setInt(7, pizza.getPizzaId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 管理员删除披萨
    public boolean deletePizza(int pizzaId) {
        String sql = "DELETE FROM pizzas WHERE pizza_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, pizzaId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 获取所有可用披萨（专门为 /api/pizzas/available 接口）
    public List<Pizza> getAvailable() {
        List<Pizza> availablePizzas = new ArrayList<>();
        String sql = "SELECT pizza_id, name, description, base_price, category, image, available " +
                "FROM pizzas WHERE available = 1 ORDER BY category, name";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Pizza pizza = new Pizza();
                pizza.setPizzaId(rs.getInt("pizza_id"));
                pizza.setName(rs.getString("name"));
                pizza.setDescription(rs.getString("description"));
                pizza.setBasePrice(rs.getDouble("base_price"));
                pizza.setCategory(rs.getString("category"));
                pizza.setImage(rs.getString("image"));
                pizza.setAvailable(rs.getBoolean("available"));
                availablePizzas.add(pizza);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return availablePizzas;
    }
}