package com.tonyocallimoutou.go4lunch.Retrofit;

import com.tonyocallimoutou.go4lunch.BuildConfig;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitMap {
    /*
     * Retrofit get annotation with our URL
     * And our method that will return us details of student.
     */
    @GET("api/place/nearbysearch/json?sensor=true&radius=5000&type=restaurant&key=" + BuildConfig.MAPS_API_KEY)
    Call<PlaceDetail> getNearbyPlaces(@Query("location") String location);

}
