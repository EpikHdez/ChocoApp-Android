package com.erickhdez.chocoapp.model;

import java.util.LinkedList;
import java.util.List;

public final class LocalUser extends User {
    private static LocalUser INSTANCE = null;

    private List<Address> addresses;
    private List<Place> favoritePlaces;
    private UserType userType;

    private LocalUser() {
        super();
        favoritePlaces = new LinkedList<>();
        addresses = new LinkedList<>();
    }

    public static LocalUser getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LocalUser();
        }

        return INSTANCE;
    }

    public List<Place> getFavoritePlaces() {
        return favoritePlaces;
    }

    public void addFavoritePlace(Place place) {
        favoritePlaces.add(place);
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void addAddress(Address address) {
        addresses.add(address);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }
}
