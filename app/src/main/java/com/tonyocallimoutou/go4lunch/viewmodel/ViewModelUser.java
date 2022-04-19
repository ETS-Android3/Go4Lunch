package com.tonyocallimoutou.go4lunch.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.tonyocallimoutou.go4lunch.model.User;
import com.tonyocallimoutou.go4lunch.repository.RestaurantRepository;
import com.tonyocallimoutou.go4lunch.repository.UserRepository;

import java.util.List;

public class ViewModelUser extends ViewModel {

    private UserRepository userRepository;
    private RestaurantRepository restaurantRepository;

    private MutableLiveData<List<User>> workmates = new MutableLiveData<>();
    private MutableLiveData<User> currentUserLiveData = new MutableLiveData<>();


    public ViewModelUser (UserRepository userRepository, RestaurantRepository restaurantRepository) {
        this.userRepository = userRepository ;
        this.restaurantRepository = restaurantRepository;
    }

    // Current User

    public FirebaseUser getCurrentFirebaseUser() {
        return userRepository.getCurrentFirebaseUser();
    }

    public User getCurrentUser() {return userRepository.getCurrentUser();}

    public void setCurrentUserLiveData() {
        userRepository.setCurrentUserLivedata(currentUserLiveData);
    }

    public LiveData<User> getCurrentUserLiveData() {
        return currentUserLiveData;
    }

    public boolean isCurrentLogged() {
        return (this.getCurrentFirebaseUser() != null);
    }

    public Task<Void> signOut(Context context) {
        return userRepository.signOut(context);
    }

    public void createUser() {
        userRepository.createUser();
    }

    public Task<Void> deleteUser(Context context){
        if (userRepository.getCurrentUser().getBookedRestaurant() != null) {
            restaurantRepository.cancelBookedRestaurantInFirebase(userRepository.getCurrentUser(), userRepository.getCurrentUser().getBookedRestaurant());
            userRepository.cancelRestaurant();
        }
        return userRepository.deleteUser(context);
    }

    public void setNameOfCurrentUser(String name) {
        userRepository.setNameOfCurrentUser(name);
    }

    // Workmates

    public void setWorkmatesList() {
        userRepository.setWorkmatesList(workmates);
    }

    public LiveData<List<User>> getWorkmates() {
        return workmates;
    }

}
