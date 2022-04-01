package com.tonyocallimoutou.go4lunch.utils;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.tonyocallimoutou.go4lunch.BuildConfig;
import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;

public class RestaurantPhoto {

    private static RestaurantDetails result;
    private static ImageView image;

    private RestaurantPhoto(RestaurantDetails restaurant, ImageView picture) {
        result = restaurant;
        image = picture;
    }


    public static RestaurantPhoto newInstance(RestaurantDetails restaurant, ImageView picture) {

        return new RestaurantPhoto(restaurant, picture);
    }

    public static void setLittlePhoto() {
        String photoRef = result.getPhotos().get(0).getPhotoReference();
        String request = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&maxheight=400&key=" +
                BuildConfig.PLACES_API_KEY +
                "&photoreference=" + photoRef;

        Glide.with(image.getContext()).load(request).into(image);
    }


}
