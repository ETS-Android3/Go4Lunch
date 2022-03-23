package com.tonyocallimoutou.go4lunch.repository;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tonyocallimoutou.go4lunch.model.Restaurant;
import com.tonyocallimoutou.go4lunch.Retrofit.PlaceDetail;
import com.tonyocallimoutou.go4lunch.Retrofit.RetrofitMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantRepository {

    private static final String COLLECTION_NAME = "restaurants";

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

    public CollectionReference getRestaurantsCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public void createRestaurant (String id, String name) {
        Restaurant restaurantToCreate = new Restaurant(id,name);
        getRestaurantsCollection().document(id).set(restaurantToCreate);
    }

    public void getNearbyPlace(String location, PlaceCallback callback) {
        retrofitMap.getNearbyPlaces(location).enqueue(new Callback<PlaceDetail>() {
            @Override
            public void onResponse(@NonNull Call<PlaceDetail> call, @NonNull Response<PlaceDetail> response) {
                if (response.isSuccessful()) {
                    PlaceDetail body = response.body();
                    callback.onSuccess(body);
                } else {
                    callback.onError();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PlaceDetail> call, @NonNull Throwable t) {
                callback.onError();
            }
        });

    }

    public interface PlaceCallback {
        void onSuccess(PlaceDetail placesDetail);
        void onError();
    }

}
