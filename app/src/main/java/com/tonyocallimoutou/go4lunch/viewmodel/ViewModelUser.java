package com.tonyocallimoutou.go4lunch.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tonyocallimoutou.go4lunch.model.User;
import com.tonyocallimoutou.go4lunch.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class ViewModelUser extends ViewModel {

    private UserRepository userRepository;

    private MutableLiveData<List<User>> workmates = new MutableLiveData<>();


    public ViewModelUser (UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // User

    public FirebaseUser getCurrentFirebaseUser() {
        return userRepository.getCurrentFirebaseUser();
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

    public CollectionReference getUsersCollection(){
        return userRepository.getUsersCollection();
    }

    public LiveData<List<User>> getWorkmates() {
        return workmates;
    }

    public void setWorkmatesList() {
        List<User> workmatesList = new ArrayList<>();

        getUsersCollection().get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot document : list) {
                                User user = document.toObject(User.class);
                                if (!user.getUid().equals(getCurrentFirebaseUser().getUid())) {
                                    workmatesList.add(user);
                                }
                            }
                        }

                        workmates.setValue(workmatesList);
                    }
                });
    }

}
