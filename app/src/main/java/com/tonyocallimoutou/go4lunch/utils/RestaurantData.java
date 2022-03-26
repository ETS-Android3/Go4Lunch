package com.tonyocallimoutou.go4lunch.utils;

import android.os.Bundle;
import android.util.Log;

import com.tonyocallimoutou.go4lunch.model.Places.RestaurantsResult;

public class RestaurantData {

    private static RestaurantsResult result;

    private RestaurantData(RestaurantsResult restaurant) {
        result =restaurant;
    }


    public static RestaurantData newInstance(RestaurantsResult restaurant) {
        return new RestaurantData(restaurant);
    }

    public static String getRestaurantName() {
        return result.getName();
    }

    public static String getDistance() {
        return "0 m";
    }

    public static String getOpeningHour() {
        try {
            if (result.getOpeningHours().getOpenNow()) {
                Log.d("TAG", "hour: " + result.getOpeningHours().getWeekdayText());
            }
        }
        catch (Exception e) {
            Log.e("TAG", "getOpeningHour: " + e );
        }
        return "Ferme";
    }

    public static String getTypeAndAddress() {
        String type = result.getTypes().get(0);
        String address = result.getVicinity();

        return type + " - " + address;
    }


}
