package com.tonyocallimoutou.go4lunch.ui.setting;

import android.content.Intent;
import android.os.Bundle;

import androidx.lifecycle.ViewModelProvider;

import com.tonyocallimoutou.go4lunch.R;
import com.tonyocallimoutou.go4lunch.ui.BaseActivity;
import com.tonyocallimoutou.go4lunch.ui.MainActivity;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelFactory;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelUser;

public class SettingActivity extends BaseActivity {

    ViewModelUser viewModelUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModelUser = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(ViewModelUser.class);
        setContentView(R.layout.activity_setting);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}