package com.erickhdez.chocoapp.model;

public class Picture {
    private int id;
    private String url;

    public Picture(int id, String url) {
        this.id = id;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }
}
