package com.tonyocallimoutou.go4lunch.repository;

import android.content.Context;
import android.net.Uri;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tonyocallimoutou.go4lunch.model.User;

public class UserRepository {

    private static final String COLLECTION_NAME = "users";


    private static volatile UserRepository instance;

    private UserRepository() {}

    public static UserRepository getInstance() {
        UserRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized (UserRepository.class) {
            if (instance == null) {
                instance = new UserRepository();
            }
            return instance;
        }
    }

    public FirebaseUser getCurrentFirebaseUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public User getCurrentUser() {

        FirebaseUser user = getCurrentFirebaseUser();
        if (user != null) {
            String urlPicture = (user.getPhotoUrl() != null) ?
                    user.getPhotoUrl().toString()
                    : null;
            String username = user.getDisplayName();
            String uid = user.getUid();

            User currentUser = new User(uid, username, urlPicture);

            return currentUser;
        }
        return null;
    }

    public Task<Void> signOut(Context context) {
        return AuthUI.getInstance().signOut(context);
    }

    public CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public void createUser() {
        FirebaseUser user = getCurrentFirebaseUser();
        if (user != null) {
            String urlPicture = (user.getPhotoUrl() != null) ?
                    user.getPhotoUrl().toString()
                    : null;
            String username = user.getDisplayName();
            String uid = user.getUid();

            User userToCreate = new User(uid,username,urlPicture);

            getUsersCollection().document(uid).set(userToCreate);

        }
    }


}
