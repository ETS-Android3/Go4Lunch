package com.tonyocallimoutou.go4lunch.utils;

import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;

import java.util.ArrayList;
import java.util.List;

public class RestaurantMethod {

    public static List<RestaurantDetails> getNearbyRestaurantWithoutBooked(List<RestaurantDetails> nearbyRestaurant, List<RestaurantDetails> bookedRestaurant) {
        List<RestaurantDetails> results = new ArrayList<>();
        results.addAll(nearbyRestaurant);
        if (bookedRestaurant != null) {
            List<RestaurantDetails> sup =  new ArrayList<>();
            for (RestaurantDetails nearby : nearbyRestaurant) {
                for (RestaurantDetails booked : bookedRestaurant) {
                    if (nearby.getPlaceId().equals(booked.getPlaceId())) {
                        sup.add(nearby);
                    }
                }
            }
            results.removeAll(sup);
        }
        return results;
    }

}
