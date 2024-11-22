package com.example.project1.Model;

public class CustomerModel {
    private int id;
    private String name;
    private String phoneNumber;
    private boolean isVIP;

    public CustomerModel() {
    }

    public CustomerModel(int id, String name, String phoneNumber, boolean isVIP) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.isVIP = isVIP;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isVIP() {
        return isVIP;
    }

    public void setVIP(boolean VIP) {
        isVIP = VIP;
    }

    public String getStatus() {
        return this.isVIP ? "VIP" : "Phổ thông";
    }
}