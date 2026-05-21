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
                // GET /api/toppings/ - get available toppings
                List<Map<String, Object>> toppings = toppingDAO.getAllToppings();
                JSONObject result = new JSONObject();
                result.put("code", 200);
                result.put("message", "success");
                result.put("data", convertToJSONArray(toppings));
                sendResponse(response, HttpServletResponse.SC_OK, result);
            } else {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length == 2) {
                    String action = pathParts[1];

                    switch (action) {
                        case "available":
                            List<Map<String, Object>> availableToppings = toppingDAO.getAvailableToppings();
                            JSONObject availableResult = new JSONObject();
                            availableResult.put("code", 200);
                            availableResult.put("message", "success");
                            availableResult.put("data", convertToJSONArray(availableToppings));
                            sendResponse(response, HttpServletResponse.SC_OK, availableResult);
                            break;

                        case "category":
                            String category = request.getParameter("type");
                            if (category != null && !category.isEmpty()) {
                                List<Map<String, Object>> toppingsByCategory = toppingDAO.getToppingsByCategory(category);
                                JSONObject categoryResult = new JSONObject();
                                categoryResult.put("code", 200);
                                categoryResult.put("message", "success");
                                categoryResult.put("data", convertToJSONArray(toppingsByCategory));
                                sendResponse(response, HttpServletResponse.SC_OK, categoryResult);
                            } else {
                                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Lacks category parameter");
                            }
                            break;

                        case "popular":
                            String limitParam = request.getParameter("limit");
                            int limit = 5;
                            if (limitParam != null && !limitParam.isEmpty()) {
                                limit = Integer.parseInt(limitParam);
                            }
                            List<Map<String, Object>> popularToppings = toppingDAO.getPopularToppings(limit);
                            JSONObject popularResult = new JSONObject();
                            popularResult.put("code", 200);
                            popularResult.put("message", "success");
                            popularResult.put("data", convertToJSONArray(popularToppings));
                            sendResponse(response, HttpServletResponse.SC_OK, popularResult);
                            break;

                        case "low-stock":
                            List<Map<String, Object>> lowStockToppings = toppingDAO.getLowStockToppings();
                            JSONObject lowStockResult = new JSONObject();
                            lowStockResult.put("code", 200);
                            lowStockResult.put("message", "success");
                            lowStockResult.put("data", convertToJSONArray(lowStockToppings));
                            sendResponse(response, HttpServletResponse.SC_OK, lowStockResult);
                            break;

                        default:
                            try {
                                int toppingId = Integer.parseInt(action);
                                Map<String, Object> topping = toppingDAO.getToppingById(toppingId);
                                if (topping != null && !topping.isEmpty()) {
                                    JSONObject singleResult = new JSONObject();
                                    singleResult.put("code", 200);
                                    singleResult.put("message", "success");
                                    singleResult.put("data", convertToJSON(topping));
                                    sendResponse(response, HttpServletResponse.SC_OK, singleResult);
                                } else {
                                    sendError(response, HttpServletResponse.SC_NOT_FOUND, "Topping cannot be found");
                                }
                            } catch (NumberFormatException e) {
                                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request path");
                            }
                            break;
                    }
                } else if (pathParts.length == 3 && "pizza".equals(pathParts[1])) {
                    try {
                        int pizzaId = Integer.parseInt(pathParts[2]);
                        List<Map<String, Object>> pizzaToppings = toppingDAO.getToppingsByPizzaId(pizzaId);
                        JSONObject pizzaResult = new JSONObject();
                        pizzaResult.put("code", 200);
                        pizzaResult.put("message", "success");
                        pizzaResult.put("data", convertToJSONArray(pizzaToppings));
                        sendResponse(response, HttpServletResponse.SC_OK, pizzaResult);
                    } catch (NumberFormatException e) {
                        sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid pizza ID");
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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // POST /api/toppings/ - create new topping
                JSONObject jsonData = parseJSONFromRequest(request);
                if (jsonData != null) {
                    int toppingId = toppingDAO.createTopping(jsonData);
                    if (toppingId > 0) {
                        JSONObject result = new JSONObject();
                        result.put("success", true);
                        result.put("message", "Successfully created topping");
                        result.put("toppingId", toppingId);
                        sendResponse(response, HttpServletResponse.SC_CREATED, result);
                    } else {
                        sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to create topping");
                    }
                } else {
                    sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON data");
                }
            } else {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length == 2) {
                    String action = pathParts[1];
                    JSONObject jsonData = parseJSONFromRequest(request);

                    if (jsonData == null) {
                        sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON data");
                        return;
                    }

                    switch (action) {
                        case "assign":
                            // POST /api/toppings/assign - assign topping for pizza
                            handleAssignTopping(jsonData, response);
                            break;

                        case "update-stock":
                            // POST /api/toppings/update-stock - update stock
                            handleUpdateStock(jsonData, response);
                            break;

                        default:
                            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Unknown operation");
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
                                result.put("message", "Successfully updated topping");
                                sendResponse(response, HttpServletResponse.SC_OK, result);
                            } else {
                                sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update topping");
                            }
                        } else {
                            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON data");
                        }
                    } catch (NumberFormatException e) {
                        sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid topping ID");
                    }
                } else {
                    sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request path");
                }
            } else {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Lacks topping ID");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error" + e.getMessage());
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
                    // DELETE /api/toppings/{id} - delete topping
                    try {
                        int toppingId = Integer.parseInt(pathParts[1]);
                        boolean success = toppingDAO.deleteTopping(toppingId);
                        if (success) {
                            JSONObject result = new JSONObject();
                            result.put("success", true);
                            result.put("message", "Successfully deleted topping");
                            sendResponse(response, HttpServletResponse.SC_OK, result);
                        } else {
                            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to delete topping");
                        }
                    } catch (NumberFormatException e) {
                        sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid topping ID");
                    }
                } else if (pathParts.length == 3 && "pizza".equals(pathParts[1])) {
                    // DELETE /api/toppings/pizza/{relationId} - remove topping from pizza
                    try {
                        int relationId = Integer.parseInt(pathParts[2]);
                        boolean success = toppingDAO.removePizzaTopping(relationId);
                        if (success) {
                            JSONObject result = new JSONObject();
                            result.put("success", true);
                            result.put("message", "Successfully removed topping");
                            sendResponse(response, HttpServletResponse.SC_OK, result);
                        } else {
                            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to remove topping");
                        }
                    } catch (NumberFormatException e) {
                        sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid relation topping ID");
                    }
                } else {
                    sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request path");
                }
            } else {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Lacks topping ID");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error" + e.getMessage());
        }
    }

    // Handle with assigning topping to pizza
    private void handleAssignTopping(JSONObject jsonData, HttpServletResponse response)
            throws IOException {
        if (!jsonData.has("pizzaId") || !jsonData.has("toppingId")) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Lacks necessary parameters");
            return;
        }

        int pizzaId = jsonData.getInt("pizzaId");
        int toppingId = jsonData.getInt("toppingId");
        int quantity = jsonData.optInt("quantity", 1);

        boolean success = toppingDAO.assignToppingToPizza(pizzaId, toppingId, quantity);
        if (success) {
            JSONObject result = new JSONObject();
            result.put("success", true);
            result.put("message", "Successfully assigned topping");
            sendResponse(response, HttpServletResponse.SC_OK, result);
        } else {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to assign topping");
        }
    }

    // Handle with updating stock
    private void handleUpdateStock(JSONObject jsonData, HttpServletResponse response)
            throws IOException {
        if (!jsonData.has("toppingId") || !jsonData.has("stock")) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Lacks necessary parameters");
            return;
        }

        int toppingId = jsonData.getInt("toppingId");
        int stock = jsonData.getInt("stock");

        boolean success = toppingDAO.updateStock(toppingId, stock);
        if (success) {
            JSONObject result = new JSONObject();
            result.put("success", true);
            result.put("message", "Successfully updated stock");
            sendResponse(response, HttpServletResponse.SC_OK, result);
        } else {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update stock");
        }
    }

    // Analyse JSON data from request
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

    // Change Map to JSONObject
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

    // Change Map to JSONArray
    private JSONArray convertToJSONArray(List<Map<String, Object>> list) {
        JSONArray array = new JSONArray();
        for (Map<String, Object> map : list) {
            array.put(convertToJSON(map));
        }
        return array;
    }

    // Send success request(JSONObject)
    private void sendResponse(HttpServletResponse response, int statusCode, JSONObject jsonData)
            throws IOException {
        response.setStatus(statusCode);
        PrintWriter out = response.getWriter();
        out.print(jsonData.toString());
        out.flush();
    }

    // Send success request(JSONArray)
    private void sendResponse(HttpServletResponse response, int statusCode, JSONArray jsonArray)
            throws IOException {
        response.setStatus(statusCode);
        PrintWriter out = response.getWriter();
        out.print(jsonArray.toString());
        out.flush();
    }

    // Send error request
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