package main.java.edu.group11.dto.request;

import java.util.List;

public class OrderItemRequest {
    private int pizzaId;
    private int quantity;
    private List<Integer> toppingIds;

    public int getPizzaId() { return pizzaId; }
    public void setPizzaId(int pizzaId) { this.pizzaId = pizzaId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public List<Integer> getToppingIds() { return toppingIds; }
    public void setToppingIds(List<Integer> toppingIds) { this.toppingIds = toppingIds; }
}
