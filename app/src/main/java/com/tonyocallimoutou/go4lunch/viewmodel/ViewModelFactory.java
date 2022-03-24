package com.tonyocallimoutou.go4lunch.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private static ViewModelFactory factory;


    public ViewModelFactory() {

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
            return (T) new ViewModelUser();
        }
        else if (modelClass.isAssignableFrom(ViewModelRestaurant.class))
            return (T) new ViewModelRestaurant();

        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
