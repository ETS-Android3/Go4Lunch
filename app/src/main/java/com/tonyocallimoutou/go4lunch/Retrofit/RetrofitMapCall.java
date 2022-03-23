package com.tonyocallimoutou.go4lunch.Retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitMapCall {

    private static String baseUrl = "https://maps.googleapis.com/maps/api/place/";

    public static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
