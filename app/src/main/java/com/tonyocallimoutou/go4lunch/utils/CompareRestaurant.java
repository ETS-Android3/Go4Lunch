package com.tonyocallimoutou.go4lunch.utils;

import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;

import java.util.List;

public class CompareRestaurant {

    public static boolean isEqual(List<RestaurantDetails> restaurants, List<RestaurantDetails> comparator) {
        if (restaurants == null || comparator == null || restaurants.size() ==0) {
            return false;
        }

        if (restaurants.size() == comparator.size()) {
            for (int i=0; i<restaurants.size();i++) {
                if (! restaurants.get(i).getPlaceId().equals(comparator.get(i).getPlaceId())) {
                    return false;
                }
            }
        }
        else {
            return false;
        }

        return true;

    }
}
