package com.erickhdez.chocoapp.model;

public class Product {
    private int id;
    private String name;
    private String description;
    private float price;
    private Picture picture;

    public Product(int id, String name, String description, float price, Picture picture) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.picture = picture;
    }
}
