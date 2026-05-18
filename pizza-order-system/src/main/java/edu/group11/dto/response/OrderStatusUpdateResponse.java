package main.java.edu.group11.dto.response;

public class OrderStatusUpdateResponse {
    private int orderId;
    private String status;
    private String message;

    public OrderStatusUpdateResponse() {}

    public OrderStatusUpdateResponse(int orderId, String status, String message) {
        this.orderId = orderId;
        this.status = status;
        this.message = message;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
