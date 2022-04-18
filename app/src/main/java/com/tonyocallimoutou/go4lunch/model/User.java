package com.tonyocallimoutou.go4lunch.model;

import androidx.annotation.Nullable;

import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {

    private String uid;
    private String username;
    @Nullable
    private String urlPicture;
    private String email;
    @Nullable
    private RestaurantDetails bookedRestaurant;
    private List<String> likeRestaurantId;

    public User() {}

    public User(User user) {
        uid = user.getUid();
        username = user.getUsername();
        urlPicture = user.getUrlPicture();
        email = user.getEmail();
        bookedRestaurant = user.getBookedRestaurant();
        likeRestaurantId = user.getLikeRestaurantId();
    }

    public User(String uid, String username, @Nullable String urlPicture, String email) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.email = email;
        this.bookedRestaurant = null;
        this.likeRestaurantId = new ArrayList<>();  
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Nullable
    public RestaurantDetails getBookedRestaurant() {
        return bookedRestaurant;
    }

    public void setBookedRestaurant(RestaurantDetails bookedRestaurantId) {
        this.bookedRestaurant = bookedRestaurantId;
    }

    public List<String> getLikeRestaurantId() {
        return likeRestaurantId;
    }

    public void setLikeRestaurantId(List<String> likeRestaurantId) {
        this.likeRestaurantId = likeRestaurantId;
    }
}
