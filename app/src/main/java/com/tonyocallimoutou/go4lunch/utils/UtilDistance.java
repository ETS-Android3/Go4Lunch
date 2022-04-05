package com.tonyocallimoutou.go4lunch.utils;

import android.location.Location;

public class UtilDistance {

    public static void roundToNearestFiftyMeters(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        double newLat = Math.round(lat*1000.0)/1000.0;
        double newLng = Math.round(lng*1000.0)/1000.0;

        location.setLatitude(newLat);
        location.setLongitude(newLng);
    }
}
