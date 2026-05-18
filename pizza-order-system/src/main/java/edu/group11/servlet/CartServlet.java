package main.java.edu.group11.servlet;

import com.google.gson.Gson;
import main.java.edu.group11.dto.response.Result;
import main.java.edu.group11.model.Pizza;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/cart/*")
public class CartServlet extends HttpServlet {
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.getWriter().write(gson.toJson(Result.error(401, "请先登录")));
            return;
        }

        // 获取购物车内容
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> cart = (List<Map<String, Object>>) session.getAttribute("cart");

        if (cart == null) {
            cart = new ArrayList<>();
        }

        resp.getWriter().write(gson.toJson(Result.success(cart)));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.getWriter().write(gson.toJson(Result.error(401, "请先登录")));
            return;
        }

        String pathInfo = req.getPathInfo();

        if ("/add".equals(pathInfo)) {
            handleAddToCart(req, resp, session);
        } else if ("/remove".equals(pathInfo)) {
            handleRemoveFromCart(req, resp, session);
        } else if ("/clear".equals(pathInfo)) {
            handleClearCart(req, resp, session);
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(gson.toJson(Result.error(404, "接口不存在")));
        }
    }

    @SuppressWarnings("unchecked")
    private void handleAddToCart(HttpServletRequest req, HttpServletResponse resp, HttpSession session) throws IOException {
        BufferedReader reader = req.getReader();
        Map<String, Object> cartItem = gson.fromJson(reader, Map.class);

        List<Map<String, Object>> cart = (List<Map<String, Object>>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
        }

        // 检查是否已存在相同商品
        int pizzaId = ((Double) cartItem.get("pizzaId")).intValue();
        boolean exists = false;
        for (Map<String, Object> item : cart) {
            if (item.get("pizzaId").equals(pizzaId)) {
                int newQuantity = ((Double) item.get("quantity")).intValue() + ((Double) cartItem.get("quantity")).intValue();
                item.put("quantity", newQuantity);
                exists = true;
                break;
            }
        }

        if (!exists) {
            cart.add(cartItem);
        }

        session.setAttribute("cart", cart);
        resp.getWriter().write(gson.toJson(Result.success(cart)));
    }

    @SuppressWarnings("unchecked")
    private void handleRemoveFromCart(HttpServletRequest req, HttpServletResponse resp, HttpSession session) throws IOException {
        String pizzaIdStr = req.getParameter("pizzaId");

        if (pizzaIdStr == null) {
            resp.getWriter().write(gson.toJson(Result.error(400, "pizzaId不能为空")));
            return;
        }

        int pizzaId = Integer.parseInt(pizzaIdStr);
        List<Map<String, Object>> cart = (List<Map<String, Object>>) session.getAttribute("cart");

        if (cart != null) {
            cart.removeIf(item -> item.get("pizzaId").equals(pizzaId));
            session.setAttribute("cart", cart);
        }

        resp.getWriter().write(gson.toJson(Result.success(cart)));
    }

    private void handleClearCart(HttpServletRequest req, HttpServletResponse resp, HttpSession session) throws IOException {
        session.removeAttribute("cart");
        resp.getWriter().write(gson.toJson(Result.success("购物车已清空")));
    }
}
