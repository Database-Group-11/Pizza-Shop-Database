package main.java.edu.group11.dao;

import main.java.edu.group11.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportDAO {

    // 销售统计报表类
    public static class SalesReport {
        private String period;  // 时间段
        private double totalSales;  // 总销售额
        private int totalOrders;  // 总订单数
        private double averageOrderValue;  // 平均订单金额
        private int totalPizzasSold;  // 总披萨销量

        // Getters and Setters
        public String getPeriod() { return period; }
        public void setPeriod(String period) { this.period = period; }

        public double getTotalSales() { return totalSales; }
        public void setTotalSales(double totalSales) { this.totalSales = totalSales; }

        public int getTotalOrders() { return totalOrders; }
        public void setTotalOrders(int totalOrders) { this.totalOrders = totalOrders; }

        public double getAverageOrderValue() { return averageOrderValue; }
        public void setAverageOrderValue(double averageOrderValue) { this.averageOrderValue = averageOrderValue; }

        public int getTotalPizzasSold() { return totalPizzasSold; }
        public void setTotalPizzasSold(int totalPizzasSold) { this.totalPizzasSold = totalPizzasSold; }
    }

    // 披萨销售排行
    public static class PizzaSalesRanking {
        private int pizzaId;
        private String pizzaName;
        private int totalQuantity;
        private double totalRevenue;
        private double percentage;  // 销售占比

        // Getters and Setters
        public int getPizzaId() { return pizzaId;
        }
        public void setPizzaId(int pizzaId) { this.pizzaId = pizzaId; }

        public String getPizzaName() { return pizzaName; }
        public void setPizzaName(String pizzaName) { this.pizzaName = pizzaName; }

        public int getTotalQuantity() { return totalQuantity; }
        public void setTotalQuantity(int totalQuantity) { this.totalQuantity = totalQuantity; }

        public double getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }

        public double getPercentage() { return percentage; }
        public void setPercentage(double percentage) { this.percentage = percentage; }
    }

    // 获取今日销售报表
    public SalesReport getTodaySalesReport() {
        String sql = "SELECT " +
                "COUNT(DISTINCT o.order_id) as total_orders, " +
                "SUM(oi.quantity) as total_pizzas, " +
                "SUM(oi.subtotal) as total_sales " +
                "FROM orders o " +
                "JOIN order_items oi ON o.order_id = oi.order_id " +
                "WHERE DATE(o.created_at) = CURDATE() AND o.status != 'cancelled'";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                SalesReport report = new SalesReport();
                report.setPeriod("今日");
                report.setTotalOrders(rs.getInt("total_orders"));
                report.setTotalPizzasSold(rs.getInt("total_pizzas"));
                report.setTotalSales(rs.getDouble("total_sales"));
                if (report.getTotalOrders() > 0) {
                    report.setAverageOrderValue(report.getTotalSales() / report.getTotalOrders());
                } else {
                    report.setAverageOrderValue(0);
                }
                return report;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 获取本周销售报表
    public SalesReport getWeeklySalesReport() {
        String sql = "SELECT " +
                "COUNT(DISTINCT o.order_id) as total_orders, " +
                "SUM(oi.quantity) as total_pizzas, " +
                "SUM(oi.subtotal) as total_sales " +
                "FROM orders o " +
                "JOIN order_items oi ON o.order_id = oi.order_id " +
                "WHERE YEARWEEK(o.created_at) = YEARWEEK(CURDATE()) AND o.status != 'cancelled'";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                SalesReport report = new SalesReport();
                report.setPeriod("本周");
                report.setTotalOrders(rs.getInt("total_orders"));
                report.setTotalPizzasSold(rs.getInt("total_pizzas"));
                report.setTotalSales(rs.getDouble("total_sales"));
                if (report.getTotalOrders() > 0) {
                    report.setAverageOrderValue(report.getTotalSales() / report.getTotalOrders());
                } else {
                    report.setAverageOrderValue(0);
                }
                return report;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 获取本月销售报表
    public SalesReport getMonthlySalesReport() {
        String sql = "SELECT " +
                "COUNT(DISTINCT o.order_id) as total_orders, " +
                "SUM(oi.quantity) as total_pizzas, " +
                "SUM(oi.subtotal) as total_sales " +
                "FROM orders o " +
                "JOIN order_items oi ON o.order_id = oi.order_id " +
                "WHERE MONTH(o.created_at) = MONTH(CURDATE()) " +
                "AND YEAR(o.created_at) = YEAR(CURDATE()) " +
                "AND o.status != 'cancelled'";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                SalesReport report = new SalesReport();
                report.setPeriod("本月");
                report.setTotalOrders(rs.getInt("total_orders"));
                report.setTotalPizzasSold(rs.getInt("total_pizzas"));
                report.setTotalSales(rs.getDouble("total_sales"));
                if (report.getTotalOrders() > 0) {
                    report.setAverageOrderValue(report.getTotalSales() / report.getTotalOrders());
                } else {
                    report.setAverageOrderValue(0);
                }
                return report;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 获取指定日期范围的销售报表
    public SalesReport getSalesReportByDateRange(String startDate, String endDate) {
        String sql = "SELECT " +
                "COUNT(DISTINCT o.order_id) as total_orders, " +
                "SUM(oi.quantity) as total_pizzas, " +
                "SUM(oi.subtotal) as total_sales " +
                "FROM orders o " +
                "JOIN order_items oi ON o.order_id = oi.order_id " +
                "WHERE DATE(o.created_at) BETWEEN ? AND ? " +
                "AND o.status != 'cancelled'";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, startDate);
            pstmt.setString(2, endDate);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    SalesReport report = new SalesReport();
                    report.setPeriod(startDate + " 至 " + endDate);
                    report.setTotalOrders(rs.getInt("total_orders"));
                    report.setTotalPizzasSold(rs.getInt("total_pizzas"));
                    report.setTotalSales(rs.getDouble("total_sales"));
                    if (report.getTotalOrders() > 0) {
                        report.setAverageOrderValue(report.getTotalSales() / report.getTotalOrders());
                    } else {
                        report.setAverageOrderValue(0);
                    }
                    return report;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 获取热门披萨排行榜（按销量）
    public List<PizzaSalesRanking> getTopSellingPizzas(int limit) {
        List<PizzaSalesRanking> rankings = new ArrayList<>();
        String sql = "SELECT " +
                "p.pizza_id, " +
                "p.name as pizza_name, " +
                "SUM(oi.quantity) as total_quantity, " +
                "SUM(oi.subtotal) as total_revenue " +
                "FROM pizzas p " +
                "JOIN order_items oi ON p.pizza_id = oi.pizza_id " +
                "JOIN orders o ON oi.order_id = o.order_id " +
                "WHERE o.status != 'cancelled' " +
                "GROUP BY p.pizza_id, p.name " +
                "ORDER BY total_quantity DESC " +
                "LIMIT ?";

        // 先获取总销量用于计算百分比
        int totalAllSales = getTotalPizzasSold();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PizzaSalesRanking ranking = new PizzaSalesRanking();
                    ranking.setPizzaId(rs.getInt("pizza_id"));
                    ranking.setPizzaName(rs.getString("pizza_name"));
                    ranking.setTotalQuantity(rs.getInt("total_quantity"));
                    ranking.setTotalRevenue(rs.getDouble("total_revenue"));

                    if (totalAllSales > 0) {
                        double percentage = (ranking.getTotalQuantity() * 100.0) / totalAllSales;
                        ranking.setPercentage(Math.round(percentage * 100.0) / 100.0);
                    } else {
                        ranking.setPercentage(0);
                    }

                    rankings.add(ranking);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rankings;
    }

    // 获取所有披萨的总销量
    private int getTotalPizzasSold() {
        String sql = "SELECT SUM(oi.quantity) as total FROM order_items oi " +
                "JOIN orders o ON oi.order_id = o.order_id " +
                "WHERE o.status != 'cancelled'";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 获取分类销售统计
    public Map<String, Object> getCategorySalesStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        String sql = "SELECT " +
                "p.category, " +
                "COUNT(DISTINCT o.order_id) as order_count, " +
                "SUM(oi.quantity) as total_quantity, " +
                "SUM(oi.subtotal) as total_revenue " +
                "FROM pizzas p " +
                "JOIN order_items oi ON p.pizza_id = oi.pizza_id " +
                "JOIN orders o ON oi.order_id = o.order_id " +
                "WHERE o.status != 'cancelled' " +
                "GROUP BY p.category " +
                "ORDER BY total_revenue DESC";

        List<Map<String, Object>> categorySales = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            double totalRevenue = 0;
            while (rs.next()) {
                Map<String, Object> categoryData = new HashMap<>();
                categoryData.put("category", rs.getString("category"));
                categoryData.put("orderCount", rs.getInt("order_count"));
                categoryData.put("totalQuantity", rs.getInt("total_quantity"));
                categoryData.put("totalRevenue", rs.getDouble("total_revenue"));
                categorySales.add(categoryData);
                totalRevenue += rs.getDouble("total_revenue");
            }

            statistics.put("categorySales", categorySales);
            statistics.put("totalRevenue", totalRevenue);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return statistics;
    }

    // 获取每日销售趋势（最近7天）
    public List<Map<String, Object>> getDailySalesTrend() {
        List<Map<String, Object>> trend = new ArrayList<>();
        String sql = "SELECT " +
                "DATE(o.created_at) as sale_date, " +
                "COUNT(DISTINCT o.order_id) as order_count, " +
                "SUM(oi.quantity) as pizzas_sold, " +
                "SUM(oi.subtotal) as daily_revenue " +
                "FROM orders o " +
                "JOIN order_items oi ON o.order_id = oi.order_id " +
                "WHERE o.created_at >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
                "AND o.status != 'cancelled' " +
                "GROUP BY DATE(o.created_at) " +
                "ORDER BY sale_date DESC";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> dayData = new HashMap<>();
                dayData.put("date", rs.getDate("sale_date").toString());
                dayData.put("orderCount", rs.getInt("order_count"));
                dayData.put("pizzasSold", rs.getInt("pizzas_sold"));
                dayData.put("dailyRevenue", rs.getDouble("daily_revenue"));
                trend.add(dayData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trend;
    }

    // 获取小时销售分析（哪个时间段订单最多）
    public List<Map<String, Object>> getHourlySalesAnalysis() {
        List<Map<String, Object>> hourlyAnalysis = new ArrayList<>();
        String sql = "SELECT " +
                "HOUR(o.created_at) as hour, " +
                "COUNT(DISTINCT o.order_id) as order_count, " +
                "SUM(oi.quantity) as pizzas_sold " +
                "FROM orders o " +
                "JOIN order_items oi ON o.order_id = oi.order_id " +
                "WHERE o.status != 'cancelled' " +
                "GROUP BY HOUR(o.created_at) " +
                "ORDER BY hour";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> hourData = new HashMap<>();
                hourData.put("hour", rs.getInt("hour"));
                hourData.put("orderCount", rs.getInt("order_count"));
                hourData.put("pizzasSold", rs.getInt("pizzas_sold"));
                hourlyAnalysis.add(hourData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hourlyAnalysis;
    }

    // 获取会员/非会员销售对比
    public Map<String, Object> getMemberVsNonMemberSales() {
        Map<String, Object> comparison = new HashMap<>();
        String sql = "SELECT " +
                "CASE WHEN o.user_id IS NOT NULL THEN '会员' ELSE '非会员' END as customer_type, " +
                "COUNT(DISTINCT o.order_id) as order_count, " +
                "SUM(oi.subtotal) as total_revenue, " +
                "AVG(oi.subtotal) as average_order_value " +
                "FROM orders o " +
                "JOIN order_items oi ON o.order_id = oi.order_id " +
                "WHERE o.status != 'cancelled' " +
                "GROUP BY customer_type";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String type = rs.getString("customer_type");
                Map<String, Object> typeData = new HashMap<>();
                typeData.put("orderCount", rs.getInt("order_count"));
                typeData.put("totalRevenue", rs.getDouble("total_revenue"));
                typeData.put("averageOrderValue", rs.getDouble("average_order_value"));
                comparison.put(type, typeData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comparison;
    }
}
