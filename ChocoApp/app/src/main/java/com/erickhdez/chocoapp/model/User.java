package com.erickhdez.chocoapp.model;

public class User {
    protected int id;
    protected String name;
    protected String lastName;
    protected String email;
    protected Picture picture;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public Picture getPicture() {
        return picture;
    }
}
