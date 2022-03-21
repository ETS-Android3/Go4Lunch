package com.tonyocallimoutou.go4lunch.viewmodel;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.tonyocallimoutou.go4lunch.R;
import com.tonyocallimoutou.go4lunch.repository.RestaurantRepository;
import com.tonyocallimoutou.go4lunch.repository.UserRepository;

import java.util.List;

public class ViewModelUser extends ViewModel {

    public UserRepository userRepository;

    public RestaurantRepository restaurantRepository;

    public ViewModelUser () {
        userRepository = UserRepository.getInstance();
        restaurantRepository = RestaurantRepository.getInstance();
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

}
