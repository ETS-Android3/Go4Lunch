package com.tonyocallimoutou.go4lunch.repository;

import android.content.Context;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.tonyocallimoutou.go4lunch.model.Places.RestaurantsResult;
import com.tonyocallimoutou.go4lunch.model.User;

import java.util.List;

public class UserRepository {

    private static final String COLLECTION_NAME = "users";

    private User currentUser;

    private static volatile UserRepository instance;

    private UserRepository() {}

    // Instance

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

    // My Firestore Collection

    public CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // Get Current User

    public FirebaseUser getCurrentFirebaseUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public User getCurrentUser() {

        FirebaseUser user = getCurrentFirebaseUser();
        if (user != null) {
            if (currentUser == null || ! currentUser.getUid().equals(user.getUid())) {

                return null;
            }
            return currentUser;
        }
        return null;
    }

    public void createUser() {

        FirebaseUser user = getCurrentFirebaseUser();
        if (user != null) {


            getUsersCollection().get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            boolean isAlreadyExisting = false;
                            if (!queryDocumentSnapshots.isEmpty()) {
                                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                for (DocumentSnapshot document : list) {
                                    User workmate = document.toObject(User.class);
                                    if (workmate.getUid().equals(user.getUid())) {
                                        isAlreadyExisting = true;
                                        currentUser = workmate;
                                    }
                                }
                            }
                            if ( ! isAlreadyExisting) {

                                Log.d("TAG", "createUser: " + user);

                                String urlPicture = (user.getPhotoUrl() != null) ?
                                        user.getPhotoUrl().toString()
                                        : null;
                                String username = user.getDisplayName();
                                String uid = user.getUid();

                                currentUser = new User(uid, username, urlPicture);

                                getUsersCollection().document(currentUser.getUid()).set(currentUser);
                            }
                        }
                    });
        }
    }

    public Task<Void> signOut(Context context) {
        return AuthUI.getInstance().signOut(context);
    }

    // Booked Restaurant

    public void bookedRestaurant(RestaurantsResult restaurant) {
        currentUser.setBookedRestaurant(restaurant);
        getUsersCollection().document(currentUser.getUid()).set(currentUser);
    }

    public void cancelRestaurant() {
        currentUser.setBookedRestaurant(null);
        getUsersCollection().document(currentUser.getUid()).set(currentUser);
    }


}
