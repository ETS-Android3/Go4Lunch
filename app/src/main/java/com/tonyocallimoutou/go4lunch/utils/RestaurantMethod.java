package com.tonyocallimoutou.go4lunch.utils;

import com.tonyocallimoutou.go4lunch.model.Places.RestaurantsResult;

import java.util.ArrayList;
import java.util.List;

public class RestaurantMethod {

    public static List<RestaurantsResult> getNearbyRestaurantWithoutBooked(List<RestaurantsResult> nearbyRestaurant, List<RestaurantsResult> bookedRestaurant) {
        if (bookedRestaurant != null) {
            List<RestaurantsResult> sup =  new ArrayList<>();
            for (RestaurantsResult nearby : nearbyRestaurant) {
                for (RestaurantsResult booked : bookedRestaurant) {
                    if (nearby.getPlaceId().equals(booked.getPlaceId())) {
                        sup.add(nearby);
                    }
                }
            }
            nearbyRestaurant.removeAll(sup);
        }
        return nearbyRestaurant;
    }

}
