package main.java.edu.group11.dto.request;

public class ToppingUpdateRequest {
    private int toppingId;
    private String name;
    private double price;
    private int stockQuantity;
    private boolean available;

    public ToppingUpdateRequest() {}

    public ToppingUpdateRequest(int toppingId, String name, double price, int stockQuantity, boolean available) {
        this.toppingId = toppingId;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.available = available;
    }

    public int getToppingId() {
        return toppingId;
    }

    public void setToppingId(int toppingId) {
        this.toppingId = toppingId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "ToppingUpdateRequest{" +
                "toppingId=" + toppingId +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", stockQuantity=" + stockQuantity +
                ", available=" + available +
                '}';
    }
}
