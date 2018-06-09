package com.example.ximena.nomnom.controller;

class Manager {
    private static final Manager ourInstance = new Manager();

    static Manager getInstance() {
        return ourInstance;
    }

    private Manager() {
    }
}
