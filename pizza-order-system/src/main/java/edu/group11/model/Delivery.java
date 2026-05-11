package main.java.edu.group11.model;

import java.util.Date;

public class Delivery {
    private int deliveryId;
    private int orderId;
    private String riderName;
    private Date startTime;
    private Date arriveTime;
    private String status; // pending, picked_up, delivering, delivered

    // Constructors

    public Delivery(int orderId, String riderName) {
        this.orderId = orderId;
        this.riderName = riderName;
        this.status = "pending";
    }

    // Getters and Setters
    public int getDeliveryId() { return deliveryId; }
    public void setDeliveryId(int deliveryId) { this.deliveryId = deliveryId; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public String getRiderName() { return riderName; }
    public void setRiderName(String riderName) { this.riderName = riderName; }

    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }

    public Date getArriveTime() { return arriveTime; }
    public void setArriveTime(Date arriveTime) { this.arriveTime = arriveTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
