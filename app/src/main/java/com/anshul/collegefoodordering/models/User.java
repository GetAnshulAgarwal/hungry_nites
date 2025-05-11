// User.java
package com.anshul.collegefoodordering.models;

public class User {
    private String uid;
    private String email;
    private String name;
    private String userType; // "student" or "vendor"

    public User() {
        // Required empty constructor for Firebase
    }

    public User(String uid, String email, String name, String userType) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.userType = userType;
    }

    // Getters and setters
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
}
