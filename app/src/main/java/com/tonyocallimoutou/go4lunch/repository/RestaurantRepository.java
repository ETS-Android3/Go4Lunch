package com.tonyocallimoutou.go4lunch.repository;

import android.location.Location;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tonyocallimoutou.go4lunch.BuildConfig;
import com.tonyocallimoutou.go4lunch.model.places.details.PlaceDetails;
import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;
import com.tonyocallimoutou.go4lunch.model.places.nearby.NearbyPlace;
import com.tonyocallimoutou.go4lunch.api.RetrofitMap;

import java.util.List;

import retrofit2.Call;

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

    public CollectionReference getBookedRestaurantsCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME_BOOKED_RESTAURANT);
    }

    public CollectionReference getLocationNearbyRestaurantsCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME_LOCATION_NEARBY_RESTAURANT);
    }

    public CollectionReference getNearbyRestaurantsCollection(String location) {
        return getLocationNearbyRestaurantsCollection().document(location)
                .collection(COLLECTION_NAME_NEARBY_RESTAURANT);
    }


    // Booked Restaurant

    public void createBookedRestaurantInFirebase (RestaurantDetails restaurant) {
        getBookedRestaurantsCollection().document(restaurant.getPlaceId()).set(restaurant);
    }

    // Nearby Restaurant

    public void createNearbyRestaurantInFirebase (Location userLocation, List<RestaurantDetails> restaurants) {

        String location = userLocation.getLatitude() + "," + userLocation.getLongitude();;

        for (RestaurantDetails restaurant : restaurants) {
            getNearbyRestaurantsCollection(location)
                        .document(restaurant.getPlaceId())
                        .set(restaurant);
        }

        getLocationNearbyRestaurantsCollection().document(location).set(userLocation);
    }

    // Retrofit

    public Call<NearbyPlace> getNearbyPlace(String location) {
        Log.e("ERROR", "APPEL API NEARBY: " );
        return retrofitMap.getNearbyPlaces(location);
    }

    public Call<PlaceDetails> getPlaceDetails (String placeId) {
        Log.e("ERROR", "APPEL API DETAIL: " );
        return retrofitMap.getPlaceDetails(placeId);
    }

    public void getImage (RestaurantDetails restaurant) {
        Log.e("ERROR", "APPEL API PHOTO: " );
        if (restaurant.getPhotos() != null) {
            String photoId = restaurant.getPhotos().get(0).getPhotoReference();
            String request = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&maxheight=400&key=" +
                    BuildConfig.PLACES_API_KEY +
                    "&photoreference=" + photoId;

            restaurant.getPhotos().get(0).setImage(request);
        }
    }

}
