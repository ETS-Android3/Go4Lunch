package com.tonyocallimoutou.go4lunch.model;

import androidx.annotation.Nullable;

import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;
import com.tonyocallimoutou.go4lunch.utils.UtilChatId;

import java.util.ArrayList;
import java.util.List;

public class Chat {

    private String id;
    private List<User> users;
    private List<Message> messages;
    private RestaurantDetails restaurant;

    public Chat() {
    }

    public Chat(@Nullable RestaurantDetails restaurant, List<User> users) {
        this.users = users;
        id = UtilChatId.getChatIdWithUsers(restaurant,users);
        messages = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public RestaurantDetails getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(RestaurantDetails restaurant) {
        this.restaurant = restaurant;
    }
}
