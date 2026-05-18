package main.java.edu.group11.servlet;

import com.google.gson.Gson;
import main.java.edu.group11.dao.OrderDAO;
import main.java.edu.group11.dao.PaymentDAO;
import main.java.edu.group11.dto.request.PaymentRequest;
import main.java.edu.group11.dto.response.PaymentResponse;
import main.java.edu.group11.dto.response.Result;
import main.java.edu.group11.model.Order;
import main.java.edu.group11.model.Payment;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.UUID;

@WebServlet("/api/payments")
public class PaymentServlet extends HttpServlet {
    private PaymentDAO paymentDAO = new PaymentDAO();
    private OrderDAO orderDAO = new OrderDAO();
    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");

        // 验证登录
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.getWriter().write(gson.toJson(Result.error(401, "请先登录")));
            return;
        }

        BufferedReader reader = req.getReader();
        PaymentRequest paymentReq = gson.fromJson(reader, PaymentRequest.class);

        if (paymentReq.getOrderId() <= 0) {
            resp.getWriter().write(gson.toJson(Result.error(400, "订单ID无效")));
            return;
        }

        // 验证订单是否存在且属于当前用户
        Order order = orderDAO.findById(paymentReq.getOrderId());
        if (order == null) {
            resp.getWriter().write(gson.toJson(Result.error(404, "订单不存在")));
            return;
        }

        int userId = (int) session.getAttribute("userId");
        if (order.getCustomerId() != userId) {
            resp.getWriter().write(gson.toJson(Result.error(403, "无权支付该订单")));
            return;
        }

        // 检查订单状态
        if (!"pending".equals(order.getStatus())) {
            resp.getWriter().write(gson.toJson(Result.error(400, "订单状态不允许支付")));
            return;
        }

        // 创建支付记录
        Payment payment = new Payment();
        payment.setOrderId(paymentReq.getOrderId());
        payment.setPaymentMethod(paymentReq.getMethod());
        payment.setAmount(order.getTotalAmount());
        payment.setTransactionId(generateTransactionId());

        int paymentId = paymentDAO.createPayment(payment);

        if (paymentId > 0) {
            // 更新订单状态
            orderDAO.updateStatus(paymentReq.getOrderId(), "paid");

            PaymentResponse response = new PaymentResponse();
            response.setPaymentId(paymentId);
            response.setStatus("success");

            resp.getWriter().write(gson.toJson(Result.success(response)));
        } else {
            resp.getWriter().write(gson.toJson(Result.error(500, "支付失败，请稍后重试")));
        }
    }

    private String generateTransactionId() {
        return "TXN" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
