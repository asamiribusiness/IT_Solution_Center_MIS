package com.itsolutioncenter.model;

import java.util.Date;

public class User {
    private int userId;
    private String username;
    private String passwordHash;
    private String email;
    private String fullName;
    private String role;
    private String phone;
    private String address;
    private Date hireDate;
    private double salary;
    private boolean isActive;
    private Date createdAt;
   // private List<String> permissions;
    // Constructors
    public User() {
    }
        public User(int userId, String username, String fullName, String role, boolean isActive) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
        this.isActive = isActive;
    }
    public User(int userId,String username, String passwordHash, String fullName, String role) {
        this.userId=userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.role = role;
        this.isActive = true;
        this.createdAt = new Date();
    }
    public User(String username, String passwordHash,String email, String fullName, String role, String phone, String address,Date hireDate, double salary,boolean isActive)
    {
        this.username = username;
        this.passwordHash=passwordHash;
        this.email=email;
        this.fullName = fullName;
        this.role = role;
        this.phone=phone;
        this.address=address;
        this.hireDate=hireDate;
        this.salary=salary;
        this.isActive = true;
        this.createdAt = new Date();
    }
    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getHireDate() {
        return hireDate;
    }

    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return fullName + " (" + username + ") - " + role;
    }
}