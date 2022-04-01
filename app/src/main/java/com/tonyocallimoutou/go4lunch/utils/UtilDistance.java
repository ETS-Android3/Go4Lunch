package com.tonyocallimoutou.go4lunch.utils;

import android.location.Location;

public class UtilDistance {

    public static int getDistanceBetweenTwoLocation(Location a, Location b) {

        double lat1 = a.getLatitude();
        double lng1 = a.getLongitude();
        double lat2 = b.getLatitude();
        double lng2 = b.getLongitude();
        float[] distanceFloat = new float[1];
        Location.distanceBetween(lat1,lng1,lat2,lng2,distanceFloat);

        return Math.round(distanceFloat[0]);
    }

    public static void roundToThousandths(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        double newLat = Math.round(lat*10000.0)/10000.0;
        double newLng = Math.round(lng*10000.0)/10000.0;

        location.setLatitude(newLat);
        location.setLongitude(newLng);
    }
}
