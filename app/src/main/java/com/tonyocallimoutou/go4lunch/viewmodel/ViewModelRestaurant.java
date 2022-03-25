package com.tonyocallimoutou.go4lunch.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.CollectionReference;
import com.tonyocallimoutou.go4lunch.model.Places.NearbyPlace;
import com.tonyocallimoutou.go4lunch.api.RetrofitMap;
import com.tonyocallimoutou.go4lunch.repository.RestaurantRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewModelRestaurant extends ViewModel {

    public RestaurantRepository restaurantRepository;


    private MutableLiveData<NearbyPlace> nearbyPlaceMutableLiveData = new MutableLiveData<>();

    private int sizeOfListRestaurantNearbyPlace = 10;


    public ViewModelRestaurant(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
        restaurantRepository = RestaurantRepository.getInstance(RetrofitMap.retrofit.create(RetrofitMap.class));
    }


    public LiveData<NearbyPlace> getNearbyPlaceLiveData() {
        return nearbyPlaceMutableLiveData;
    }


    // Nearby Restaurant

    public CollectionReference getNearbyRestaurantsCollection(){
        return restaurantRepository.getNearbyRestaurantsCollection();
    }


    public void setNearbyPlaceInFirebase(String location) {

        restaurantRepository.getNearbyPlace(location).enqueue(new Callback<NearbyPlace>() {
            @Override
            public void onResponse(Call<NearbyPlace> call, Response<NearbyPlace> response) {
                nearbyPlaceMutableLiveData.setValue(response.body());
                createNearbyRestaurantInFirebase(response.body());
            }

            @Override
            public void onFailure(Call<NearbyPlace> call, Throwable t) {
            }
        });
    }

    private void createNearbyRestaurantInFirebase(NearbyPlace listRestaurant) {

        restaurantRepository.removeNearbyRestaurantInFirebase();

        if (listRestaurant != null) {
            for (int i=0; i<listRestaurant.getResults().size(); i++) {
                if (i< sizeOfListRestaurantNearbyPlace) {
                    restaurantRepository.createNearbyRestaurantInFirebase(listRestaurant.getResults().get(i));
                }
            }
        }
    }


    // Booked Restaurant

    public CollectionReference getBookedRestaurantsCollection(){
        return restaurantRepository.getBookedRestaurantsCollection();
    }

    private void createBookedRestaurantInFirebase(NearbyPlace listRestaurant) {
        restaurantRepository.createBookedRestaurantInFirebase(listRestaurant.getResults().get(0));
    }

}
