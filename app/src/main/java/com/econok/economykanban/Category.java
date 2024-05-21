package com.econok.economykanban;

public class Category {
    private String name;

    public Category() {
        // Necesario para Firebase
    }

    public Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
