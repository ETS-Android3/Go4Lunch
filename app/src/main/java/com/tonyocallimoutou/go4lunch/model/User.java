package com.tonyocallimoutou.go4lunch.model;

import androidx.annotation.Nullable;

import com.tonyocallimoutou.go4lunch.model.Places.RestaurantsResult;

public class User {

    private String uid;
    private String username;
    @Nullable
    private String urlPicture;
    @Nullable
    private RestaurantsResult bookedRestaurant;

    public User() {}

    public User(User user) {
        uid = user.getUid();
        username = user.getUsername();
        urlPicture = user.getUrlPicture();
        bookedRestaurant = user.getBookedRestaurant();
    }

    public User(String uid, String username, @Nullable String urlPicture) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.bookedRestaurant = null;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Nullable
    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(@Nullable String urlPicture) {
        this.urlPicture = urlPicture;
    }

    @Nullable
    public RestaurantsResult getBookedRestaurant() {
        return bookedRestaurant;
    }

    public void setBookedRestaurant(RestaurantsResult bookedRestaurantId) {
        this.bookedRestaurant = bookedRestaurantId;
    }
}
