package com.erickhdez.chocoapp.model;

import java.util.LinkedList;
import java.util.List;

public final class LocalUser extends User {
    private static LocalUser INSTANCE = null;

    private List<Place> favoritePlaces;

    private LocalUser() {
        super();
        favoritePlaces = new LinkedList<>();
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
}
