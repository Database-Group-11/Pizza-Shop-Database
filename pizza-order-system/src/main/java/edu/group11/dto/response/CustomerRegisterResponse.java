package main.java.edu.group11.dto.response;

public class CustomerRegisterResponse {
    private int customerId;

    public CustomerRegisterResponse(int customerId) {
        this.customerId = customerId;
    }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
}
