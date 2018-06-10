package com.example.ximena.nomnom;

import android.content.Context;

import com.example.ximena.nomnom.model.Product;
import com.example.ximena.nomnom.model.Restaurant;

import java.util.ArrayList;

class ManagerUser {
    private Restaurant currentRestaurant;
    private Product currentProduct;
    private ArrayList<Restaurant> currentRestaurants;
    private ArrayList<Product> currentProducts;
    private float currentLatitud;
    private float currentLongitude;
    private float tempLatitud, tempLongitude;
    private String name, lastname;
    private String email;
    private String picture;
    private int idUser;
    private Context currentContext;
    private int flag_map;

    public float getTempLatitud() {
        return tempLatitud;
    }

    public void setTempLatitud(float tempLatitud) {
        this.tempLatitud = tempLatitud;
    }

    public float getTempLongitude() {
        return tempLongitude;
    }

    public void setTempLongitude(float tempLongitude) {
        this.tempLongitude = tempLongitude;
    }

    public int getFlag_map() {
        return flag_map;
    }

    public void setFlag_map(int flag_map) {
        this.flag_map = flag_map;
    }

    public Context getCurrentContext() {
        return currentContext;
    }

    public void setCurrentContext(Context currentContext) {
        this.currentContext = currentContext;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    private static final ManagerUser ourInstance = new ManagerUser();

    static ManagerUser getInstance() {
        return ourInstance;
    }

    private ManagerUser() {
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
