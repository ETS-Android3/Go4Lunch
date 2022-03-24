package com.tonyocallimoutou.go4lunch.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.CollectionReference;
import com.tonyocallimoutou.go4lunch.Retrofit.NearByPlace;
import com.tonyocallimoutou.go4lunch.Retrofit.RetrofitMap;
import com.tonyocallimoutou.go4lunch.repository.RestaurantRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ViewModelRestaurant extends ViewModel {

    public RestaurantRepository restaurantRepository;

    private MutableLiveData<NearByPlace> placesDetailLiveData = new MutableLiveData<>();

    private String baseUrl = "https://maps.googleapis.com/maps/api/place/";

    public Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build();


    public ViewModelRestaurant() {
        restaurantRepository = RestaurantRepository.getInstance(retrofit.create(RetrofitMap.class));
    }


    // Restaurant

    public void createRestaurant(String id, String name) {
        restaurantRepository.createRestaurant(id,name);
    }

    public CollectionReference getRestaurantsCollection(){
        return restaurantRepository.getRestaurantsCollection();
    }

    public LiveData<NearByPlace> getPlacesLiveData() {
        return placesDetailLiveData;
    }

    public void getNearByPlace(String location) {
        restaurantRepository.getNearByPlace(location).enqueue(new Callback<NearByPlace>() {
            @Override
            public void onResponse(Call<NearByPlace> call, Response<NearByPlace> response) {
                Log.d("TAG", "onResponse: ");
                placesDetailLiveData.setValue(response.body());
            }

            @Override
            public void onFailure(Call<NearByPlace> call, Throwable t) {
                Log.d("TAG", "onFailure: ");
            }
        });
    }
}
