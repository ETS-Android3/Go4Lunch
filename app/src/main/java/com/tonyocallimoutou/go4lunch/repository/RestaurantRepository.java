package com.tonyocallimoutou.go4lunch.repository;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.search.CategorySearchEngine;
import com.mapbox.search.CategorySearchOptions;
import com.mapbox.search.MapboxSearchSdk;
import com.mapbox.search.ResponseInfo;
import com.mapbox.search.SearchCallback;
import com.mapbox.search.SearchRequestTask;
import com.mapbox.search.result.SearchAddress;
import com.mapbox.search.result.SearchResult;
import com.tonyocallimoutou.go4lunch.MainActivity;
import com.tonyocallimoutou.go4lunch.R;
import com.tonyocallimoutou.go4lunch.model.Restaurant;

import java.util.List;

public class RestaurantRepository {

    private static final String COLLECTION_NAME = "restaurants";

    private static volatile RestaurantRepository instance;

    public RestaurantRepository() {
    }

    public static RestaurantRepository getInstance() {
        RestaurantRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized (RestaurantRepository.class) {
            if (instance == null) {
                instance = new RestaurantRepository();
            }
            return instance;
        }
    }

    public CollectionReference getRestaurantsCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public void createRestaurant (String id, String name, double distance, List<String> categories, SearchAddress address) {
        Restaurant restaurantToCreate = new Restaurant(id,name,distance,categories,address);
        getRestaurantsCollection().document(id).set(restaurantToCreate);
    }

}
