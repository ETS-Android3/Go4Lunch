package com.tonyocallimoutou.go4lunch.utils;

import android.view.View;
import android.widget.ImageView;

import com.tonyocallimoutou.go4lunch.model.User;
import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;

import java.util.List;

public class RestaurantRate {

    private static RestaurantDetails restaurant;
    private static ImageView restaurantRate1;
    private static ImageView restaurantRate2;
    private static ImageView restaurantRate3;
    private static List<User> listWorkmates;
    private static int rateRestaurant;

    public RestaurantRate(RestaurantDetails restaurantDetails, ImageView rate1, ImageView rate2, ImageView rate3, List<User> workmates) {
        restaurant = restaurantDetails;
        restaurantRate1 = rate1;
        restaurantRate2 = rate2;
        restaurantRate3 = rate3;
        listWorkmates = workmates;
    }

    public static RestaurantRate newInstance(RestaurantDetails restaurant, ImageView rate1, ImageView rate2, ImageView rate3, List<User> workmates) {
        return new RestaurantRate(restaurant,rate1,rate2,rate3,workmates);
    }

    public static void setImage() {
        setRate();
        switch (rateRestaurant) {
            case 0:
                restaurantRate1.setVisibility(View.GONE);
                restaurantRate2.setVisibility(View.GONE);
                restaurantRate3.setVisibility(View.GONE);
                break;

            case 1:
                restaurantRate1.setVisibility(View.VISIBLE);
                restaurantRate2.setVisibility(View.GONE);
                restaurantRate3.setVisibility(View.GONE);
                break;

            case 2:
                restaurantRate1.setVisibility(View.VISIBLE);
                restaurantRate2.setVisibility(View.VISIBLE);
                restaurantRate3.setVisibility(View.GONE);
                break;

            case 3:
                restaurantRate1.setVisibility(View.VISIBLE);
                restaurantRate2.setVisibility(View.VISIBLE);
                restaurantRate3.setVisibility(View.VISIBLE);
                break;

            default:
                break;
        }
    }

    private static void setRate() {
        double count = 0;
        for (User user : listWorkmates) {
            if (user.getLikeRestaurantId().contains(restaurant.getPlaceId())) {
                count ++;
            }
        }

        double rate = count/listWorkmates.size();

        rateRestaurant = (int) Math.round(rate*3);
    }

    public static int getRate() {
        return rateRestaurant;
    }
}
