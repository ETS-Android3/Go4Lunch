package com.tonyocallimoutou.go4lunch.viewmodel;

import android.location.Location;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tonyocallimoutou.go4lunch.api.RetrofitMap;
import com.tonyocallimoutou.go4lunch.model.Places.NearbyPlace;
import com.tonyocallimoutou.go4lunch.model.Places.RestaurantsResult;
import com.tonyocallimoutou.go4lunch.model.User;
import com.tonyocallimoutou.go4lunch.repository.RestaurantRepository;
import com.tonyocallimoutou.go4lunch.repository.UserRepository;
import com.tonyocallimoutou.go4lunch.utils.RestaurantMethod;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewModelRestaurant extends ViewModel {

    private RestaurantRepository restaurantRepository;
    private UserRepository userRepository;

    private final int nbrOfNearbyRestaurant = 10;

    private MutableLiveData<List<RestaurantsResult>> nearbyPlaceMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<List<RestaurantsResult>> bookedRestaurantMutableLiveData = new MutableLiveData<>();


    // Constructor

    public ViewModelRestaurant() {
        this.restaurantRepository = RestaurantRepository.getInstance(RetrofitMap.retrofit.create(RetrofitMap.class));
        this.userRepository = UserRepository.getInstance();
    }


    // Nearby Restaurant

    public void setNearbyPlace(Location userLocation) {

        if (userLocation != null) {

            String location = userLocation.getLatitude() + "," + userLocation.getLongitude();

            restaurantRepository.getNearbyPlace(location).enqueue(new Callback<NearbyPlace>() {
                @Override
                public void onResponse(Call<NearbyPlace> call, Response<NearbyPlace> response) {
                    nearbyPlaceMutableLiveData.setValue(response.body().getResults());
                    List<RestaurantsResult> nearby = nearbyPlaceMutableLiveData.getValue();
                    List<RestaurantsResult> booked = bookedRestaurantMutableLiveData.getValue();
                    RestaurantMethod.getNearbyRestaurantWithoutBooked(nearby, booked);

                    nearbyPlaceMutableLiveData.setValue(nearby);
                    Log.d("TAG", "onResponse: " + nearbyPlaceMutableLiveData.getValue().size());
                }

                @Override
                public void onFailure(Call<NearbyPlace> call, Throwable t) {
                }
            });
        }
    }

    public LiveData<List<RestaurantsResult>> getNearbyRestaurantLiveData() {
        return nearbyPlaceMutableLiveData ;
    }



    // Booked Restaurant

    public CollectionReference getBookedRestaurantsCollection(){
        return restaurantRepository.getBookedRestaurantsCollection();
    }

    private void createBookedRestaurantInFirebase(RestaurantsResult restaurant) {
        restaurantRepository.createBookedRestaurantInFirebase(restaurant);
    }

    public RestaurantsResult getRestaurantOfCurrentUser() {
        if (userRepository.getCurrentUser() != null) {
            return userRepository.getCurrentUser().getBookedRestaurant();
        }
        return null;
    }

    public void bookedThisRestaurant(RestaurantsResult restaurant) {
        Log.d("TAG", "bookedThisRestaurant: ");

        if (userRepository.getCurrentUser().getBookedRestaurant() != null) {
            Log.d("TAG", "bookedThisRestaurant: CANCEL");
            cancelBookedRestaurant(userRepository.getCurrentUser().getBookedRestaurant());
        }

        userRepository.bookedRestaurant(restaurant);

        restaurant.getWorkmatesId().add(userRepository.getCurrentUser().getUid());
        createBookedRestaurantInFirebase(restaurant);
    }

    public void cancelBookedRestaurant(RestaurantsResult restaurant) {
        Log.d("TAG", "CancelRestaurantBooking: ");

        userRepository.cancelRestaurant();
        restaurant.getWorkmatesId().remove(userRepository.getCurrentUser().getUid());

        getBookedRestaurantsCollection().document(restaurant.getPlaceId()).set(restaurant);
        if (!restaurant.isBooked()) {
            Log.d("TAG", "cancelRestaurantBooking: OK");
            getBookedRestaurantsCollection().document(restaurant.getPlaceId()).delete();
            nearbyPlaceMutableLiveData.getValue().add(restaurant);
        }

        setBookedRestaurantList();
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

                        bookedRestaurantMutableLiveData.setValue(restaurants);

                    }
                });

    }

    public LiveData<List<RestaurantsResult>> getBookedRestaurantLiveData() {
        return bookedRestaurantMutableLiveData;
    }


}
