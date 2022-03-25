package com.tonyocallimoutou.go4lunch.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.tonyocallimoutou.go4lunch.api.RetrofitMap;
import com.tonyocallimoutou.go4lunch.repository.RestaurantRepository;
import com.tonyocallimoutou.go4lunch.repository.UserRepository;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private static ViewModelFactory factory;

    private RestaurantRepository restaurantRepository;
    private UserRepository userRepository;


    public ViewModelFactory() {
        restaurantRepository = RestaurantRepository.getInstance(RetrofitMap.retrofit.create(RetrofitMap.class));
        userRepository = UserRepository.getInstance();
    }

    public static ViewModelFactory getInstance() {
        if (factory == null) {
            synchronized (ViewModelFactory.class) {
                if (factory == null) {
                    factory = new ViewModelFactory();
                }
            }
        }
        return factory;
    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

        if (modelClass.isAssignableFrom(ViewModelUser.class)) {
            return (T) new ViewModelUser(userRepository);
        }
        else if (modelClass.isAssignableFrom(ViewModelRestaurant.class))
            return (T) new ViewModelRestaurant(restaurantRepository);

        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
