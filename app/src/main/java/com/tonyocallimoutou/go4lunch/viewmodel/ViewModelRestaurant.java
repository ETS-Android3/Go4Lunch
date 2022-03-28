package com.tonyocallimoutou.go4lunch.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tonyocallimoutou.go4lunch.model.Places.NearbyPlace;
import com.tonyocallimoutou.go4lunch.model.Places.RestaurantsResult;
import com.tonyocallimoutou.go4lunch.repository.RestaurantRepository;
import com.tonyocallimoutou.go4lunch.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewModelRestaurant extends ViewModel {

    private RestaurantRepository restaurantRepository;
    private UserRepository userRepository;


    private MutableLiveData<List<RestaurantsResult>> nearbyPlaceMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<List<RestaurantsResult>> bookedRestaurantMutableLiveData = new MutableLiveData<>();


    public ViewModelRestaurant(RestaurantRepository restaurantRepository, UserRepository userRepository) {
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }


    public LiveData<List<RestaurantsResult>> getNearbyRestaurantLiveData() {
        return nearbyPlaceMutableLiveData ;
    }


    // Nearby Restaurant


    public void setNearbyPlace(String location) {

        restaurantRepository.getNearbyPlace(location).enqueue(new Callback<NearbyPlace>() {
            @Override
            public void onResponse(Call<NearbyPlace> call, Response<NearbyPlace> response) {
                nearbyPlaceMutableLiveData.setValue(response.body().getResults());
            }

            @Override
            public void onFailure(Call<NearbyPlace> call, Throwable t) {
            }
        });
    }


    // Booked Restaurant

    public CollectionReference getBookedRestaurantsCollection(){
        return restaurantRepository.getBookedRestaurantsCollection();
    }

    private void createBookedRestaurantInFirebase(RestaurantsResult restaurant) {
        restaurantRepository.createBookedRestaurantInFirebase(restaurant);
    }

    public void bookedThisRestaurant(RestaurantsResult restaurant) {
        Log.d("TAG", "bookedThisRestaurant: ");


        restaurant.getWorkmates().add(userRepository.getCurrentUser());
        createBookedRestaurantInFirebase(restaurant);
    }

    public void cancelRestaurantBooking(RestaurantsResult restaurant) {
        Log.d("TAG", "CancelRestaurantBooking: ");
        restaurant.getWorkmates().remove(userRepository.getCurrentUser());
        if (! restaurant.isBooked()) {
            getBookedRestaurantsCollection().document(restaurant.getPlaceId()).delete();
        }
    }

    public LiveData<List<RestaurantsResult>> getBookedRestaurantLiveData() {
        return bookedRestaurantMutableLiveData;
    }

    public void setBookedRestaurantList() {
        List<RestaurantsResult> restaurants = new ArrayList<>();
        getBookedRestaurantsCollection().get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot document : list) {
                                RestaurantsResult restaurant = document.toObject(RestaurantsResult.class);
                                restaurants.add(restaurant);
                            }
                        }

                        Log.d("TAG", "onSuccess: booke" + restaurants.size());
                        bookedRestaurantMutableLiveData.setValue(restaurants);

                    }
                });

    }

}
