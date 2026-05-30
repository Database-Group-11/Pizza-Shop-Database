package main.java.edu.group11.servlet;

import main.java.edu.group11.dao.PizzaDAO;
import main.java.edu.group11.model.Pizza;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/pizzas/*")
public class PizzaServlet extends HttpServlet {
    private PizzaDAO pizzaDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        pizzaDAO = new PizzaDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Check if there is a category query parameter
            String categoryParam = request.getParameter("category");
            if (categoryParam != null && !categoryParam.isEmpty()) {
                List<Pizza> pizzas = pizzaDAO.getPizzasByCategory(categoryParam);
                sendResponse(response, 200, "success", pizzasToJson(pizzas));
            }
            // GET /api/pizzas - get all pizzas
            else if (pathInfo == null || "/".equals(pathInfo)) {
                List<Pizza> pizzas = pizzaDAO.getAllPizzas();
                sendResponse(response, 200, "success", pizzasToJson(pizzas));
            }
            // GET /api/pizzas/available - get available pizzas
            else if ("/available".equals(pathInfo)) {
                List<Pizza> pizzas = pizzaDAO.getAvailable();
                sendResponse(response, 200, "success", pizzasToJson(pizzas));
            }
            // GET /api/pizzas/category/{category} - get by category
            else if (pathInfo.startsWith("/category/")) {
                String category = pathInfo.substring(10);
                List<Pizza> pizzas = pizzaDAO.getPizzasByCategory(category);
                sendResponse(response, 200, "success", pizzasToJson(pizzas));
            }
            // GET /api/pizzas/{id} - get a single pizza
            else {
                int id = Integer.parseInt(pathInfo.substring(1));
                Pizza pizza = pizzaDAO.getPizzaById(id);
                if (pizza != null) {
                    sendResponse(response, 200, "success", pizzaToJson(pizza));
                } else {
                    sendResponse(response, 404, "Pizza doesn't exist", null);
                }
            }
        } catch (NumberFormatException e) {
            sendResponse(response, 400, "Parameter format error", null);
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(response, 500, "Server inner error: " + e.getMessage(), null);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String requestBody = sb.toString();

            Pizza pizza = parsePizzaFromJson(requestBody);
            boolean success = pizzaDAO.addPizza(pizza);

            if (success) {
                String data = "{\"pizza_id\":" + pizza.getPizzaId() + "}";
                sendResponse(response, 200, "Successfully added pizza", data);
            } else {
                sendResponse(response, 500, "Failed to add pizza", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(response, 500, "Server inner error: " + e.getMessage(), null);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            if (pathInfo == null || "/".equals(pathInfo)) {
                sendResponse(response, 400, "Please assign pizza ID", null);
                return;
            }

            int id = Integer.parseInt(pathInfo.substring(1));
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String requestBody = sb.toString();

            Pizza pizza = parsePizzaFromJson(requestBody);
            pizza.setPizzaId(id);

            boolean success = pizzaDAO.updatePizza(pizza);
            if (success) {
                sendResponse(response, 200, "Successfully update pizza", null);
            } else {
                sendResponse(response, 500, "Failed to add pizza", null);
            }
        } catch (NumberFormatException e) {
            sendResponse(response, 400, "Parameter format error", null);
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(response, 500, "Server inner error: " + e.getMessage(), null);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            if (pathInfo == null || "/".equals(pathInfo)) {
                sendResponse(response, 400, "Please assign pizza ID", null);
                return;
            }

            int id = Integer.parseInt(pathInfo.substring(1));
            boolean success = pizzaDAO.deletePizza(id);
            if (success) {
                sendResponse(response, 200, "Successfully delete pizza", null);
            } else {
                sendResponse(response, 500, "Failed to delete pizza", null);
            }
        } catch (NumberFormatException e) {
            sendResponse(response, 400, "Parameter format error", null);
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(response, 500, "Server internal error: " + e.getMessage(), null);
        }
    }

    private Pizza parsePizzaFromJson(String json) {
        Pizza pizza = new Pizza();
        pizza.setName(extractJsonValue(json, "name"));
        pizza.setDescription(extractJsonValue(json, "description"));
        // Compatible with both field name formats
        String basePriceStr = extractJsonValue(json, "basePrice");
        if (basePriceStr == null) {
            basePriceStr = extractJsonValue(json, "base_price");
        }
        pizza.setBasePrice(Double.parseDouble(basePriceStr));
        pizza.setCategory(extractJsonValue(json, "category"));
        pizza.setImage(extractJsonValue(json, "image"));
        String available = extractJsonValue(json, "available");
        if (available != null) {
            pizza.setAvailable(Boolean.parseBoolean(available));
        }
        return pizza;
    }

    private String pizzaToJson(Pizza pizza) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"pizzaId\":").append(pizza.getPizzaId()).append(",");
        json.append("\"name\":\"").append(escapeJson(pizza.getName())).append("\",");
        json.append("\"description\":\"").append(escapeJson(pizza.getDescription())).append("\",");
        json.append("\"basePrice\":").append(pizza.getBasePrice()).append(",");
        json.append("\"category\":\"").append(escapeJson(pizza.getCategory())).append("\",");
        json.append("\"image\":\"").append(escapeJson(pizza.getImage())).append("\",");
        json.append("\"available\":").append(pizza.isAvailable());
        json.append("}");
        return json.toString();
    }

    private String pizzasToJson(List<Pizza> pizzas) {
        StringBuilder json = new StringBuilder();
        json.append("[");
        for (int i = 0; i < pizzas.size(); i++) {
            if (i > 0) json.append(",");
            json.append(pizzaToJson(pizzas.get(i)));
        }
        json.append("]");
        return json.toString();
    }

    private String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\"";
        int keyIndex = json.indexOf(searchKey);
        if (keyIndex == -1) return null;

        int colonIndex = json.indexOf(":", keyIndex);
        if (colonIndex == -1) return null;

        while (colonIndex + 1 < json.length() && json.charAt(colonIndex + 1) == ' ') {
            colonIndex++;
        }

        if (colonIndex + 1 >= json.length()) return null;

        char firstChar = json.charAt(colonIndex + 1);
        if (firstChar == '"') {
            int startQuote = colonIndex + 1;
            int endQuote = json.indexOf("\"", startQuote + 1);
            if (endQuote == -1) return null;
            return json.substring(startQuote + 1, endQuote);
        } else {
            int endValue = colonIndex + 1;
            while (endValue < json.length() &&
                    (Character.isDigit(json.charAt(endValue)) ||
                            json.charAt(endValue) == '.' ||
                            json.charAt(endValue) == '-' ||
                            json.charAt(endValue) == 't' ||
                            json.charAt(endValue) == 'f')) {
                endValue++;
            }
            return json.substring(colonIndex + 1, endValue);
        }
    }

    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private void sendResponse(HttpServletResponse response, int code, String message, String data)
            throws IOException {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"code\":").append(code).append(",");
        json.append("\"message\":\"").append(escapeJson(message)).append("\"");
        if (data != null) {
            json.append(",\"data\":").append(data);
        } else {
            json.append(",\"data\":null");
        }
        json.append("}");

        PrintWriter out = response.getWriter();
        out.print(json.toString());
        out.flush();
    }
}