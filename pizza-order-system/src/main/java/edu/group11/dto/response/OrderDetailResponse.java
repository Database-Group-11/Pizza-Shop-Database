package main.java.edu.group11.dto.response;

import main.java.edu.group11.model.OrderItem;
import main.java.edu.group11.model.Payment;
import main.java.edu.group11.model.Delivery;

import java.util.Date;
import java.util.List;

public class OrderDetailResponse {
    private int orderId;
    private String orderNo;
    private int customerId;
    private String customerName;
    private String customerPhone;
    private String customerAddress;
    private Date orderTime;
    private double totalAmount;
    private String status;
    private String deliveryAddress;
    private String paymentMethod;
    private List<OrderItemResponse> items;
    private PaymentResponse payment;
    private DeliveryResponse delivery;

    // Nested classes
    public static class OrderItemResponse {
        private int pizzaId;
        private String pizzaName;
        private int quantity;
        private double unitPrice;
        private double subtotal;
        private List<ToppingItem> toppings;

        // Getters and Setters
        public int getPizzaId() { return pizzaId; }
        public void setPizzaId(int pizzaId) { this.pizzaId = pizzaId; }
        public String getPizzaName() { return pizzaName; }
        public void setPizzaName(String pizzaName) { this.pizzaName = pizzaName; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public double getUnitPrice() { return unitPrice; }
        public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
        public double getSubtotal() { return subtotal; }
        public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
        public List<ToppingItem> getToppings() { return toppings; }
        public void setToppings(List<ToppingItem> toppings) { this.toppings = toppings; }
    }

    public static class ToppingItem {
        private int toppingId;
        private String name;
        private double price;
        private int quantity;

        // Getters and Setters
        public int getToppingId() { return toppingId; }
        public void setToppingId(int toppingId) { this.toppingId = toppingId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }

    public static class PaymentResponse {
        private int paymentId;
        private String paymentMethod;
        private double amount;
        private String status;
        private Date paymentTime;

        // Getters and Setters
        public int getPaymentId() { return paymentId; }
        public void setPaymentId(int paymentId) { this.paymentId = paymentId; }
        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
        public double getAmount() { return amount; }
        public void setAmount(double amount) { this.amount = amount; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public Date getPaymentTime() { return paymentTime; }
        public void setPaymentTime(Date paymentTime) { this.paymentTime = paymentTime; }
    }

    public static class DeliveryResponse {
        private int deliveryId;
        private String riderName;
        private String riderPhone;
        private Date startTime;
        private Date arriveTime;
        private String status;
        private Date estimatedDeliveryTime;

        // Getters and Setters
        public int getDeliveryId() { return deliveryId; }
        public void setDeliveryId(int deliveryId) { this.deliveryId = deliveryId; }
        public String getRiderName() { return riderName; }
        public void setRiderName(String riderName) { this.riderName = riderName; }
        public String getRiderPhone() { return riderPhone; }
        public void setRiderPhone(String riderPhone) { this.riderPhone = riderPhone; }
        public Date getStartTime() { return startTime; }
        public void setStartTime(Date startTime) { this.startTime = startTime; }
        public Date getArriveTime() { return arriveTime; }
        public void setArriveTime(Date arriveTime) { this.arriveTime = arriveTime; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public Date getEstimatedDeliveryTime() { return estimatedDeliveryTime; }
        public void setEstimatedDeliveryTime(Date estimatedDeliveryTime) { this.estimatedDeliveryTime = estimatedDeliveryTime; }
    }

    // Getters and Setters for OrderDetailResponse
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    public String getCustomerAddress() { return customerAddress; }
    public void setCustomerAddress(String customerAddress) { this.customerAddress = customerAddress; }
    public Date getOrderTime() { return orderTime; }
    public void setOrderTime(Date orderTime) { this.orderTime = orderTime; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public List<OrderItemResponse> getItems() { return items; }
    public void setItems(List<OrderItemResponse> items) { this.items = items; }
    public PaymentResponse getPayment() { return payment; }
    public void setPayment(PaymentResponse payment) { this.payment = payment; }
    public DeliveryResponse getDelivery() { return delivery; }
    public void setDelivery(DeliveryResponse delivery) { this.delivery = delivery; }
}
