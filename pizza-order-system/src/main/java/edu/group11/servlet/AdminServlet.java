package main.java.edu.group11.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import main.java.edu.group11.dao.OrderDAO;
import main.java.edu.group11.dto.request.AdminLoginRequest;
import main.java.edu.group11.dto.response.Result;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/admin/*")
public class AdminServlet extends HttpServlet {
    private OrderDAO orderDAO = new OrderDAO();
    private Gson gson = new Gson();

    private void setCorsHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        resp.setHeader("Access-Control-Max-Age", "3600");
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        setCorsHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        setCorsHeaders(resp);
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json;charset=UTF-8");

        if ("/login".equals(pathInfo)) {
            handleLogin(req, resp);
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(gson.toJson(Result.error(404, "Interface doesn't exist")));
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        setCorsHeaders(resp);
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json;charset=UTF-8");

        if ("/orders".equals(pathInfo)) {
            handleGetAllOrders(req, resp);
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(gson.toJson(Result.error(404, "Interface doesn't exist")));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        setCorsHeaders(resp);
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json;charset=UTF-8");

        if (pathInfo != null && pathInfo.startsWith("/orders/") && pathInfo.endsWith("/status")) {
            String orderIdStr = pathInfo.substring(8, pathInfo.length() - 7);
            handleUpdateOrderStatus(req, resp, orderIdStr);
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(gson.toJson(Result.error(404, "Interface doesn't exist")));
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BufferedReader reader = req.getReader();
        AdminLoginRequest loginReq = gson.fromJson(reader, AdminLoginRequest.class);

        if ("admin".equals(loginReq.getUsername()) && "admin123".equals(loginReq.getPassword())) {
            JsonObject data = new JsonObject();
            data.addProperty("username", "admin");
            data.addProperty("token", "admin-token-" + System.currentTimeMillis());
            resp.getWriter().write(gson.toJson(Result.success(data)));
        } else {
            resp.getWriter().write(gson.toJson(Result.error(401, "Wrong username or password")));
        }
    }

    private void handleGetAllOrders(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            List<main.java.edu.group11.model.Order> orders = orderDAO.findAll();
            resp.getWriter().write(gson.toJson(Result.success(orders)));
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write(gson.toJson(Result.error(500, "Failed to load orders: " + e.getMessage())));
        }
    }

    private void handleUpdateOrderStatus(HttpServletRequest req, HttpServletResponse resp, String orderIdStr) throws IOException {
        try {
            int orderId = Integer.parseInt(orderIdStr);
            BufferedReader reader = req.getReader();
            JsonObject body = gson.fromJson(reader, JsonObject.class);
            String status = body.get("status").getAsString();

            boolean success = orderDAO.updateStatus(orderId, status);
            if (success) {
                resp.getWriter().write(gson.toJson(Result.success("Order status updated")));
            } else {
                resp.getWriter().write(gson.toJson(Result.error(500, "Failed to update status")));
            }
        } catch (NumberFormatException e) {
            resp.getWriter().write(gson.toJson(Result.error(400, "Invalid order ID")));
        }
    }
}
