package com.erickhdez.chocoapp.model;

public class PlaceType {
    private int id;
    private String name;

    public PlaceType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
