package com.wastely.models;

/**
 * Represents a logged-in user session.
 * Stores user information and authentication details.
 */
public class User {
    private String id;
    private String email;
    private String fullName;
    private String role;  // "MENRO_ADMIN" or "BARANGAY_ADMIN"
    private String barangay;
    
    public User(String id, String email, String fullName, String role, String barangay) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.barangay = barangay;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getBarangay() { return barangay; }
    public void setBarangay(String barangay) { this.barangay = barangay; }
    
    public boolean isMenroAdmin() {
        return "MENRO_ADMIN".equals(role);
    }
    
    public boolean isBarangayAdmin() {
        return "BARANGAY_ADMIN".equals(role);
    }
}
