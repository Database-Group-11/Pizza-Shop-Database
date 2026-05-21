package main.java.edu.group11.servlet;

import main.java.edu.group11.dao.ReportDAO;
import main.java.edu.group11.dao.ReportDAO.SalesReport;
import main.java.edu.group11.dao.ReportDAO.PizzaSalesRanking;
import org.json.JSONArray;
import org.json.JSONObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@WebServlet("/api/reports/*")
public class ReportServlet extends HttpServlet {

    private ReportDAO reportDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        reportDAO = new ReportDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                JSONObject result = new JSONObject();
                result.put("reportTypes", new JSONArray()
                        .put("sales/today")
                        .put("sales/weekly")
                        .put("sales/monthly")
                        .put("sales/range")
                        .put("top-pizzas")
                        .put("category-sales")
                        .put("daily-trend")
                        .put("hourly-analysis")
                        .put("member-comparison"));
                sendResponse(response, HttpServletResponse.SC_OK, result);
            } else {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length >= 2) {
                    String reportType = pathParts[1];

                    switch (reportType) {
                        case "sales":
                            handleSalesReport(request, response, pathParts);
                            break;
                        case "top-pizzas":
                            handleTopPizzasReport(request, response);
                            break;
                        case "category-sales":
                            handleCategorySalesReport(request, response);
                            break;
                        case "daily-trend":
                            handleDailyTrendReport(request, response);
                            break;
                        case "hourly-analysis":
                            handleHourlyAnalysisReport(request, response);
                            break;
                        case "member-comparison":
                            handleMemberComparisonReport(request, response);
                            break;
                        default:
                            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Unknown report type");
                            break;
                    }
                } else {
                    sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request path");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error: " + e.getMessage());
        }
    }

    // Handle with sales report
    private void handleSalesReport(HttpServletRequest request, HttpServletResponse response, String[] pathParts)
            throws IOException {
        if (pathParts.length < 3) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Please assign sales report type");
            return;
        }

        String period = pathParts[2];
        SalesReport report = null;

        switch (period) {
            case "today":
                report = reportDAO.getTodaySalesReport();
                break;
            case "weekly":
                report = reportDAO.getWeeklySalesReport();
                break;
            case "monthly":
                report = reportDAO.getMonthlySalesReport();
                break;
            case "range":
                String startDate = request.getParameter("startDate");
                String endDate = request.getParameter("endDate");
                if (startDate == null || endDate == null) {
                    sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Please provide starting and ending date");
                    return;
                }
                report = reportDAO.getSalesReportByDateRange(startDate, endDate);
                break;
            default:
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid sales report type");
                return;
        }

        if (report == null) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to get sales report data");
            return;
        }

        JSONObject result = new JSONObject();
        result.put("period", report.getPeriod());
        result.put("totalOrders", report.getTotalOrders());
        result.put("totalSales", report.getTotalSales());
        result.put("averageOrderValue", report.getAverageOrderValue());
        result.put("totalPizzasSold", report.getTotalPizzasSold());

        sendResponse(response, HttpServletResponse.SC_OK, result);
    }

    // Handle with top pizza report
    private void handleTopPizzasReport(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String limitParam = request.getParameter("limit");
        int limit = 10;

        if (limitParam != null && !limitParam.isEmpty()) {
            try {
                limit = Integer.parseInt(limitParam);
                if (limit > 50) limit = 50;
            } catch (NumberFormatException e) {
            }
        }

        List<PizzaSalesRanking> topPizzas = reportDAO.getTopSellingPizzas(limit);
        JSONArray result = new JSONArray();

        for (PizzaSalesRanking pizza : topPizzas) {
            JSONObject item = new JSONObject();
            item.put("pizzaId", pizza.getPizzaId());
            item.put("pizzaName", pizza.getPizzaName());
            item.put("totalQuantity", pizza.getTotalQuantity());
            item.put("totalRevenue", pizza.getTotalRevenue());
            item.put("percentage", pizza.getPercentage());
            result.put(item);
        }

        sendResponse(response, HttpServletResponse.SC_OK, result);
    }

    // Handle with category sales report
    private void handleCategorySalesReport(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        Map<String, Object> categoryStats = reportDAO.getCategorySalesStatistics();
        JSONObject result = new JSONObject();

        result.put("totalRevenue", categoryStats.get("totalRevenue"));

        JSONArray categories = new JSONArray();
        List<Map<String, Object>> categorySales = (List<Map<String, Object>>) categoryStats.get("categorySales");
        for (Map<String, Object> cat : categorySales) {
            JSONObject catObj = new JSONObject();
            catObj.put("category", cat.get("category"));
            catObj.put("orderCount", cat.get("orderCount"));
            catObj.put("totalQuantity", cat.get("totalQuantity"));
            catObj.put("totalRevenue", cat.get("totalRevenue"));
            categories.put(catObj);
        }
        result.put("categorySales", categories);

        sendResponse(response, HttpServletResponse.SC_OK, result);
    }

    // Handle with daily sales trend report
    private void handleDailyTrendReport(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        List<Map<String, Object>> dailyTrend = reportDAO.getDailySalesTrend();
        JSONArray result = new JSONArray();

        for (Map<String, Object> day : dailyTrend) {
            JSONObject item = new JSONObject();
            item.put("date", day.get("date"));
            item.put("orderCount", day.get("orderCount"));
            item.put("pizzasSold", day.get("pizzasSold"));
            item.put("dailyRevenue", day.get("dailyRevenue"));
            result.put(item);
        }

        sendResponse(response, HttpServletResponse.SC_OK, result);
    }

    // Handle with hourly sales analysis report
    private void handleHourlyAnalysisReport(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        List<Map<String, Object>> hourlyAnalysis = reportDAO.getHourlySalesAnalysis();
        JSONArray result = new JSONArray();

        for (Map<String, Object> hour : hourlyAnalysis) {
            JSONObject item = new JSONObject();
            item.put("hour", hour.get("hour"));
            item.put("orderCount", hour.get("orderCount"));
            item.put("pizzasSold", hour.get("pizzasSold"));
            result.put(item);
        }

        sendResponse(response, HttpServletResponse.SC_OK, result);
    }

    // Handle with member/nonmember comparison report
    private void handleMemberComparisonReport(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        Map<String, Object> comparison = reportDAO.getMemberVsNonMemberSales();
        JSONObject result = new JSONObject();

        if (comparison.containsKey("member")) {
            Map<String, Object> member = (Map<String, Object>) comparison.get("member");
            JSONObject memberObj = new JSONObject();
            memberObj.put("orderCount", member.get("orderCount"));
            memberObj.put("totalRevenue", member.get("totalRevenue"));
            memberObj.put("averageOrderValue", member.get("averageOrderValue"));
            result.put("members", memberObj);
        }

        if (comparison.containsKey("nonmember")) {
            Map<String, Object> nonMember = (Map<String, Object>) comparison.get("nonmember");
            JSONObject nonMemberObj = new JSONObject();
            nonMemberObj.put("orderCount", nonMember.get("orderCount"));
            nonMemberObj.put("totalRevenue", nonMember.get("totalRevenue"));
            nonMemberObj.put("averageOrderValue", nonMember.get("averageOrderValue"));
            result.put("nonMembers", nonMemberObj);
        }

        sendResponse(response, HttpServletResponse.SC_OK, result);
    }

    // Send success response(JSONObject)
    private void sendResponse(HttpServletResponse response, int statusCode, JSONObject jsonData)
            throws IOException {
        response.setStatus(statusCode);
        PrintWriter out = response.getWriter();
        out.print(jsonData.toString());
        out.flush();
    }

    // Send success response(JSONArray)
    private void sendResponse(HttpServletResponse response, int statusCode, JSONArray jsonArray)
            throws IOException {
        response.setStatus(statusCode);
        PrintWriter out = response.getWriter();
        out.print(jsonArray.toString());
        out.flush();
    }

    // Send error response
    private void sendError(HttpServletResponse response, int statusCode, String message)
            throws IOException {
        response.setStatus(statusCode);
        JSONObject error = new JSONObject();
        error.put("error", message);
        error.put("timestamp", System.currentTimeMillis());
        PrintWriter out = response.getWriter();
        out.print(error.toString());
        out.flush();
    }
}