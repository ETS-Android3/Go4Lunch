package com.tonyocallimoutou.go4lunch.utils;

import com.tonyocallimoutou.go4lunch.model.User;
import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;

import java.util.ArrayList;
import java.util.List;

public class WorkmatesLunch {

    public static List<User> getWorkmatesLunch(RestaurantDetails restaurant, List<User> workmates) {

        List<User> workmatesLunch = new ArrayList<>();

        if (restaurant.getWorkmatesId().size() != 0) {
            for (User user : workmates) {
                if (user.getBookedRestaurant() != null) {
                    if (user.getBookedRestaurant().getPlaceId().equals(restaurant.getPlaceId())) {
                        workmatesLunch.add(user);
                    }
                }
            }
        }
        else {
            workmatesLunch.clear();
        }
        return workmatesLunch;
    }
}
