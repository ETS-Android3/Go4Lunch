package com.tonyocallimoutou.go4lunch.repository;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tonyocallimoutou.go4lunch.model.Places.RestaurantsResult;
import com.tonyocallimoutou.go4lunch.model.Places.NearbyPlace;
import com.tonyocallimoutou.go4lunch.api.RetrofitMap;

import retrofit2.Call;

public class RestaurantRepository {

    private static final String COLLECTION_NAME_BOOKED_RESTAURANT = "booked_restaurant";

    private static volatile RestaurantRepository instance;
    private RetrofitMap retrofitMap;

    public RestaurantRepository(RetrofitMap retrofitMap) {
        this.retrofitMap = retrofitMap;
    }

    public static RestaurantRepository getInstance(RetrofitMap retrofitMap) {
        RestaurantRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized (RestaurantRepository.class) {
            if (instance == null) {
                instance = new RestaurantRepository(retrofitMap);
            }
            return instance;
        }
    }


    public CollectionReference getBookedRestaurantsCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME_BOOKED_RESTAURANT);
    }

    public void createBookedRestaurantInFirebase (RestaurantsResult restaurant) {
        getBookedRestaurantsCollection().document(restaurant.getPlaceId()).set(restaurant);
    }

    public Call<NearbyPlace> getNearbyPlace(String location) {
        return retrofitMap.getNearbyPlaces(location);
    }

}
