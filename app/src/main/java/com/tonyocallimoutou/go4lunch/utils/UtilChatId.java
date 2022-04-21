package com.tonyocallimoutou.go4lunch.utils;

import androidx.annotation.Nullable;

import com.tonyocallimoutou.go4lunch.model.User;
import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UtilChatId {

    public static String getChatIdWithUsers(@Nullable RestaurantDetails restaurant,  List<User> users) {
        if (restaurant != null) {
            return "restaurantId:" + restaurant.getPlaceId();
        }
        else {
            if (users.size() != 0) {

                String id = "";
                List<String> listId = new ArrayList<>();
                for (User user : users) {
                    listId.add(user.getUid());
                }
                Collections.sort(listId);
                for (String userId : listId) {
                    id += (userId);
                }

                return id;
            }

            return null;
        }
    }
}
