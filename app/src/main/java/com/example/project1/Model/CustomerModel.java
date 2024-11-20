package com.example.project1.Model;

public class CustomerModel {
      private int id;
      private String name;
      private String sdt;

    public CustomerModel() {
    }

    public CustomerModel(int id, String name, String sdt) {
        this.id = id;
        this.name = name;
        this.sdt = sdt;
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

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }
}
