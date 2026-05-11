package main.java.edu.group11.model;

import java.util.Date;

public class Payment {
    private int paymentId;
    private int orderId;
    private String method;
    private String status; // pending, completed, failed
    private double amount;
    private Date paymentTime;

    // Constructors
    public Payment() {}

    public Payment(int orderId, String method, double amount) {
        this.orderId = orderId;
        this.method = method;
        this.amount = amount;
        this.status = "pending";
    }

    // Getters and Setters
    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public Date getPaymentTime() { return paymentTime; }
    public void setPaymentTime(Date paymentTime) { this.paymentTime = paymentTime; }
}