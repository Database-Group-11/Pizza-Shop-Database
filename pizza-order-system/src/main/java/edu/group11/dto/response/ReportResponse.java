package main.java.edu.group11.dto.response;

import java.util.List;
import java.util.Map;

public class ReportResponse {
    private double totalRevenue;           // 总营业额
    private int totalOrders;               // 总订单数
    private double avgOrderValue;          // 平均订单金额
    private int totalCustomers;            // 总顾客数
    private List<Map<String, Object>> topPizzas;      // 畅销披萨排行
    private List<Map<String, Object>> dailySales;     // 每日销售趋势
    private Map<String, Object> categorySales;        // 分类销售统计
    private Map<String, Object> memberComparison;     // 会员对比

    public ReportResponse() {}

    // Getters and Setters
    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }

    public double getAvgOrderValue() {
        return avgOrderValue;
    }

    public void setAvgOrderValue(double avgOrderValue) {
        this.avgOrderValue = avgOrderValue;
    }

    public int getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(int totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    public List<Map<String, Object>> getTopPizzas() {
        return topPizzas;
    }

    public void setTopPizzas(List<Map<String, Object>> topPizzas) {
        this.topPizzas = topPizzas;
    }

    public List<Map<String, Object>> getDailySales() {
        return dailySales;
    }

    public void setDailySales(List<Map<String, Object>> dailySales) {
        this.dailySales = dailySales;
    }

    public Map<String, Object> getCategorySales() {
        return categorySales;
    }

    public void setCategorySales(Map<String, Object> categorySales) {
        this.categorySales = categorySales;
    }

    public Map<String, Object> getMemberComparison() {
        return memberComparison;
    }

    public void setMemberComparison(Map<String, Object> memberComparison) {
        this.memberComparison = memberComparison;
    }
}
