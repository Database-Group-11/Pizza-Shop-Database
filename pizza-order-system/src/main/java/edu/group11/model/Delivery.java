package main.java.edu.group11.model;

import java.util.Date;

public class Delivery {
    private int deliveryId;
    private int orderId;
    private String riderName;
    private String riderPhone;
    private Date startTime;
    private Date arriveTime;
    private String status;  // preparing, delivering, delivered, cancelled
    private Date estimatedDeliveryTime;
    private Date createTime;
    private Date updateTime;

    // 构造方法
    public Delivery() {}

    public Delivery(int orderId, String riderName, String riderPhone) {
        this.orderId = orderId;
        this.riderName = riderName;
        this.riderPhone = riderPhone;
        this.status = "preparing";
    }

    // Getters and Setters
    public int getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(int deliveryId) {
        this.deliveryId = deliveryId;
    }

    public int getOrderId() {return orderId;}
    public void setOrderId(int orderId) {this.orderId = orderId;}

    public String getRiderName() {return riderName;}
    public void setRiderName(String riderName) {this.riderName = riderName;}

    public String getRiderPhone() {return riderPhone;}
    public void setRiderPhone(String riderPhone) {this.riderPhone = riderPhone;}

    public Date getStartTime() {return startTime;}
    public void setStartTime(Date startTime) {this.startTime = startTime;}

    public Date getArriveTime() {return arriveTime;}
    public void setArriveTime(Date arriveTime) {this.arriveTime = arriveTime;}

    public String getStatus() {return status;}
    public void setStatus(String status) {this.status = status;}

    public Date getEstimatedDeliveryTime() {return estimatedDeliveryTime;}
    public void setEstimatedDeliveryTime(Date estimatedDeliveryTime) {this.estimatedDeliveryTime = estimatedDeliveryTime;}

    public Date getCreateTime() {return createTime;}
    public void setCreateTime(Date createTime) {this.createTime = createTime;}

    public Date getUpdateTime() {return updateTime;}
    public void setUpdateTime(Date updateTime) {this.updateTime = updateTime;}

    // 辅助方法
    public boolean isDelivered() {
        return "delivered".equals(status);
    }

    public boolean isDelivering() {
        return "delivering".equals(status);
    }

    public boolean isPreparing() {
        return "preparing".equals(status);
    }

    @Override
    public String toString() {
        return "Delivery{" +
                "deliveryId=" + deliveryId +
                ", orderId=" + orderId +
                ", riderName='" + riderName + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
