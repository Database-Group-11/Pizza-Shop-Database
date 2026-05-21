package main.java.edu.group11.servlet;

import com.google.gson.Gson;
import main.java.edu.group11.dao.OrderDAO;
import main.java.edu.group11.dao.OrderItemDAO;
import main.java.edu.group11.dao.PizzaDAO;
import main.java.edu.group11.dto.request.OrderCreateRequest;
import main.java.edu.group11.dto.request.OrderItemRequest;
import main.java.edu.group11.dto.response.OrderCreateResponse;
import main.java.edu.group11.dto.response.OrderResponse;
import main.java.edu.group11.dto.response.Result;
import main.java.edu.group11.model.Order;
import main.java.edu.group11.model.OrderItem;
import main.java.edu.group11.model.Pizza;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@WebServlet("/api/orders/*")
public class OrderServlet extends HttpServlet {
    private OrderDAO orderDAO = new OrderDAO();
    private OrderItemDAO orderItemDAO = new OrderItemDAO();
    private PizzaDAO pizzaDAO = new PizzaDAO();
    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json;charset=UTF-8");

        if (pathInfo == null || "/".equals(pathInfo)) {
            handleCreateOrder(req, resp);
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(gson.toJson(Result.error(404, "接口不存在")));
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json;charset=UTF-8");

        if ("/customer".equals(pathInfo)) {
            handleGetOrdersByCustomer(req, resp);
        } else if (pathInfo != null && pathInfo.startsWith("/")) {
            // 获取单个订单详情，如 /orders/123
            String orderIdStr = pathInfo.substring(1);
            handleGetOrderDetail(req, resp, orderIdStr);
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(gson.toJson(Result.error(404, "接口不存在")));
        }
    }

    private void handleCreateOrder(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BufferedReader reader = req.getReader();
        OrderCreateRequest orderReq = gson.fromJson(reader, OrderCreateRequest.class);
        // 验证登录状态
        int userId = orderReq.getCustomerId();
        if (userId <= 0) {
            resp.getWriter().write(gson.toJson(Result.error(401, "请先登录")));
            return;
        }

        // 验证订单项
        if (orderReq.getItems() == null || orderReq.getItems().isEmpty()) {
            resp.getWriter().write(gson.toJson(Result.error(400, "订单不能为空")));
            return;
        }

        try {
            // 构建订单对象
            Order order = new Order();
            order.setOrderNo(generateOrderNo());
            order.setCustomerId(orderReq.getCustomerId());
            order.setTotalAmount(orderReq.getTotalAmount());
            order.setDeliveryAddress(orderReq.getDeliveryAddress());
            order.setPaymentMethod(orderReq.getPaymentMethod());

            // 构建订单项列表
            List<OrderItem> items = new ArrayList<>();
            for (OrderItemRequest itemReq : orderReq.getItems()) {
                Pizza pizza = pizzaDAO.getPizzaById(itemReq.getPizzaId());
                if (pizza == null) {
                    resp.getWriter().write(gson.toJson(Result.error(400, "披萨不存在: " + itemReq.getPizzaId())));
                    return;
                }

                OrderItem item = new OrderItem();
                item.setPizzaId(itemReq.getPizzaId());
                item.setPizzaName(pizza.getName());
                item.setQuantity(itemReq.getQuantity());
                item.setUnitPrice(pizza.getBasePrice());
                item.setSubtotal(pizza.getBasePrice() * itemReq.getQuantity());
                items.add(item);
            }

            int orderId = orderDAO.createOrder(order, items);

            OrderCreateResponse response = new OrderCreateResponse();
            response.setOrderId(orderId);
            response.setStatus("pending");

            resp.getWriter().write(gson.toJson(Result.success(response)));

        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write(gson.toJson(Result.error(500, "创建订单失败: " + e.getMessage())));
        }
    }

    private void handleGetOrdersByCustomer(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String customerIdStr = req.getParameter("customer_id");

        if (customerIdStr == null || customerIdStr.isEmpty()) {
            resp.getWriter().write(gson.toJson(Result.error(400, "customer_id不能为空")));
            return;
        }

        try {
            int customerId = Integer.parseInt(customerIdStr);
            List<Order> orders = orderDAO.findByCustomerId(customerId);

            List<OrderResponse> response = new ArrayList<>();
            for (Order order : orders) {
                OrderResponse orderResp = new OrderResponse();
                orderResp.setOrderId(order.getOrderId());
                orderResp.setOrderNo(order.getOrderNo());
                orderResp.setOrderTime(order.getOrderTime());
                orderResp.setTotalAmount(order.getTotalAmount());
                orderResp.setStatus(order.getStatus());
                response.add(orderResp);
            }

            resp.getWriter().write(gson.toJson(Result.success(response)));
        } catch (NumberFormatException e) {
            resp.getWriter().write(gson.toJson(Result.error(400, "customer_id格式错误")));
        }
    }

    private void handleGetOrderDetail(HttpServletRequest req, HttpServletResponse resp, String orderIdStr) throws IOException {
        try {
            int orderId = Integer.parseInt(orderIdStr);
            Order order = orderDAO.findById(orderId);

            if (order == null) {
                resp.getWriter().write(gson.toJson(Result.error(404, "订单不存在")));
                return;
            }

            // 权限验证：只能查看自己的订单
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("userId") == null) {
                resp.getWriter().write(gson.toJson(Result.error(401, "请先登录")));
                return;
            }

            int userId = (int) session.getAttribute("userId");
            if (userId != order.getCustomerId()) {
                resp.getWriter().write(gson.toJson(Result.error(403, "无权查看该订单")));
                return;
            }

            resp.getWriter().write(gson.toJson(Result.success(order)));

        } catch (NumberFormatException e) {
            resp.getWriter().write(gson.toJson(Result.error(400, "订单ID格式错误")));
        }
    }

    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
}