package main.java.edu.group11.dto.response;

import java.util.Date;

public class DeliveryResponse {
    private int deliveryId;
    private int orderId;
    private String riderName;
    private String riderPhone;
    private Date startTime;
    private Date arriveTime;
    private String status;
    private Date estimatedDeliveryTime;

    public DeliveryResponse() {}

    public DeliveryResponse(int deliveryId, int orderId, String riderName, String riderPhone,
                            Date startTime, Date arriveTime, String status, Date estimatedDeliveryTime) {
        this.deliveryId = deliveryId;
        this.orderId = orderId;
        this.riderName = riderName;
        this.riderPhone = riderPhone;
        this.startTime = startTime;
        this.arriveTime = arriveTime;
        this.status = status;
        this.estimatedDeliveryTime = estimatedDeliveryTime;
    }

    public int getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(int deliveryId) {
        this.deliveryId = deliveryId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getRiderName() {
        return riderName;
    }

    public void setRiderName(String riderName) {
        this.riderName = riderName;
    }

    public String getRiderPhone() {
        return riderPhone;
    }

    public void setRiderPhone(String riderPhone) {
        this.riderPhone = riderPhone;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getArriveTime() {
        return arriveTime;
    }

    public void setArriveTime(Date arriveTime) {
        this.arriveTime = arriveTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getEstimatedDeliveryTime() {
        return estimatedDeliveryTime;
    }

    public void setEstimatedDeliveryTime(Date estimatedDeliveryTime) {
        this.estimatedDeliveryTime = estimatedDeliveryTime;
    }

    // 辅助方法
    public String getStatusText() {
        switch (status) {
            case "preparing": return "准备中";
            case "delivering": return "配送中";
            case "delivered": return "已送达";
            case "cancelled": return "已取消";
            default: return "未知";
        }
    }
}
