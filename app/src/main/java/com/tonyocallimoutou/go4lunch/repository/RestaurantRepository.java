package com.tonyocallimoutou.go4lunch.repository;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tonyocallimoutou.go4lunch.model.Restaurant;
import com.tonyocallimoutou.go4lunch.Retrofit.NearByPlace;
import com.tonyocallimoutou.go4lunch.Retrofit.RetrofitMap;

import retrofit2.Call;

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

    public Call<NearByPlace> getNearByPlace(String location) {
        return retrofitMap.getNearByPlaces(location);
    }

}
