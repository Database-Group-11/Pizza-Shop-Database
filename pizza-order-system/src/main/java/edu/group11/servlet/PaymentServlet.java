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

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.getWriter().write(gson.toJson(Result.error(401, "Please login")));
            return;
        }

        BufferedReader reader = req.getReader();
        PaymentRequest paymentReq = gson.fromJson(reader, PaymentRequest.class);

        if (paymentReq.getOrderId() <= 0) {
            resp.getWriter().write(gson.toJson(Result.error(400, "Invalid order ID")));
            return;
        }

        Order order = orderDAO.findById(paymentReq.getOrderId());
        if (order == null) {
            resp.getWriter().write(gson.toJson(Result.error(404, "Order doesn't exist")));
            return;
        }

        int userId = (int) session.getAttribute("userId");
        if (order.getCustomerId() != userId) {
            resp.getWriter().write(gson.toJson(Result.error(403, "You have no right to pay for this order")));
            return;
        }

        if (!"pending".equals(order.getStatus())) {
            resp.getWriter().write(gson.toJson(Result.error(400, "This order status doesn't allow you to pay")));
            return;
        }

        Payment payment = new Payment();
        payment.setOrderId(paymentReq.getOrderId());
        payment.setPaymentMethod(paymentReq.getMethod());
        payment.setAmount(order.getTotalAmount());
        payment.setTransactionId(generateTransactionId());

        int paymentId = paymentDAO.createPayment(payment);

        if (paymentId > 0) {
            orderDAO.updateStatus(paymentReq.getOrderId(), "paid");

            PaymentResponse response = new PaymentResponse();
            response.setPaymentId(paymentId);
            response.setStatus("success");

            resp.getWriter().write(gson.toJson(Result.success(response)));
        } else {
            resp.getWriter().write(gson.toJson(Result.error(500, "Failed to pay for order, please try again")));
        }
    }

    private String generateTransactionId() {
        return "TXN" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
