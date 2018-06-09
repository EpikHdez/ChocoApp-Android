package com.example.ximena.nomnom.controller;

import com.example.ximena.nomnom.model.Product;
import com.example.ximena.nomnom.model.Restaurant;

import java.util.ArrayList;

class Manager {
    private Restaurant currentRestaurant;
    private Product currentProduct;
    private ArrayList<Restaurant> currentRestaurants;
    private ArrayList<Product> currentProducts;
    private float currentLatitud;
    private float currentLongitude;
    private String name;
    private String email;


    private static final Manager ourInstance = new Manager();

    static Manager getInstance() {
        return ourInstance;
    }

    private Manager() {
    }

    public Restaurant getCurrentRestaurant() {
        return currentRestaurant;
    }

    public void setCurrentRestaurant(Restaurant currentRestaurant) {
        this.currentRestaurant = currentRestaurant;
    }

    public Product getCurrentProduct() {
        return currentProduct;
    }

    public void setCurrentProduct(Product currentProduct) {
        this.currentProduct = currentProduct;
    }

    public ArrayList<Restaurant> getCurrentRestaurants() {
        return currentRestaurants;
    }

    public void setCurrentRestaurants(ArrayList<Restaurant> currentRestaurants) {
        this.currentRestaurants = currentRestaurants;
    }

    public ArrayList<Product> getCurrentProducts() {
        return currentProducts;
    }

    public void setCurrentProducts(ArrayList<Product> currentProducts) {
        this.currentProducts = currentProducts;
    }

    public float getCurrentLatitud() {
        return currentLatitud;
    }

    public void setCurrentLatitud(float currentLatitud) {
        this.currentLatitud = currentLatitud;
    }

    public float getCurrentLongitude() {
        return currentLongitude;
    }

    public void setCurrentLongitude(float currentLongitude) {
        this.currentLongitude = currentLongitude;
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
}
