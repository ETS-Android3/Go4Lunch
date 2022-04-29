package com.tonyocallimoutou.go4lunch.utils;

import androidx.annotation.Nullable;

import com.tonyocallimoutou.go4lunch.model.Chat;
import com.tonyocallimoutou.go4lunch.model.User;
import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UtilChatId {

    private static final String restaurantId = "restaurantId:";

    public static String getChatIdWithUsers(@Nullable RestaurantDetails restaurant,  List<User> users) {
        if (restaurant != null) {
            return restaurantId + restaurant.getPlaceId();
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

    public static Map<String, Integer> getNumberOfNoReadingMessage(User currentUser, List<Chat> chatList) {
        String id;
        int nbr = 0;
        Map<String, Integer> map = new HashMap<String, Integer>();

        for (Chat chat : chatList) {
            if (chat.getId().contains(currentUser.getUid())) {
                id = chat.getId().replaceAll(currentUser.getUid(), "");
                nbr = chat.getNoReadingMessageNumber(currentUser);
                map.put(id, nbr);
            }
            else if (chat.getId().contains(restaurantId)) {
                id = chat.getId().replaceAll(restaurantId,"");
                nbr = chat.getNoReadingMessageNumber(currentUser);
                map.put(id, nbr);
            }
        }
        return map;
    }
}
