package com.example.project1.Model;

public class ProductModel {
    private int id;
    private String name;
    private double discountPrice;
    private String productType;
    private String productBrand;

    public ProductModel() {
    }

    // Constructor, getters, and setters
    public ProductModel(int id, String name, double discountPrice, String productType, String productBrand) {
        this.id = id;
        this.name = name;
        this.discountPrice = discountPrice;
        this.productType = productType;
        this.productBrand = productBrand;
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

    public double getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(double discountPrice) {
        this.discountPrice = discountPrice;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getProductBrand() {
        return productBrand;
    }

    public void setProductBrand(String productBrand) {
        this.productBrand = productBrand;
    }
}
