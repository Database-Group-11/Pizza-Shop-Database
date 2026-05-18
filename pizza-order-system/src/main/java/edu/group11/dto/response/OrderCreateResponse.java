package main.java.edu.group11.dto.response;

public class OrderCreateResponse {
    private int orderId;
    private String status;

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
