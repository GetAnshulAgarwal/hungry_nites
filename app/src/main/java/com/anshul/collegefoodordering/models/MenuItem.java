// MenuItem.java
package com.anshul.collegefoodordering.models;

public class MenuItem {
    private String id;
    private String vendorId;
    private String name;
    private String description;
    private double price;
    private boolean available;

    public MenuItem() {
        // Required empty constructor for Firebase
    } // Missing closing brace

    public MenuItem(String id, String vendorId, String name, String description, double price, boolean available) {
        this.id = id;
        this.vendorId = vendorId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.available = available;
    } // Missing closing brace

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getVendorId() { return vendorId; }
    public void setVendorId(String vendorId) { this.vendorId = vendorId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}
