package com.example.project1.Model;

public class DiscountModel {
    private int id;
    private String name;
    private float discountPrice;
    private int minOrderPrice;
    private String startDate;
    private String endDate;
    private int quantity;
    private boolean isValid;

    public DiscountModel() {
    }

    public DiscountModel(int id, String name, float discountPrice, int minOrderPrice, String startDate, String endDate, int quantity, boolean isValid) {
        this.id = id;
        this.name = name;
        this.discountPrice = discountPrice;
        this.minOrderPrice = minOrderPrice;
        this.startDate = startDate;
        this.endDate = endDate;
        this.quantity = quantity;
        this.isValid = isValid;
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

    public float getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(float discountPrice) {
        this.discountPrice = discountPrice;
    }

    public int getMinOrderPrice() {
        return minOrderPrice;
    }

    public void setMinOrderPrice(int minOrderPrice) {
        this.minOrderPrice = minOrderPrice;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean isValid) {
        this.isValid = isValid;
    }

    public String getStatus() {
        return this.isValid ? "Còn hạn" : "Hết hạn";
    }
}
