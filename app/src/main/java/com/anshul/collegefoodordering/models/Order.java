// Order.java
package com.anshul.collegefoodordering.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order {
    private String id;
    private String studentId;
    private String vendorId;
    private List<com.anshul.collegefoodordering.models.MenuItem> items;
    private double totalAmount;
    private String status; // "pending", "accepted", "rejected", "prepared", "delivered", "cancelled"
    private Date orderTime;
    private Date responseTime;
    private Date deliveryTime;

    public Order() {
        // Required empty constructor for Firebase
        items = new ArrayList<>();
    }

    public Order(String id, String studentId, String vendorId, List<com.anshul.collegefoodordering.models.MenuItem> items, double totalAmount) {
        this.id = id;
        this.studentId = studentId;
        this.vendorId = vendorId;
        this.items = items;
        this.totalAmount = totalAmount;
        this.status = "pending";
        this.orderTime = new Date();
    } // Add closing brace here

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getVendorId() { return vendorId; }
    public void setVendorId(String vendorId) { this.vendorId = vendorId; }

    public List<com.anshul.collegefoodordering.models.MenuItem> getItems() { return items; }
    public void setItems(List<com.anshul.collegefoodordering.models.MenuItem> items) { this.items = items; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getOrderTime() { return orderTime; }
    public void setOrderTime(Date orderTime) { this.orderTime = orderTime; }

    public Date getResponseTime() { return responseTime; }
    public void setResponseTime(Date responseTime) { this.responseTime = responseTime; }

    public Date getDeliveryTime() { return deliveryTime; }
    public void setDeliveryTime(Date deliveryTime) { this.deliveryTime = deliveryTime; }
}
