package com.tonyocallimoutou.go4lunch.utils;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.tonyocallimoutou.go4lunch.model.Places.RestaurantsResult;

public class RestaurantData {

    private static RestaurantsResult result;
    private static Location userLocation;

    private RestaurantData(RestaurantsResult restaurant) {
        result = restaurant;
    }


    public static RestaurantData newInstance(RestaurantsResult restaurant) {
        return new RestaurantData(restaurant);
    }
    public static void newInstanceOfPosition(Location location) {
        userLocation = location;
    }


    // Get String

    public static String getDistance() {
        if (userLocation != null) {
            double distance = 1;
            return distance + "m";
        }
        else {
            return "Aucune position";
        }
    }

    public static String getRestaurantName() {
        return result.getName();
    }

    public static String getOpeningHour() {
        if (result.getOpeningHours() != null) {
            if (result.getOpeningHours().getOpenNow()) {
                return "Ouvert";
            }
            else {
                return "Fermé";
            }
        }
        else {
            return "Aucune infos";
        }
    }

    public static String getTypeAndAddress() {
        String type = result.getTypes().get(0);
        String address = result.getVicinity();

        return type + " - " + address;
    }

    public static String getNbrWorkmates() {
        if (result.getWorkmates().size() > 0) {
            return "(" + result.getWorkmates().size() + ")";
        }
        else {
            return null;
        }
    }


}
