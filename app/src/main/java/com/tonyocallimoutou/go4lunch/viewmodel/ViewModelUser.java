package com.tonyocallimoutou.go4lunch.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.tonyocallimoutou.go4lunch.repository.RestaurantRepository;
import com.tonyocallimoutou.go4lunch.repository.UserRepository;
import com.tonyocallimoutou.go4lunch.Retrofit.PlaceDetail;
import com.tonyocallimoutou.go4lunch.Retrofit.RetrofitMap;
import com.tonyocallimoutou.go4lunch.Retrofit.RetrofitMapCall;

public class ViewModelUser extends ViewModel {

    public UserRepository userRepository;

    public RestaurantRepository restaurantRepository;

    private MutableLiveData<PlaceDetail> placesDetailLiveData = new MutableLiveData<>();



    public ViewModelUser () {
        userRepository = UserRepository.getInstance();
        restaurantRepository = RestaurantRepository.getInstance(RetrofitMapCall.retrofit.create(RetrofitMap.class));
    }

    // User

    public FirebaseUser getCurrentUser() {
        return userRepository.getCurrentUser();
    }

    public boolean isCurrentLogged() {
        return (this.getCurrentUser() != null);
    }

    public Task<Void> signOut(Context context) {
        return userRepository.signOut(context);
    }

    public void createUser() {
        userRepository.createUser();
    }

    public CollectionReference getUsersCollection(){
        return userRepository.getUsersCollection();
    }


    // Restaurant

    public void createRestaurant(String id, String name) {
        restaurantRepository.createRestaurant(id,name);
    }

    public CollectionReference getRestaurantsCollection(){
        return restaurantRepository.getRestaurantsCollection();
    }

    public LiveData<PlaceDetail> getPlacesLiveData() {
        return placesDetailLiveData;
    }

    public void getNearbyPlace(String location) {
        Log.d("TAG", "getNearbyPlace: ");
        restaurantRepository.getNearbyPlace(location, new RestaurantRepository.PlaceCallback() {
            @Override
            public void onSuccess(PlaceDetail placesDetail) {
                Log.d("TAG", "onSuccess: ");
                placesDetailLiveData.setValue(placesDetail);
            }

            @Override
            public void onError() {

            }
        });
    }

}
