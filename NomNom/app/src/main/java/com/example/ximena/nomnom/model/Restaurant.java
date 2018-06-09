package com.example.ximena.nomnom.model;

import java.util.HashMap;

public class Restaurant {
    private int id;
    private String name;
    private float latitude;
    private float longitude;
    private String type;
    private HashMap<String,String> pictures;

    public Restaurant(int id, String name, float latitude, float longitude, String type, HashMap<String, String> pictures) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
        this.pictures = pictures;
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

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public HashMap<String, String> getPictures() {
        return pictures;
    }

    public void setPictures(HashMap<String, String> pictures) {
        this.pictures = pictures;
    }
}
