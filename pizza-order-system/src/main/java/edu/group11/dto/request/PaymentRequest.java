package main.java.edu.group11.dto.request;

public class PaymentRequest {
    private int orderId;
    private String method;

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
}
