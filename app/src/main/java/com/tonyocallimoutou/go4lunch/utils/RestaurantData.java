package com.tonyocallimoutou.go4lunch.utils;

import android.location.Location;
import android.util.Log;

import com.tonyocallimoutou.go4lunch.R;
import com.tonyocallimoutou.go4lunch.model.Places.RestaurantsResult;

import java.util.Random;

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
            double lat1 = userLocation.getLatitude();
            double lng1 = userLocation.getLongitude();
            double lat2 = result.getGeometry().getLocation().getLat();
            double lng2 = result.getGeometry().getLocation().getLng();
            float[] distanceFloat = new float[1];
            Location.distanceBetween(lat1,lng1,lat2,lng2,distanceFloat);

            int distance = Math.round(distanceFloat[0]);
            return distance + "m";
        }
        else {
            return null;
        }
    }

    public static String getRestaurantName() {
        return result.getName();
    }

    public static int getOpeningHour() {
        if (result.getOpeningHours() != null) {
            if (result.getOpeningHours().getOpenNow()) {
                return R.string.restaurant_data_open;
            }
            else {
                return R.string.restaurant_data_close;
            }
        }
        else {
            return R.string.restaurant_data_no_data;
        }
    }

    public static String getTypeAndAddress() {
        String type = result.getTypes().get(0);
        String address = result.getVicinity();

        return type + " - " + address;
    }

    public static String getNbrWorkmates() {
        if (result.getWorkmatesId().size() > 0) {
            return "(" + result.getWorkmatesId().size() + ")";
        }
        else {
            return null;
        }
    }

    public static int getRate() {
        Log.d("TAG", "getRate: " + result.getRating());
        Random ran = new Random();
        return ran.nextInt(4);
    }

    public static void getPicture() {

    }


}
