package main.java.edu.group11.servlet;

import main.java.edu.group11.dao.ReportDAO;
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
                // 获取所有报表类型列表
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
                            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "未知的报表类型");
                            break;
                    }
                } else {
                    sendError(response, HttpServletResponse.SC_BAD_REQUEST, "无效的请求路径");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "服务器错误: " + e.getMessage());
        }
    }

    // 处理销售报表
    private void handleSalesReport(HttpServletRequest request, HttpServletResponse response, String[] pathParts)
            throws IOException {
        if (pathParts.length < 3) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "请指定销售报表类型");
            return;
        }

        String period = pathParts[2];
        JSONObject result = new JSONObject();

        switch (period) {
            case "today":
                Map<String, Object> todayReport = reportDAO.getTodaySalesReport();
                result = convertToJSON(todayReport);
                break;
            case "weekly":
                Map<String, Object> weeklyReport = reportDAO.getWeeklySalesReport();
                result = convertToJSON(weeklyReport);
                break;
            case "monthly":
                Map<String, Object> monthlyReport = reportDAO.getMonthlySalesReport();
                result = convertToJSON(monthlyReport);
                break;
            case "range":
                // GET /api/reports/sales/range?startDate=2024-01-01&endDate=2024-01-31
                String startDate = request.getParameter("startDate");
                String endDate = request.getParameter("endDate");
                if (startDate == null || endDate == null) {
                    sendError(response, HttpServletResponse.SC_BAD_REQUEST, "请提供开始日期和结束日期");
                    return;
                }
                Map<String, Object> rangeReport = reportDAO.getSalesReportByDateRange(startDate, endDate);
                result = convertToJSON(rangeReport);
                break;
            default:
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "无效的销售报表类型");
                return;
        }

        sendResponse(response, HttpServletResponse.SC_OK, result);
    }

    // 处理热门披萨报表
    private void handleTopPizzasReport(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String limitParam = request.getParameter("limit");
        int limit = 10; // 默认10个

        if (limitParam != null && !limitParam.isEmpty()) {
            try {
                limit = Integer.parseInt(limitParam);
                if (limit > 50) limit = 50; // 限制最大50
            } catch (NumberFormatException e) {
                // 使用默认值
            }
        }

        List<Map<String, Object>> topPizzas = reportDAO.getTopSellingPizzas(limit);
        JSONArray result = convertToJSONArray(topPizzas);
        sendResponse(response, HttpServletResponse.SC_OK, result);
    }

    // 处理分类销售报表
    private void handleCategorySalesReport(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        Map<String, Object> categoryStats = reportDAO.getCategorySalesStatistics();
        JSONObject result = convertToJSON(categoryStats);
        sendResponse(response, HttpServletResponse.SC_OK, result);
    }

    // 处理每日销售趋势报表
    private void handleDailyTrendReport(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        List<Map<String, Object>> dailyTrend = reportDAO.getDailySalesTrend();
        JSONArray result = convertToJSONArray(dailyTrend);
        sendResponse(response, HttpServletResponse.SC_OK, result);
    }

    // 处理小时销售分析报表
    private void handleHourlyAnalysisReport(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        List<Map<String, Object>> hourlyAnalysis = reportDAO.getHourlySalesAnalysis();
        JSONArray result = convertToJSONArray(hourlyAnalysis);
        sendResponse(response, HttpServletResponse.SC_OK, result);
    }

    // 处理会员/非会员对比报表
    private void handleMemberComparisonReport(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        Map<String, Object> comparison = reportDAO.getMemberVsNonMemberSales();
        JSONObject result = convertToJSON(comparison);
        sendResponse(response, HttpServletResponse.SC_OK, result);
    }

    // 转换Map为JSONObject
    private JSONObject convertToJSON(Map<String, Object> map) {
        JSONObject json = new JSONObject();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Map) {
                json.put(entry.getKey(), convertToJSON((Map<String, Object>) value));
            } else if (value instanceof List) {
                json.put(entry.getKey(), convertToJSONArray((List<Map<String, Object>>) value));
            } else {
                json.put(entry.getKey(), value);
            }
        }
        return json;
    }

    // 转换Map列表为JSONArray
    private JSONArray convertToJSONArray(List<Map<String, Object>> list) {
        JSONArray array = new JSONArray();
        for (Map<String, Object> map : list) {
            array.put(convertToJSON(map));
        }
        return array;
    }

    // 发送成功响应（JSONObject）
    private void sendResponse(HttpServletResponse response, int statusCode, JSONObject jsonData)
            throws IOException {
        response.setStatus(statusCode);
        PrintWriter out = response.getWriter();
        out.print(jsonData.toString());
        out.flush();
    }

    // 发送成功响应（JSONArray）
    private void sendResponse(HttpServletResponse response, int statusCode, JSONArray jsonArray)
            throws IOException {
        response.setStatus(statusCode);
        PrintWriter out = response.getWriter();
        out.print(jsonArray.toString());
        out.flush();
    }

    // 发送错误响应
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