package main.java.edu.group11.model;

import java.util.Date;

public class Customer {
    private int customerId;
    private String name;
    private String phone;
    private String password;
    private String address;
    private Date createTime;

    // 无参构造器
    public Customer() {}

    // 带参构造器
    public Customer(String name, String phone, String password, String address) {
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.address = address;
    }

    // Getters and Setters
    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
