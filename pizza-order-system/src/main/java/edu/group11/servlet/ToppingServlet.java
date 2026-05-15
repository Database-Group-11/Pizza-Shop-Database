package main.java.edu.group11.servlet;

import main.java.edu.group11.dao.ToppingDAO;
import org.json.JSONArray;
import org.json.JSONObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@WebServlet("/api/toppings/*")
public class ToppingServlet extends HttpServlet {

    private ToppingDAO toppingDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        toppingDAO = new ToppingDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /api/toppings/ - 获取所有可用配料
                List<Map<String, Object>> toppings = toppingDAO.getAllToppings();
                sendResponse(response, HttpServletResponse.SC_OK, convertToJSONArray(toppings));
            } else {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length == 2) {
                    String action = pathParts[1];

                    switch (action) {
                        case "available":
                            // GET /api/toppings/available - 获取可用配料
                            List<Map<String, Object>> availableToppings = toppingDAO.getAvailableToppings();
                            sendResponse(response, HttpServletResponse.SC_OK, convertToJSONArray(availableToppings));
                            break;

                        case "category":
                            // GET /api/toppings/category?type=meat
                            String category = request.getParameter("type");
                            if (category != null && !category.isEmpty()) {
                                List<Map<String, Object>> toppingsByCategory = toppingDAO.getToppingsByCategory(category);
                                sendResponse(response, HttpServletResponse.SC_OK, convertToJSONArray(toppingsByCategory));
                            } else {
                                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "缺少分类参数");
                            }
                            break;

                        case "popular":
                            // GET /api/toppings/popular?limit=5
                            String limitParam = request.getParameter("limit");
                            int limit = 5;
                            if (limitParam != null && !limitParam.isEmpty()) {
                                limit = Integer.parseInt(limitParam);
                            }
                            List<Map<String, Object>> popularToppings = toppingDAO.getPopularToppings(limit);
                            sendResponse(response, HttpServletResponse.SC_OK, convertToJSONArray(popularToppings));
                            break;

                        case "low-stock":
                            // GET /api/toppings/low-stock - 获取低库存配料
                            List<Map<String, Object>> lowStockToppings = toppingDAO.getLowStockToppings();
                            sendResponse(response, HttpServletResponse.SC_OK, convertToJSONArray(lowStockToppings));
                            break;

                        default:
                            // GET /api/toppings/{id}
                            try {
                                int toppingId = Integer.parseInt(action);
                                Map<String, Object> topping = toppingDAO.getToppingById(toppingId);
                                if (topping != null && !topping.isEmpty()) {
                                    sendResponse(response, HttpServletResponse.SC_OK, convertToJSON(topping));
                                } else {
                                    sendError(response, HttpServletResponse.SC_NOT_FOUND, "配料未找到");
                                }
                            } catch (NumberFormatException e) {
                                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "无效的请求路径");
                            }
                            break;
                    }
                } else if (pathParts.length == 3 && "pizza".equals(pathParts[1])) {
                    // GET /api/toppings/pizza/{pizzaId}
                    try {
                        int pizzaId = Integer.parseInt(pathParts[2]);
                        List<Map<String, Object>> pizzaToppings = toppingDAO.getToppingsByPizzaId(pizzaId);
                        sendResponse(response, HttpServletResponse.SC_OK, convertToJSONArray(pizzaToppings));
                    } catch (NumberFormatException e) {
                        sendError(response, HttpServletResponse.SC_BAD_REQUEST, "无效的披萨ID");
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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // POST /api/toppings/ - 创建新配料（管理员）
                JSONObject jsonData = parseJSONFromRequest(request);
                if (jsonData != null) {
                    int toppingId = toppingDAO.createTopping(jsonData);
                    if (toppingId > 0) {
                        JSONObject result = new JSONObject();
                        result.put("success", true);
                        result.put("message", "配料创建成功");
                        result.put("toppingId", toppingId);
                        sendResponse(response, HttpServletResponse.SC_CREATED, result);
                    } else {
                        sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "创建配料失败");
                    }
                } else {
                    sendError(response, HttpServletResponse.SC_BAD_REQUEST, "无效的JSON数据");
                }
            } else {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length == 2) {
                    String action = pathParts[1];
                    JSONObject jsonData = parseJSONFromRequest(request);

                    if (jsonData == null) {
                        sendError(response, HttpServletResponse.SC_BAD_REQUEST, "无效的JSON数据");
                        return;
                    }

                    switch (action) {
                        case "assign":
                            // POST /api/toppings/assign - 为披萨分配配料
                            handleAssignTopping(jsonData, response);
                            break;

                        case "update-stock":
                            // POST /api/toppings/update-stock - 更新库存
                            handleUpdateStock(jsonData, response);
                            break;

                        default:
                            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "未知的操作");
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

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            if (pathInfo != null && !pathInfo.equals("/")) {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length == 2) {
                    try {
                        int toppingId = Integer.parseInt(pathParts[1]);
                        JSONObject jsonData = parseJSONFromRequest(request);
                        if (jsonData != null) {
                            boolean success = toppingDAO.updateTopping(toppingId, jsonData);
                            if (success) {
                                JSONObject result = new JSONObject();
                                result.put("success", true);
                                result.put("message", "配料更新成功");
                                sendResponse(response, HttpServletResponse.SC_OK, result);
                            } else {
                                sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "更新配料失败");
                            }
                        } else {
                            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "无效的JSON数据");
                        }
                    } catch (NumberFormatException e) {
                        sendError(response, HttpServletResponse.SC_BAD_REQUEST, "无效的配料ID");
                    }
                } else {
                    sendError(response, HttpServletResponse.SC_BAD_REQUEST, "无效的请求路径");
                }
            } else {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "缺少配料ID");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "服务器错误: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            if (pathInfo != null && !pathInfo.equals("/")) {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length == 2) {
                    // DELETE /api/toppings/{id} - 删除配料
                    try {
                        int toppingId = Integer.parseInt(pathParts[1]);
                        boolean success = toppingDAO.deleteTopping(toppingId);
                        if (success) {
                            JSONObject result = new JSONObject();
                            result.put("success", true);
                            result.put("message", "配料删除成功");
                            sendResponse(response, HttpServletResponse.SC_OK, result);
                        } else {
                            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "删除配料失败");
                        }
                    } catch (NumberFormatException e) {
                        sendError(response, HttpServletResponse.SC_BAD_REQUEST, "无效的配料ID");
                    }
                } else if (pathParts.length == 3 && "pizza".equals(pathParts[1])) {
                    // DELETE /api/toppings/pizza/{relationId} - 移除披萨的配料
                    try {
                        int relationId = Integer.parseInt(pathParts[2]);
                        boolean success = toppingDAO.removePizzaTopping(relationId);
                        if (success) {
                            JSONObject result = new JSONObject();
                            result.put("success", true);
                            result.put("message", "配料已从披萨中移除");
                            sendResponse(response, HttpServletResponse.SC_OK, result);
                        } else {
                            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "移除配料失败");
                        }
                    } catch (NumberFormatException e) {
                        sendError(response, HttpServletResponse.SC_BAD_REQUEST, "无效的关系ID");
                    }
                } else {
                    sendError(response, HttpServletResponse.SC_BAD_REQUEST, "无效的请求路径");
                }
            } else {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "缺少配料ID");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "服务器错误: " + e.getMessage());
        }
    }

    // 处理分配配料到披萨
    private void handleAssignTopping(JSONObject jsonData, HttpServletResponse response)
            throws IOException {
        if (!jsonData.has("pizzaId") || !jsonData.has("toppingId")) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "缺少必要参数");
            return;
        }

        int pizzaId = jsonData.getInt("pizzaId");
        int toppingId = jsonData.getInt("toppingId");
        int quantity = jsonData.optInt("quantity", 1);

        boolean success = toppingDAO.assignToppingToPizza(pizzaId, toppingId, quantity);
        if (success) {
            JSONObject result = new JSONObject();
            result.put("success", true);
            result.put("message", "配料分配成功");
            sendResponse(response, HttpServletResponse.SC_OK, result);
        } else {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "配料分配失败");
        }
    }

    // 处理更新库存
    private void handleUpdateStock(JSONObject jsonData, HttpServletResponse response)
            throws IOException {
        if (!jsonData.has("toppingId") || !jsonData.has("stock")) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "缺少必要参数");
            return;
        }

        int toppingId = jsonData.getInt("toppingId");
        int stock = jsonData.getInt("stock");

        boolean success = toppingDAO.updateStock(toppingId, stock);
        if (success) {
            JSONObject result = new JSONObject();
            result.put("success", true);
            result.put("message", "库存更新成功");
            sendResponse(response, HttpServletResponse.SC_OK, result);
        } else {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "库存更新失败");
        }
    }

    // 从请求中解析JSON数据
    private JSONObject parseJSONFromRequest(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }

        if (sb.length() > 0) {
            try {
                return new JSONObject(sb.toString());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
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