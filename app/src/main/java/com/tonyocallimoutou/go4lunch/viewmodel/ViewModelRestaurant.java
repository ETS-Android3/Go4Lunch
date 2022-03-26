package com.tonyocallimoutou.go4lunch.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.CollectionReference;
import com.tonyocallimoutou.go4lunch.model.Places.NearbyPlace;
import com.tonyocallimoutou.go4lunch.repository.RestaurantRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewModelRestaurant extends ViewModel {

    private RestaurantRepository restaurantRepository;


    private MutableLiveData<NearbyPlace> nearbyPlaceMutableLiveData = new MutableLiveData<>();


    public ViewModelRestaurant(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }


    public LiveData<NearbyPlace> getNearbyPlaceLiveData() {
        return nearbyPlaceMutableLiveData;
    }


    // Nearby Restaurant


    public void setNearbyPlace(String location) {

        restaurantRepository.getNearbyPlace(location).enqueue(new Callback<NearbyPlace>() {
            @Override
            public void onResponse(Call<NearbyPlace> call, Response<NearbyPlace> response) {
                nearbyPlaceMutableLiveData.setValue(response.body());
            }

            @Override
            public void onFailure(Call<NearbyPlace> call, Throwable t) {
            }
        });
    }


    // Booked Restaurant

    public CollectionReference getBookedRestaurantsCollection(){
        return restaurantRepository.getBookedRestaurantsCollection();
    }

    private void createBookedRestaurantInFirebase(NearbyPlace listRestaurant) {
        restaurantRepository.createBookedRestaurantInFirebase(listRestaurant.getResults().get(0));
    }

}
