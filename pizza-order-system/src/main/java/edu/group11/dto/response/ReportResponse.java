package main.java.edu.group11.dto.response;

import java.util.List;
import java.util.Map;

public class ReportResponse {
    private double totalRevenue;
    private int totalOrders;
    private double avgOrderValue;
    private int totalCustomers;
    private List<Map<String, Object>> topPizzas;
    private List<Map<String, Object>> dailySales; // Daily sales trend
    private Map<String, Object> categorySales; // Classified sales statistics
    private Map<String, Object> memberComparison;

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
