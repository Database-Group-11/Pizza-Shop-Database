package main.java.edu.group11.servlet;

import main.java.edu.group11.dao.DeliveryDAO;

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

@WebServlet("/api/deliveries/*")
public class DeliveryServlet extends HttpServlet {

    private DeliveryDAO deliveryDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        deliveryDAO = new DeliveryDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // 获取所有进行中的配送
                List<Map<String, Object>> deliveries = deliveryDAO.getActiveDeliveries();
                sendResponse(response, HttpServletResponse.SC_OK, convertToJSONArray(deliveries));
            } else {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length == 2) {
                    String action = pathParts[1];

                    switch (action) {
                        case "order":
                            // GET /api/deliveries/order?orderId=123
                            String orderIdParam = request.getParameter("orderId");
                            if (orderIdParam != null && !orderIdParam.isEmpty()) {
                                int orderId = Integer.parseInt(orderIdParam);
                                Map<String, Object> delivery = deliveryDAO.getDeliveryByOrderId(orderId);
                                if (delivery != null && !delivery.isEmpty()) {
                                    sendResponse(response, HttpServletResponse.SC_OK, convertToJSON(delivery));
                                } else {
                                    sendError(response, HttpServletResponse.SC_NOT_FOUND, "配送信息未找到");
                                }
                            } else {
                                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "缺少订单ID参数");
                            }
                            break;

                        case "driver":
                            // GET /api/deliveries/driver?name=张三
                            String driverName = request.getParameter("name");
                            if (driverName != null && !driverName.isEmpty()) {
                                List<Map<String, Object>> deliveries = deliveryDAO.getDeliveriesByDriver(driverName);
                                sendResponse(response, HttpServletResponse.SC_OK, convertToJSONArray(deliveries));
                            } else {
                                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "缺少骑手姓名参数");
                            }
                            break;

                        case "overdue":
                            // GET /api/deliveries/overdue
                            List<Map<String, Object>> overdueDeliveries = deliveryDAO.getOverdueDeliveries();
                            sendResponse(response, HttpServletResponse.SC_OK, convertToJSONArray(overdueDeliveries));
                            break;

                        case "statistics":
                            // GET /api/deliveries/statistics
                            Map<String, Object> stats = deliveryDAO.getTodayDeliveryStatistics();
                            sendResponse(response, HttpServletResponse.SC_OK, convertToJSON(stats));
                            break;

                        default:
                            // GET /api/deliveries/{id}
                            try {
                                int deliveryId = Integer.parseInt(action);
                                Map<String, Object> delivery = deliveryDAO.getDeliveryById(deliveryId);
                                if (delivery != null && !delivery.isEmpty()) {
                                    sendResponse(response, HttpServletResponse.SC_OK, convertToJSON(delivery));
                                } else {
                                    sendError(response, HttpServletResponse.SC_NOT_FOUND, "配送信息未找到");
                                }
                            } catch (NumberFormatException e) {
                                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "无效的请求路径");
                            }
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // POST /api/deliveries/ - 创建配送记录
                JSONObject jsonData = parseJSONFromRequest(request);
                if (jsonData != null) {
                    int deliveryId = deliveryDAO.createDelivery(jsonData);
                    if (deliveryId > 0) {
                        JSONObject result = new JSONObject();
                        result.put("success", true);
                        result.put("message", "配送记录创建成功");
                        result.put("deliveryId", deliveryId);
                        sendResponse(response, HttpServletResponse.SC_CREATED, result);
                    } else {
                        sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "创建配送记录失败");
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
                            // POST /api/deliveries/assign - 分配骑手
                            handleAssignDriver(jsonData, response);
                            break;

                        case "start":
                            // POST /api/deliveries/start - 开始配送
                            handleStartDelivery(jsonData, response);
                            break;

                        case "complete":
                            // POST /api/deliveries/complete - 完成配送
                            handleCompleteDelivery(jsonData, response);
                            break;

                        case "cancel":
                            // POST /api/deliveries/cancel - 取消配送
                            handleCancelDelivery(jsonData, response);
                            break;

                        case "tracking":
                            // POST /api/deliveries/tracking - 更新追踪号
                            handleUpdateTracking(jsonData, response);
                            break;

                        case "notes":
                            // POST /api/deliveries/notes - 添加备注
                            handleAddNotes(jsonData, response);
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
                        int deliveryId = Integer.parseInt(pathParts[1]);
                        JSONObject jsonData = parseJSONFromRequest(request);
                        if (jsonData != null) {
                            boolean success = false;

                            // 根据请求参数判断更新类型
                            String action = request.getParameter("action");
                            if ("status".equals(action) && jsonData.has("deliveryStatus")) {
                                String status = jsonData.getString("deliveryStatus");
                                success = deliveryDAO.updateDeliveryStatus(deliveryId, status);
                            } else {
                                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "不支持的更新操作");
                                return;
                            }

                            if (success) {
                                JSONObject result = new JSONObject();
                                result.put("success", true);
                                result.put("message", "配送信息更新成功");
                                sendResponse(response, HttpServletResponse.SC_OK, result);
                            } else {
                                sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "更新配送信息失败");
                            }
                        } else {
                            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "无效的JSON数据");
                        }
                    } catch (NumberFormatException e) {
                        sendError(response, HttpServletResponse.SC_BAD_REQUEST, "无效的配送ID");
                    }
                } else {
                    sendError(response, HttpServletResponse.SC_BAD_REQUEST, "无效的请求路径");
                }
            } else {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "缺少配送ID");
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
                    try {
                        int deliveryId = Integer.parseInt(pathParts[1]);
                        boolean success = deliveryDAO.cancelDelivery(deliveryId);
                        if (success) {
                            JSONObject result = new JSONObject();
                            result.put("success", true);
                            result.put("message", "配送已取消");
                            sendResponse(response, HttpServletResponse.SC_OK, result);
                        } else {
                            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "取消失败");
                        }
                    } catch (NumberFormatException e) {
                        sendError(response, HttpServletResponse.SC_BAD_REQUEST, "无效的配送ID");
                    }
                } else {
                    sendError(response, HttpServletResponse.SC_BAD_REQUEST, "无效的请求路径");
                }
            } else {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "缺少配送ID");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "服务器错误: " + e.getMessage());
        }
    }

    // 处理分配骑手
    private void handleAssignDriver(JSONObject jsonData, HttpServletResponse response)
            throws IOException {
        if (!jsonData.has("deliveryId") || !jsonData.has("driverName")) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "缺少必要参数");
            return;
        }

        int deliveryId = jsonData.getInt("deliveryId");
        String driverName = jsonData.getString("driverName");
        String driverPhone = jsonData.optString("driverPhone", "");

        boolean success = deliveryDAO.assignDriver(deliveryId, driverName, driverPhone);
        if (success) {
            JSONObject result = new JSONObject();
            result.put("success", true);
            result.put("message", "骑手分配成功");
            sendResponse(response, HttpServletResponse.SC_OK, result);
        } else {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "骑手分配失败");
        }
    }

    // 处理开始配送
    private void handleStartDelivery(JSONObject jsonData, HttpServletResponse response)
            throws IOException {
        if (!jsonData.has("deliveryId")) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "缺少配送ID参数");
            return;
        }

        int deliveryId = jsonData.getInt("deliveryId");
        boolean success = deliveryDAO.startDelivery(deliveryId);

        if (success) {
            JSONObject result = new JSONObject();
            result.put("success", true);
            result.put("message", "配送已开始");
            sendResponse(response, HttpServletResponse.SC_OK, result);
        } else {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "开始配送失败");
        }
    }

    // 处理完成配送
    private void handleCompleteDelivery(JSONObject jsonData, HttpServletResponse response)
            throws IOException {
        if (!jsonData.has("deliveryId")) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "缺少配送ID参数");
            return;
        }

        int deliveryId = jsonData.getInt("deliveryId");
        boolean success = deliveryDAO.completeDelivery(deliveryId);

        if (success) {
            JSONObject result = new JSONObject();
            result.put("success", true);
            result.put("message", "配送已完成");
            sendResponse(response, HttpServletResponse.SC_OK, result);
        } else {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "完成配送失败");
        }
    }

    // 处理取消配送
    private void handleCancelDelivery(JSONObject jsonData, HttpServletResponse response)
            throws IOException {
        if (!jsonData.has("deliveryId")) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "缺少配送ID参数");
            return;
        }

        int deliveryId = jsonData.getInt("deliveryId");
        boolean success = deliveryDAO.cancelDelivery(deliveryId);

        if (success) {
            JSONObject result = new JSONObject();
            result.put("success", true);
            result.put("message", "配送已取消");
            sendResponse(response, HttpServletResponse.SC_OK, result);
        } else {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "取消配送失败");
        }
    }

    // 处理更新追踪号
    private void handleUpdateTracking(JSONObject jsonData, HttpServletResponse response)
            throws IOException {
        if (!jsonData.has("deliveryId") || !jsonData.has("trackingNumber")) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "缺少必要参数");
            return;
        }

        int deliveryId = jsonData.getInt("deliveryId");
        String trackingNumber = jsonData.getString("trackingNumber");

        boolean success = deliveryDAO.updateTrackingNumber(deliveryId, trackingNumber);
        if (success) {
            JSONObject result = new JSONObject();
            result.put("success", true);
            result.put("message", "追踪号更新成功");
            sendResponse(response, HttpServletResponse.SC_OK, result);
        } else {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "更新追踪号失败");
        }
    }

    // 处理添加备注
    private void handleAddNotes(JSONObject jsonData, HttpServletResponse response)
            throws IOException {
        if (!jsonData.has("deliveryId") || !jsonData.has("notes")) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "缺少必要参数");
            return;
        }

        int deliveryId = jsonData.getInt("deliveryId");
        String notes = jsonData.getString("notes");

        boolean success = deliveryDAO.addDeliveryNotes(deliveryId, notes);
        if (success) {
            JSONObject result = new JSONObject();
            result.put("success", true);
            result.put("message", "备注添加成功");
            sendResponse(response, HttpServletResponse.SC_OK, result);
        } else {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "添加备注失败");
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
        PrintWriter out = response.getWriter();
        out.print(error.toString());
        out.flush();
    }
}