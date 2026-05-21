package main.java.edu.group11.servlet;

import com.google.gson.Gson;
import main.java.edu.group11.dao.CustomerDAO;
import main.java.edu.group11.dto.request.CustomerLoginRequest;
import main.java.edu.group11.dto.request.CustomerRegisterRequest;
import main.java.edu.group11.dto.response.CustomerLoginResponse;
import main.java.edu.group11.dto.response.CustomerRegisterResponse;
import main.java.edu.group11.dto.response.Result;
import main.java.edu.group11.model.Customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/api/customer/*")
public class CustomerServlet extends HttpServlet {
    private CustomerDAO customerDAO = new CustomerDAO();
    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json;charset=UTF-8");

        if ("/login".equals(pathInfo)) {
            handleLogin(req, resp);
        } else if ("/register".equals(pathInfo)) {
            handleRegister(req, resp);
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(gson.toJson(Result.error(404, "接口不存在")));
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:8080");
        resp.setHeader("Access-Control-Allow-Credentials", "true");

        BufferedReader reader = req.getReader();
        CustomerLoginRequest loginReq = gson.fromJson(reader, CustomerLoginRequest.class);

        if (loginReq.getPhone() == null || loginReq.getPassword() == null) {
            resp.getWriter().write(gson.toJson(Result.error(400, "手机号和密码不能为空")));
            return;
        }

        Customer customer = customerDAO.login(loginReq.getPhone(), loginReq.getPassword());

        if (customer != null) {
            HttpSession session = req.getSession();
            session.setAttribute("userId", customer.getCustomerId());

            System.out.println("Session ID: " + session.getId());

            session.setAttribute("userName", customer.getName());

            CustomerLoginResponse response = new CustomerLoginResponse();
            response.setCustomerId(customer.getCustomerId());
            response.setName(customer.getName());
            response.setPhone(customer.getPhone());
            response.setAddress(customer.getAddress());

            resp.getWriter().write(gson.toJson(Result.success(response)));
        } else {
            resp.getWriter().write(gson.toJson(Result.error(401, "手机号或密码错误")));
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BufferedReader reader = req.getReader();
        CustomerRegisterRequest registerReq = gson.fromJson(reader, CustomerRegisterRequest.class);

        // 验证必填字段
        if (registerReq.getName() == null || registerReq.getPhone() == null ||
                registerReq.getPassword() == null || registerReq.getAddress() == null) {
            resp.getWriter().write(gson.toJson(Result.error(400, "所有字段都不能为空")));
            return;
        }

        // 检查手机号是否已存在
        if (customerDAO.findByPhone(registerReq.getPhone()) != null) {
            resp.getWriter().write(gson.toJson(Result.error(409, "手机号已注册")));
            return;
        }

        Customer customer = new Customer();
        customer.setName(registerReq.getName());
        customer.setPhone(registerReq.getPhone());
        customer.setPassword(registerReq.getPassword());
        customer.setAddress(registerReq.getAddress());

        int customerId = customerDAO.register(customer);

        if (customerId > 0) {
            resp.getWriter().write(gson.toJson(Result.success(new CustomerRegisterResponse(customerId))));
        } else {
            resp.getWriter().write(gson.toJson(Result.error(500, "注册失败，请稍后重试")));
        }
    }
}
