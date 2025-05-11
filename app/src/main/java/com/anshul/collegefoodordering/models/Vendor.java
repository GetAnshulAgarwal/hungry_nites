// Vendor.java
package com.anshul.collegefoodordering.models;

import java.util.ArrayList;
import java.util.List;

public class Vendor extends User {
    private String vendorId;
    private String description;
    private List<MenuItem> menuItems;

    public Vendor() {
        // Required empty constructor for Firebase
        menuItems = new ArrayList<>();
    }

    public Vendor(String uid, String email, String name, String vendorId, String description) {
        super(uid, email, name, "vendor");
        this.vendorId = vendorId;
        this.description = description;
        this.menuItems = new ArrayList<>();
    }

    // Getters and setters
    public String getVendorId() { return vendorId; }
    public void setVendorId(String vendorId) { this.vendorId = vendorId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<MenuItem> getMenuItems() { return menuItems; }
    public void setMenuItems(List<MenuItem> menuItems) { this.menuItems = menuItems; }

    public void addMenuItem(MenuItem item) {
        if (menuItems == null) {
            menuItems = new ArrayList<>();
        }
        menuItems.add(item);
    }
}
