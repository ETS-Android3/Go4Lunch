package com.tonyocallimoutou.go4lunch.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.tonyocallimoutou.go4lunch.api.RetrofitMap;
import com.tonyocallimoutou.go4lunch.repository.ChatRepository;
import com.tonyocallimoutou.go4lunch.repository.RestaurantRepository;
import com.tonyocallimoutou.go4lunch.repository.UserRepository;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private static UserRepository userRepository;

    private static RestaurantRepository restaurantRepository;

    private static ChatRepository chatRepository;

    private static ViewModelFactory factory;

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

    private ViewModelFactory() {

        userRepository = UserRepository.getInstance();

        restaurantRepository = RestaurantRepository.getInstance(RetrofitMap.retrofit.create(RetrofitMap.class));

        chatRepository = ChatRepository.getInstance();

    }

    public static void InstrumentedTestInitFactory(UserRepository mockUser, RestaurantRepository mockRestaurant, ChatRepository mockChat) {
        if (factory == null) {
            factory = new ViewModelFactory();
        }
        userRepository = mockUser;
        restaurantRepository = mockRestaurant;
        chatRepository = mockChat;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        Log.d("TAG", "create: ");

        if (modelClass.isAssignableFrom(ViewModelUser.class)) {

            return (T) new ViewModelUser(userRepository, restaurantRepository);
        }
        else if (modelClass.isAssignableFrom(ViewModelRestaurant.class)) {

            return (T) new ViewModelRestaurant(restaurantRepository, userRepository);
        }
        else if (modelClass.isAssignableFrom(ViewModelChat.class)) {

            return (T) new ViewModelChat(chatRepository, userRepository);
        }

        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
