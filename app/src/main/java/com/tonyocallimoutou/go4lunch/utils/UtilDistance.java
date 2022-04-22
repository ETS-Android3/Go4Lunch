package com.tonyocallimoutou.go4lunch.utils;

import android.location.Location;

import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;

public class UtilDistance {

    public static void roundToNearestFiftyMeters(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        double newLat = Math.round(lat*1000.0)/1000.0;
        double newLng = Math.round(lng*1000.0)/1000.0;

        location.setLatitude(newLat);
        location.setLongitude(newLng);
    }

    public static int getDistanceWithRestaurant(Location userLocation, RestaurantDetails restaurant ) {

        Location restaurantLocation = new Location("");
        restaurantLocation.setLatitude(restaurant.getGeometry().getLocation().getLat());
        restaurantLocation.setLongitude(restaurant.getGeometry().getLocation().getLng());

        float distanceFloat = restaurantLocation.distanceTo(userLocation);

        return Math.round(distanceFloat);
    }
}
