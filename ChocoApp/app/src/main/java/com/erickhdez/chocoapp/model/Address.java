package com.erickhdez.chocoapp.model;

public class Address {
    private int id;
    private String address;
    private String latitude;
    private String longitude;
    private AddressType type;

    public Address(int id, String address, String latitude, String longitude, AddressType type) {
        this.id = id;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public AddressType getType() {
        return type;
    }
}
