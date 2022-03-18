package com.tonyocallimoutou.go4lunch.model;

import com.mapbox.search.result.SearchAddress;

import java.util.List;

public class Restaurant {

    private String id;
    private String name;
    private double distanceMeters;
    private List<String> categories;
    private SearchAddress address;

    public Restaurant(){}

    public Restaurant(String id, String name, double distanceMeters, List<String> categories, SearchAddress address) {
        this.id = id;
        this.name = name;
        this.distanceMeters = distanceMeters;
        this.categories = categories;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDistanceMeters() {
        return distanceMeters;
    }

    public void setDistanceMeters(double distanceMeters) {
        this.distanceMeters = distanceMeters;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public SearchAddress getAddress() {
        return address;
    }

    public void setAddress(SearchAddress address) {
        this.address = address;
    }


    public String getStringDistance() {
        return Math.round(distanceMeters) + "m";
    }

    public String getStringAddress() {
        String strCategories = categories.get(0);
        String strAddress = address.formattedAddress();

        return strCategories + " - " + strAddress;
    }
}
