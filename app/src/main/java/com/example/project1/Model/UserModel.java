package com.example.project1.Model;

public class UserModel {
    private int id;
    private String username;
    private String password;
    private byte[] image;
    private String name;
    private String email;
    private String phoneNumber;
    private boolean isAdmin;
    private boolean isActive;

    public UserModel() {
    }

    // Constructor không có ảnh (cho trường hợp chưa có ảnh)
    public UserModel(int id, String username, String password, String name, String email, String phoneNumber, boolean isAdmin, boolean isActive) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.isAdmin = isAdmin;
        this.isActive = isActive;
    }

    // Constructor có ảnh
    public UserModel(int id, String username, String password, byte[] image, String name, String email, String phoneNumber, boolean isAdmin, boolean isActive) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.image = image;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.isAdmin = isAdmin;
        this.isActive = isActive;
    }

    // Getters và setters cho các thuộc tính
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getActiveStatus() {
        return this.isActive ? "Còn hạn hợp đồng" : "Đã hết hạn hợp đồng";
    }

}
