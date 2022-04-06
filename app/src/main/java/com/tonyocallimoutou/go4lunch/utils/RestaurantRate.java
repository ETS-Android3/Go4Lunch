package com.tonyocallimoutou.go4lunch.utils;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class RestaurantRate {

    private static int rateRestaurant;
    private static ImageView restaurantRate1;
    private static ImageView restaurantRate2;
    private static ImageView restaurantRate3;

    public RestaurantRate(int rate, ImageView rate1, ImageView rate2, ImageView rate3) {
        rateRestaurant = rate;
        restaurantRate1 = rate1;
        restaurantRate2 = rate2;
        restaurantRate3 = rate3;
    }

    public static RestaurantRate newInstance(int rate, ImageView rate1, ImageView rate2, ImageView rate3) {
        return new RestaurantRate(rate,rate1,rate2,rate3);
    }

    public static void setImage() {
        switch (rateRestaurant) {
            case 0:
                Log.d("TAG", "0: " + rateRestaurant);
                restaurantRate1.setVisibility(View.GONE);
                restaurantRate2.setVisibility(View.GONE);
                restaurantRate3.setVisibility(View.GONE);
                break;

            case 1:
                Log.d("TAG", "1: " + rateRestaurant);
                restaurantRate1.setVisibility(View.VISIBLE);
                restaurantRate2.setVisibility(View.GONE);
                restaurantRate3.setVisibility(View.GONE);
                break;

            case 2:
                Log.d("TAG", "2: " + rateRestaurant);
                restaurantRate1.setVisibility(View.VISIBLE);
                restaurantRate2.setVisibility(View.VISIBLE);
                restaurantRate3.setVisibility(View.GONE);
                break;

            case 3:
                Log.d("TAG", "3: " + rateRestaurant);
                restaurantRate1.setVisibility(View.VISIBLE);
                restaurantRate2.setVisibility(View.VISIBLE);
                restaurantRate3.setVisibility(View.VISIBLE);
                break;

            default:
                Log.d("TAG", "PB: " + rateRestaurant);
                break;
        }
    }
}
