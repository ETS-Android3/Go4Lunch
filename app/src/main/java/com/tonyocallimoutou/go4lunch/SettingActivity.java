package com.tonyocallimoutou.go4lunch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.tonyocallimoutou.go4lunch.utils.UtilData;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelFactory;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends AppCompatActivity {

    @BindView(R.id.setting_spinner_language)
    Spinner spinnerLanguage;
    @BindView(R.id.setting_spinner_hours_no_restaurant)
    Spinner spinnerHoursNoRestaurant;
    @BindView(R.id.setting_spinner_minutes_no_restaurant)
    Spinner spinnerMinutesNoRestaurant;
    @BindView(R.id.setting_spinner_hours_restaurant)
    Spinner spinnerHoursRestaurant;
    @BindView(R.id.setting_spinner_minutes_restaurant)
    Spinner spinnerMinutesRestaurant;


    private ViewModelUser viewModelUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        viewModelUser = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(ViewModelUser.class);
        initSpinnerLanguage();
        initSpinnerRestaurant();
    }

    public void initSpinnerLanguage() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                com.facebook.R.layout.support_simple_spinner_dropdown_item,
                UtilData.getLanguage());

        spinnerLanguage.setAdapter(adapter);

        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("TAG", "onItemSelected: ");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void initSpinnerRestaurant(){
        List<Integer> listHours = new ArrayList<>();
        for (int i=0; i<24; i++) {
            listHours.add(i);
        }

        List<Integer> listMinutes = new ArrayList<>();
        for (int i=0; i<60; i++) {
            listMinutes.add(i);
        }

        ArrayAdapter<Integer> adapterHours = new ArrayAdapter<>(this,
                com.facebook.R.layout.support_simple_spinner_dropdown_item,
                listHours);
        ArrayAdapter<Integer> adapterMinutes = new ArrayAdapter<>(this,
                com.facebook.R.layout.support_simple_spinner_dropdown_item,
                listMinutes);

        spinnerHoursNoRestaurant.setAdapter(adapterHours);
        spinnerMinutesNoRestaurant.setAdapter(adapterMinutes);
        spinnerHoursRestaurant.setAdapter(adapterHours);
        spinnerMinutesRestaurant.setAdapter(adapterMinutes);

        spinnerHoursRestaurant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("TAG", "onItemSelected: ");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerMinutesRestaurant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("TAG", "onItemSelected: ");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerHoursNoRestaurant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("TAG", "onItemSelected: ");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerMinutesNoRestaurant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("TAG", "onItemSelected: ");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @OnClick(R.id.setting_remove_account)
    public void setAccount() {
        Log.d("TAG", "setAccount: ");
        showAlertDialogForLogout();
    }


    public void showAlertDialogForLogout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.setting_account_title_alert);
        builder.setMessage(R.string.setting_account_message_alert);

        builder.setPositiveButton(getString(R.string.setting_account_button_alert), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                viewModelUser.deleteUser(SettingActivity.this);
                Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }



}