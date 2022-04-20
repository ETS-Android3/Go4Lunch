package com.tonyocallimoutou.go4lunch.viewmodel;

import android.location.Location;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tonyocallimoutou.go4lunch.model.User;
import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;
import com.tonyocallimoutou.go4lunch.model.places.search.Prediction;
import com.tonyocallimoutou.go4lunch.repository.RestaurantRepository;
import com.tonyocallimoutou.go4lunch.repository.UserRepository;
import com.tonyocallimoutou.go4lunch.utils.PredictionOfWorkmates;

import java.util.List;

public class ViewModelRestaurant extends ViewModel {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    private final MutableLiveData<List<RestaurantDetails>> nearbyPlaceMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<RestaurantDetails>> bookedRestaurantMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Prediction>> predictionsMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<RestaurantDetails> detailsRestaurantMutableLiveDataMutableLiveData = new MutableLiveData<>();
    private Location userLocation;


    // Constructor

    public ViewModelRestaurant(RestaurantRepository restaurantRepository, UserRepository userRepository) {
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }


    // Nearby Restaurant

    public void setNearbyPlace(Location userLocation) {
        this.userLocation = userLocation;
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
            cancelBookedRestaurant(getRestaurantOfCurrentUser());
        }

        restaurant.getWorkmatesId().add(userRepository.getCurrentUser().getUid());

        restaurantRepository.createBookedRestaurantInFirebase(restaurant);

        userRepository.bookedRestaurant(restaurant);
    }

    public void cancelBookedRestaurant(RestaurantDetails restaurant) {

        restaurant.getWorkmatesId().remove(userRepository.getCurrentUser().getUid());

        userRepository.cancelRestaurant();
        restaurantRepository.cancelBookedRestaurantInFirebase(restaurant);
    }

    public void setBookedRestaurantList() {
        restaurantRepository.setBookedRestaurantFirestore(bookedRestaurantMutableLiveData);
    }

    public LiveData<List<RestaurantDetails>> getBookedRestaurantLiveData() {
        return bookedRestaurantMutableLiveData;
    }

    // Like

    public void likeThisRestaurant(RestaurantDetails restaurant) {
        userRepository.likeThisRestaurant(restaurant);
    }

    public void dislikeThisRestaurant(RestaurantDetails restaurant) {
        userRepository.dislikeThisRestaurant(restaurant);
    }

    // Search

    public void setSearchRestaurant(String input) {
        if (input != null) {
            restaurantRepository.setSearchRestaurant(userLocation, input, predictionsMutableLiveData);
        }
    }

    public void setSearchWorkmates(String input, List<User> workmates) {
        if (input != null) {
            List<Prediction> predictions = PredictionOfWorkmates.getWorkmates(input,workmates);
            predictionsMutableLiveData.postValue(predictions);
        }
    }

    public LiveData<List<Prediction>> getPredictionLiveData() {
        return predictionsMutableLiveData;
    }

    // Detail Restaurant

    public void setDetailsRestaurantForPrediction(Prediction prediction) {
        restaurantRepository.setDetailForPrediction(prediction,detailsRestaurantMutableLiveDataMutableLiveData);
    }

    public LiveData<RestaurantDetails> getDetailPrediction() {
        return detailsRestaurantMutableLiveDataMutableLiveData;
    }
}