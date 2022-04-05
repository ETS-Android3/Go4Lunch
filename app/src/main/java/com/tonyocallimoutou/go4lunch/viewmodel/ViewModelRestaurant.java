package com.tonyocallimoutou.go4lunch.viewmodel;

import android.location.Location;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tonyocallimoutou.go4lunch.api.RetrofitMap;
import com.tonyocallimoutou.go4lunch.model.places.details.PlaceDetails;
import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;
import com.tonyocallimoutou.go4lunch.model.places.nearby.NearbyPlace;
import com.tonyocallimoutou.go4lunch.repository.RestaurantRepository;
import com.tonyocallimoutou.go4lunch.repository.UserRepository;
import com.tonyocallimoutou.go4lunch.utils.UtilDistance;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewModelRestaurant extends ViewModel {

    private RestaurantRepository restaurantRepository;
    private UserRepository userRepository;

    private MutableLiveData<List<RestaurantDetails>> nearbyPlaceMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<List<RestaurantDetails>> bookedRestaurantMutableLiveData = new MutableLiveData<>();


    // Constructor

    public ViewModelRestaurant() {
        this.restaurantRepository = RestaurantRepository.getInstance(RetrofitMap.retrofit.create(RetrofitMap.class));
        this.userRepository = UserRepository.getInstance();
    }


    // Nearby Restaurant

    public void setNearbyPlace(Location userLocation) {
        Log.d("TAG", "setNearbyPlace: ");
        restaurantRepository.setNearbyPlace(userLocation, nearbyPlaceMutableLiveData);
    }


    public LiveData<List<RestaurantDetails>> getNearbyRestaurantLiveData() {
        return nearbyPlaceMutableLiveData ;
    }

    // Booked Restaurant

    public RestaurantDetails getRestaurantOfCurrentUser() {
        if (userRepository.getCurrentUser() != null) {
            return userRepository.getCurrentUser().getBookedRestaurant();
        }
        return null;
    }

    public void bookedThisRestaurant(RestaurantDetails restaurant) {

        if (getRestaurantOfCurrentUser() != null) {
            Log.d("TAG", "bookedThisRestaurant: Test ");
            cancelBookedRestaurant(getRestaurantOfCurrentUser());
        }


        restaurant.getWorkmatesId().add(userRepository.getCurrentUser().getUid());

        restaurantRepository.createBookedRestaurantInFirebase(restaurant);

        userRepository.bookedRestaurant(restaurant);
    }

    public void cancelBookedRestaurant(RestaurantDetails restaurant) {

        userRepository.cancelRestaurant();
        restaurant.getWorkmatesId().remove(userRepository.getCurrentUser().getUid());

        restaurantRepository.cancelBookedRestaurantInFirebase(restaurant);
    }

    public void setBookedRestaurantList() {
        restaurantRepository.setBookedRestaurantFirestore(bookedRestaurantMutableLiveData);
    }

    public LiveData<List<RestaurantDetails>> getBookedRestaurantLiveData() {
        return bookedRestaurantMutableLiveData;
    }
}