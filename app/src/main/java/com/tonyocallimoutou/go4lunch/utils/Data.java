package com.tonyocallimoutou.go4lunch.utils;

import android.util.Log;

import com.tonyocallimoutou.go4lunch.model.Places.NearbyPlace;
import com.tonyocallimoutou.go4lunch.model.Places.RestaurantsResult;
import com.tonyocallimoutou.go4lunch.model.User;

import java.util.List;

public class Data {

    private static NearbyPlace nearbyPlace;
    private static List<RestaurantsResult> bookedRestaurant;
    private static List<User> workmates;

    private Data() {
    }

    public static Data newInstanceOfNearbyPlace(NearbyPlace data) {
        nearbyPlace = data;
        return new Data();
    }

    public static Data newInstanceOfBookedRestaurant(List<RestaurantsResult> restaurants) {
        bookedRestaurant = restaurants;
        return new Data();
    }
    public static Data newInstanceOfWorkmates(List<User> users) {
        workmates = users;
        return new Data();
    }

    public static NearbyPlace getNearbyPlace () {
        return nearbyPlace;
    }

    public static List<RestaurantsResult> getBookedRestaurant() {
        return bookedRestaurant;
    }

    public static List<User> getWorkmates() {
        return workmates;
    }
}
