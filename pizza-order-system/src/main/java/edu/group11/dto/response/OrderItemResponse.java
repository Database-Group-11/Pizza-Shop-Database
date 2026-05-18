package main.java.edu.group11.dto.response;

public class OrderItemResponse {
    private int pizzaId;
    private String pizzaName;
    private int quantity;
    private double unitPrice;
    private double subtotal;

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
}
