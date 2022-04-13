package com.tonyocallimoutou.go4lunch.utils;

import com.tonyocallimoutou.go4lunch.model.User;
import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;

import java.util.ArrayList;
import java.util.List;

public class WorkmatesLunch {

    public static List<User> getWorkmatesLunch(RestaurantDetails restaurant, List<User> workmates) {
        List<User> removeUser = new ArrayList<>();
        List<User> workmatesLunch = new ArrayList<>();
        workmatesLunch.addAll(workmates);

        if (restaurant.getWorkmatesId().size() != 0) {
            for (User user : workmatesLunch) {
                if ( ! restaurant.getWorkmatesId().contains(user.getUid())) {
                    removeUser.add(user);
                }
            }
            workmatesLunch.removeAll(removeUser);
        }
        else {
            workmatesLunch.clear();
        }
        return workmatesLunch;
    }
}
