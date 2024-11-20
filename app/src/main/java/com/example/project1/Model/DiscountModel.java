package com.example.project1.Model;

public class DiscountModel {
    private int id;
    private String name;
    private float discountPrice;
    private double minOrderPrice;
    private String startDate;
    private String endDate;

    public DiscountModel() {
    }

    public DiscountModel(int id, String name, float discountPrice, double minOrderPrice, String startDate, String endDate) {
        this.id = id;
        this.name = name;
        this.discountPrice = discountPrice;
        this.minOrderPrice = minOrderPrice;
        this.startDate = startDate;
        this.endDate = endDate;
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

    public double getMinOrderPrice() {
        return minOrderPrice;
    }

    public void setMinOrderPrice(double minOrderPrice) {
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
}
