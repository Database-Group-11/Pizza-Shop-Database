package main.java.edu.group11.dto.response;

public class PizzaResponse {
    private int pizzaId;
    private String name;
    private String description;
    private double basePrice;
    private String category;
    private String image;
    private boolean available;
    private int stockQuantity;

    public PizzaResponse() {}

    public PizzaResponse(int pizzaId, String name, String description, double basePrice,
                         String category, String image, boolean available, int stockQuantity) {
        this.pizzaId = pizzaId;
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
        this.category = category;
        this.image = image;
        this.available = available;
        this.stockQuantity = stockQuantity;
    }

    public int getPizzaId() {
        return pizzaId;
    }

    public void setPizzaId(int pizzaId) {
        this.pizzaId = pizzaId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}
