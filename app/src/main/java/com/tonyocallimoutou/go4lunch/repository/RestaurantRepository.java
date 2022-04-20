package com.tonyocallimoutou.go4lunch.repository;

import android.location.Location;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.tonyocallimoutou.go4lunch.BuildConfig;
import com.tonyocallimoutou.go4lunch.api.RetrofitMap;
import com.tonyocallimoutou.go4lunch.model.User;
import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;
import com.tonyocallimoutou.go4lunch.model.places.details.PlaceDetails;
import com.tonyocallimoutou.go4lunch.model.places.nearby.NearbyPlace;
import com.tonyocallimoutou.go4lunch.model.places.search.Prediction;
import com.tonyocallimoutou.go4lunch.model.places.search.SearchPlace;
import com.tonyocallimoutou.go4lunch.utils.UtilDistance;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantRepository {

    private static final String COLLECTION_NAME_BOOKED_RESTAURANT = "booked_restaurant";

    private static final String COLLECTION_NAME_LOCATION_NEARBY_RESTAURANT = "location_nearby_restaurant";
    private static final String COLLECTION_NAME_NEARBY_RESTAURANT = "nearby_restaurant";

    private static volatile RestaurantRepository instance;
    private RetrofitMap retrofitMap;

    public RestaurantRepository(RetrofitMap retrofitMap) {
        this.retrofitMap = retrofitMap;
    }


    // Instance

    public static RestaurantRepository getInstance(RetrofitMap retrofitMap) {
        RestaurantRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized (RestaurantRepository.class) {
            if (instance == null) {
                instance = new RestaurantRepository(retrofitMap);
            }
            return instance;
        }
    }

    // My Firestore Collection

    private CollectionReference getBookedRestaurantsCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME_BOOKED_RESTAURANT);
    }

    private CollectionReference getLocationNearbyRestaurantsCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME_LOCATION_NEARBY_RESTAURANT);
    }

    private CollectionReference getNearbyRestaurantsCollection(Location userLocation) {
        String location = userLocation.getLatitude() + "," + userLocation.getLongitude();
        return getLocationNearbyRestaurantsCollection().document(location)
                .collection(COLLECTION_NAME_NEARBY_RESTAURANT);
    }


    // Booked Restaurant

    public void createBookedRestaurantInFirebase (RestaurantDetails restaurant) {
        getBookedRestaurantsCollection().document(restaurant.getPlaceId()).set(restaurant);
    }

    public void cancelBookedRestaurantInFirebase (RestaurantDetails restaurant) {
        Log.d("TAG", "cancelBookedRestaurantInFirebase: "+ restaurant.getWorkmatesId());

        if (restaurant.getWorkmatesId().size() == 0) {
            getBookedRestaurantsCollection().document(restaurant.getPlaceId()).delete();
        }
        else {
            getBookedRestaurantsCollection().document(restaurant.getPlaceId()).set(restaurant);
        }

    }

    public void setBookedRestaurantFirestore(MutableLiveData<List<RestaurantDetails>> liveData) {

        getBookedRestaurantsCollection().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    Log.w("TAG", "Listen failed.", error);
                    return;
                }

                List<RestaurantDetails> restaurants = new ArrayList<>();
                for (DocumentSnapshot document : value) {
                    RestaurantDetails restaurant = document.toObject(RestaurantDetails.class);
                    restaurants.add(restaurant);
                }

                liveData.setValue(restaurants);
            }
        });

    }


    // INIT NEARBY PLACE WITH DETAILS

    public void setNearbyPlace(Location userLocation, MutableLiveData<List<RestaurantDetails>> liveData) {

        if (userLocation != null) {

            UtilDistance.roundToNearestFiftyMeters(userLocation);

            List<String> listLocation = new ArrayList<>();

            String location = userLocation.getLatitude() + "," + userLocation.getLongitude();

            getLocationNearbyRestaurantsCollection()
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot document : list) {
                        String location = document.getId();
                        listLocation.add(location);
                    }

                    if (listLocation.contains(location)) {
                        getNearbyRestaurantFirestore(userLocation, liveData);
                    }
                    else {
                        getNearbyPlace(userLocation, liveData);
                    }

                }
            });


        }
    }

    private void getNearbyRestaurantFirestore(Location userLocation, MutableLiveData<List<RestaurantDetails>> liveData) {


        List<RestaurantDetails> restaurants = new ArrayList<>();
        getNearbyRestaurantsCollection(userLocation)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot document : list) {
                            RestaurantDetails restaurant = document.toObject(RestaurantDetails.class);
                            restaurants.add(restaurant);
                        }

                        liveData.setValue(restaurants);
                    }
                });
    }

    private void getNearbyPlace(Location userLocation, MutableLiveData<List<RestaurantDetails>> liveData){

        String location = userLocation.getLatitude() + "," + userLocation.getLongitude();

        retrofitMap.getNearbyPlaces(location).enqueue(new Callback<NearbyPlace>() {
            @Override
            public void onResponse(Call<NearbyPlace> call, Response<NearbyPlace> response) {
                setRestaurantsDetails(userLocation, liveData, response.body().getResults());
            }

            @Override
            public void onFailure(Call<NearbyPlace> call, Throwable t) {

            }
        });
    }

    private void setRestaurantsDetails(Location userLocation,
                                       MutableLiveData<List<RestaurantDetails>> liveData,
                                       List<RestaurantDetails> restaurants) {

        List<RestaurantDetails> result = new ArrayList<>();

        for (RestaurantDetails restaurant : restaurants) {

            retrofitMap.getPlaceDetails(restaurant.getPlaceId()).enqueue(new Callback<PlaceDetails>() {
                @Override
                public void onResponse(Call<PlaceDetails> call, Response<PlaceDetails> response) {
                    RestaurantDetails restaurant = response.body().getResult();
                    setImage(restaurant);
                    restaurant.setRating(0.0);
                    result.add(restaurant);

                    if (result.size() == restaurants.size()) {

                        liveData.setValue(result);
                        createNearbyRestaurantInFirebase(userLocation, result);

                    }
                }

                @Override
                public void onFailure(Call<PlaceDetails> call, Throwable t) {

                }
            });
        }
    }

    private void setImage (RestaurantDetails restaurant) {
        Log.e("ERROR", "APPEL API PHOTO: " );
        if (restaurant.getPhotos() != null) {
            String photoId = restaurant.getPhotos().get(0).getPhotoReference();
            String request = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&maxheight=400&key=" +
                    BuildConfig.PLACES_API_KEY +
                    "&photoreference=" + photoId;

            restaurant.getPhotos().get(0).setImage(request);
        }
    }


    private void createNearbyRestaurantInFirebase (Location userLocation, List<RestaurantDetails> restaurants) {

        String location = userLocation.getLatitude() + "," + userLocation.getLongitude();

        for (RestaurantDetails restaurant : restaurants) {
            getNearbyRestaurantsCollection(userLocation)
                        .document(restaurant.getPlaceId())
                        .set(restaurant);
        }

        getLocationNearbyRestaurantsCollection().document(location).set(userLocation);
    }

    // SEARCH

    public void setSearchRestaurant(Location userLocation, String input, MutableLiveData<List<Prediction>> liveData) {
        if (userLocation != null) {
            String location = userLocation.getLatitude() + "," + userLocation.getLongitude();

            retrofitMap.getSearchPlace(location, input).enqueue(new Callback<SearchPlace>() {
                @Override
                public void onResponse(Call<SearchPlace> call, Response<SearchPlace> response) {
                    List<Prediction> predictions = new ArrayList<>();
                    for (Prediction prediction : response.body().getResults()) {
                        if (prediction.getTypes().contains("food") || prediction.getTypes().contains("restaurant")) {
                            predictions.add(prediction);
                        }
                    }
                    liveData.setValue(predictions);
                }

                @Override
                public void onFailure(Call<SearchPlace> call, Throwable t) {

                }
            });
        }
    }

    // Detail

    public void setDetailForPrediction(Prediction prediction, MutableLiveData<RestaurantDetails> liveData) {
        retrofitMap.getPlaceDetails(prediction.getPlaceId()).enqueue(new Callback<PlaceDetails>() {
            @Override
            public void onResponse(Call<PlaceDetails> call, Response<PlaceDetails> response) {
                RestaurantDetails restaurant = response.body().getResult();
                setImage(restaurant);
                restaurant.setRating(0.0);

                liveData.setValue(restaurant);

            }

            @Override
            public void onFailure(Call<PlaceDetails> call, Throwable t) {

            }
        });
    }

}
