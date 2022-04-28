package com.tonyocallimoutou.go4lunch.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.tonyocallimoutou.go4lunch.R;
import com.tonyocallimoutou.go4lunch.ui.autocomplete.AutocompleteFragment;
import com.tonyocallimoutou.go4lunch.ui.chat.ChatActivity;
import com.tonyocallimoutou.go4lunch.utils.LocaleHelper;
import com.tonyocallimoutou.go4lunch.utils.UtilStatusConnection;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isDarkTheme = sharedPreferences.getBoolean(getString(R.string.shared_preference_theme), false);
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }


        UtilStatusConnection statusConnection = new ViewModelProvider(this).get(UtilStatusConnection.class);

        statusConnection.getConnected().observe(this, isConnected -> {
            AutocompleteFragment.initWithConnection(isConnected);
            ChatActivity.initConnection(isConnected);
        });
    }

}
