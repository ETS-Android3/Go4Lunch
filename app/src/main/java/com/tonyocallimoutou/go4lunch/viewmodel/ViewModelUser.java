package com.tonyocallimoutou.go4lunch.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.tonyocallimoutou.go4lunch.model.User;
import com.tonyocallimoutou.go4lunch.repository.UserRepository;

import java.util.List;

public class ViewModelUser extends ViewModel {

    private UserRepository userRepository;

    private MutableLiveData<List<User>> workmates = new MutableLiveData<>();


    public ViewModelUser (UserRepository userRepository) {
        this.userRepository = userRepository ;
    }

    // Current User

    public FirebaseUser getCurrentFirebaseUser() {
        return userRepository.getCurrentFirebaseUser();
    }

    public User getCurrentUser() {return userRepository.getCurrentUser();}

    public boolean isCurrentLogged() {
        return (this.getCurrentFirebaseUser() != null);
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

    // Workmates

    public void setWorkmatesList() {
        userRepository.setWorkmatesList(workmates);
    }

    public LiveData<List<User>> getWorkmates() {
        return workmates;
    }

}
