package main.java.edu.group11.model;

public class Pizza {
    private int pizzaId;
    private String name;
    private String description;
    private double basePrice;
    private String category;
    private String image;
    private boolean available;

    // Constructors
    public Pizza() {}

    public Pizza(String name, String description, double basePrice, String category, String image) {
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
        this.category = category;
        this.image = image;
        this.available = true;
    }

    // Getters and Setters
    public int getPizzaId() { return pizzaId; }
    public void setPizzaId(int pizzaId) { this.pizzaId = pizzaId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}