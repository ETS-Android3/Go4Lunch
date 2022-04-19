package com.tonyocallimoutou.go4lunch.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import androidx.preference.PreferenceManager;

import com.tonyocallimoutou.go4lunch.R;

import java.util.Locale;

public class LocaleHelper {

    public static ContextWrapper setLocale(Context context) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String defaultLanguage = sharedPreferences.getString(context.getString(R.string.shared_preference_language),context.getResources().getConfiguration().locale.getDisplayCountry());

        Configuration config = context.getResources().getConfiguration();

        config.locale = new Locale(defaultLanguage);

        ContextWrapper wrapper = new ContextWrapper(context.createConfigurationContext(config));

        return wrapper;
    }
}
