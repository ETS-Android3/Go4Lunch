package com.tonyocallimoutou.go4lunch.viewmodel;

import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.tonyocallimoutou.go4lunch.repository.UserRepository;

public class ViewModelUser extends ViewModel {

    public UserRepository userRepository;

    public ViewModelUser () {
        userRepository = UserRepository.getInstance();
    }

    public FirebaseUser getCurrentUser() {
        return userRepository.getCurrentUser();
    }

    public boolean isCurrentLogged() {
        return (this.getCurrentUser() != null);
    }
}
