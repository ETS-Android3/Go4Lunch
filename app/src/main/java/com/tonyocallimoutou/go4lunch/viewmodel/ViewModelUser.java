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
import com.tonyocallimoutou.go4lunch.Retrofit.NearByPlace;
import com.tonyocallimoutou.go4lunch.Retrofit.RetrofitMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ViewModelUser extends ViewModel {

    public UserRepository userRepository;


    public ViewModelUser () {
        userRepository = UserRepository.getInstance();
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

}
