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
import com.tonyocallimoutou.go4lunch.model.places.details.PlaceDetails;
import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;
import com.tonyocallimoutou.go4lunch.model.places.nearby.NearbyPlace;
import com.tonyocallimoutou.go4lunch.repository.RestaurantRepository;
import com.tonyocallimoutou.go4lunch.repository.UserRepository;
import com.tonyocallimoutou.go4lunch.utils.UtilDistance;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewModelRestaurant extends ViewModel {

    private RestaurantRepository restaurantRepository;
    private UserRepository userRepository;

    private MutableLiveData<List<RestaurantDetails>> nearbyPlaceMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<List<RestaurantDetails>> bookedRestaurantMutableLiveData = new MutableLiveData<>();


    // Constructor

    public ViewModelRestaurant() {
        this.restaurantRepository = RestaurantRepository.getInstance(RetrofitMap.retrofit.create(RetrofitMap.class));
        this.userRepository = UserRepository.getInstance();
    }


    // Nearby Restaurant

    public void setNearbyPlace(Location userLocation) {

        if (userLocation != null) {

            UtilDistance.roundToThousandths(userLocation);

            List<String> listLocation = new ArrayList<>();



            restaurantRepository.getLocationNearbyRestaurantsCollection()
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot document : list) {
                        String location = document.getId();
                        listLocation.add(location);
                    }

                    if (listLocation.contains(userLocation.toString())) {
                        Log.d("TAG", "YES: ");
                        initNearbyPlaceLiveData(userLocation);
                    }
                    else {
                        Log.d("TAG", "NO: ");
                        getNearbyPlace(userLocation);
                    }

                }
            });


        }
    }

    private void getNearbyPlace(Location userLocation) {
        String location = userLocation.getLatitude() + "," + userLocation.getLongitude();

        restaurantRepository.getNearbyPlace(location).enqueue(new Callback<NearbyPlace>() {
            @Override
            public void onResponse(Call<NearbyPlace> call, Response<NearbyPlace> response) {
                setListRestaurantsDetails(userLocation, response.body().getResults());
            }

            @Override
            public void onFailure(Call<NearbyPlace> call, Throwable t) {
            }
        });
    }

    private CollectionReference getNearbyRestaurantsCollection(Location userLocation){
        return restaurantRepository.getNearbyRestaurantsCollection(userLocation);
    }

    private void createNearbyRestaurantInFirebase(Location userLocation, List<RestaurantDetails> restaurants) {
        restaurantRepository.createNearbyRestaurantInFirebase(userLocation, restaurants);
    }

    private void initNearbyPlaceLiveData(Location userLocation) {
        Log.d("TAG", "initNearbyPlaceLiveData: ");
        List<RestaurantDetails> restaurants = new ArrayList<>();
        getNearbyRestaurantsCollection(userLocation).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        Log.d("TAG", "onSuccess: " + list.size());
                        for (DocumentSnapshot document : list) {
                            RestaurantDetails restaurant = document.toObject(RestaurantDetails.class);
                            restaurants.add(restaurant);
                        }

                        nearbyPlaceMutableLiveData.setValue(restaurants);

                    }
                });
    }





    public LiveData<List<RestaurantDetails>> getNearbyRestaurantLiveData() {
        return nearbyPlaceMutableLiveData ;
    }



    // Booked Restaurant

    private CollectionReference getBookedRestaurantsCollection(){
        return restaurantRepository.getBookedRestaurantsCollection();
    }

    private void createBookedRestaurantInFirebase(RestaurantDetails restaurant) {
        restaurantRepository.createBookedRestaurantInFirebase(restaurant);
    }

    public RestaurantDetails getRestaurantOfCurrentUser() {
        if (userRepository.getCurrentUser() != null) {
            return userRepository.getCurrentUser().getBookedRestaurant();
        }
        return null;
    }

    public void bookedThisRestaurant(RestaurantDetails restaurant) {

        if (userRepository.getCurrentUser().getBookedRestaurant() != null) {
            cancelBookedRestaurant(userRepository.getCurrentUser().getBookedRestaurant());
        }


        restaurant.getWorkmatesId().add(userRepository.getCurrentUser().getUid());
        createBookedRestaurantInFirebase(restaurant);

        userRepository.bookedRestaurant(restaurant);
    }

    public void cancelBookedRestaurant(RestaurantDetails restaurant) {

        userRepository.cancelRestaurant();
        restaurant.getWorkmatesId().remove(userRepository.getCurrentUser().getUid());

        getBookedRestaurantsCollection().document(restaurant.getPlaceId()).set(restaurant);
        if (!restaurant.isBooked()) {
            getBookedRestaurantsCollection().document(restaurant.getPlaceId()).delete();
        }

        setBookedRestaurantList();
    }

    public void setBookedRestaurantList() {
        List<RestaurantDetails> restaurants = new ArrayList<>();
        getBookedRestaurantsCollection().get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot document : list) {
                                RestaurantDetails restaurant = document.toObject(RestaurantDetails.class);
                                restaurants.add(restaurant);
                            }
                        }

                        bookedRestaurantMutableLiveData.setValue(restaurants);

                    }
                });

    }

    public LiveData<List<RestaurantDetails>> getBookedRestaurantLiveData() {
        return bookedRestaurantMutableLiveData;
    }


    // List Restaurant Details

    private void setListRestaurantsDetails(Location userLocation, List<RestaurantDetails> restaurants) {
        List<RestaurantDetails> result = new ArrayList<>();

        if (restaurants != null) {
            for (RestaurantDetails restaurant : restaurants) {

                String placeId = restaurant.getPlaceId();

                restaurantRepository.getPlaceDetails(placeId).enqueue(new Callback<PlaceDetails>() {
                    @Override
                    public void onResponse(Call<PlaceDetails> call, Response<PlaceDetails> response) {
                        result.add(response.body().getResult());

                        if (result.size() == restaurants.size()) {

                            createNearbyRestaurantInFirebase(userLocation, result);
                            initNearbyPlaceLiveData(userLocation);
                        }
                    }

                    @Override
                    public void onFailure(Call<PlaceDetails> call, Throwable t) {

                    }
                });
            }
        }

    }

}
