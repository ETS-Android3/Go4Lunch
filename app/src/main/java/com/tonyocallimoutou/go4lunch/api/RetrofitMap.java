package com.tonyocallimoutou.go4lunch.api;

import com.tonyocallimoutou.go4lunch.BuildConfig;
import com.tonyocallimoutou.go4lunch.model.places.details.PlaceDetails;
import com.tonyocallimoutou.go4lunch.model.places.nearby.NearbyPlace;
import com.tonyocallimoutou.go4lunch.model.places.search.SearchPlace;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitMap {
    /*
     * Retrofit get annotation with our URL
     * And our method that will return us details of student.
     */
    @GET("nearbysearch/json?sensor=true&rankby=distance&type=restaurant&key="+BuildConfig.PLACES_API_KEY)
    Call<NearbyPlace> getNearbyPlaces(@Query("location") String location);

    @GET("details/json?key="+BuildConfig.PLACES_API_KEY)
    Call<PlaceDetails> getPlaceDetails(@Query("place_id") String placeId);

    @GET("autocomplete/json?type=establishment&key="+BuildConfig.PLACES_API_KEY)
    Call<SearchPlace> getSearchPlace(@Query("location") String location, @Query("input") String input);


    public static final String baseUrl = "https://maps.googleapis.com/maps/api/place/";
    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
