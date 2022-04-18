package com.tonyocallimoutou.go4lunch;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.DropDownPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelRestaurant;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelUser;

public class SettingFragment extends PreferenceFragmentCompat implements SettingTimePickerFragment.TimeDialogListener {

    private EditTextPreference namePreference;
    private ListPreference languagePreference;
    private Preference notificationRestaurant;
    private Preference notificationNoRestaurant;
    private Preference removeAccount;

    private SharedPreferences sharedPreferences;
    private ViewModelUser viewModelUser;

    private String name;
    private int hourNoRestaurant;
    private int minutesNoRestaurant;
    private int hourRestaurant;
    private int minutesRestaurant;
    private String defaultLanguage;


    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.preferences,rootKey);
        viewModelUser = new ViewModelProvider(requireActivity()).get(ViewModelUser.class);
        initInformation();
        setPreferences();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initInformation() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        name = sharedPreferences.getString(getString(R.string.shared_preference_username),"");
        hourRestaurant = sharedPreferences.getInt(getString(R.string.shared_preference_hour_restaurant),12);
        minutesRestaurant = sharedPreferences.getInt(getString(R.string.shared_preference_minutes_restaurant),0);
        hourNoRestaurant = sharedPreferences.getInt(getString(R.string.shared_preference_hour_no_restaurant),11);
        minutesNoRestaurant = sharedPreferences.getInt(getString(R.string.shared_preference_minutes_no_restaurant),0);
        defaultLanguage = sharedPreferences.getString(getString(R.string.shared_preference_language),null);


        namePreference = findPreference(getString(R.string.preferences_name));
        namePreference.setSummary(getString(R.string.preferences_your_actual_name)+ " " + name);
        namePreference.setText(name);

        languagePreference = findPreference(getString(R.string.preferences_languages));
        languagePreference.setSummary(defaultLanguage);
        languagePreference.setValue(defaultLanguage);

        notificationRestaurant = findPreference(getString(R.string.preferences_notification_restaurant));
        String timeStr = hourToString(hourRestaurant,minutesRestaurant);
        notificationRestaurant.setSummary(timeStr);
        notificationNoRestaurant = findPreference(getString(R.string.preferences_notification_no_restaurant));
        timeStr = hourToString(hourNoRestaurant,minutesNoRestaurant);
        notificationNoRestaurant.setSummary(timeStr);

        removeAccount = findPreference(getString(R.string.preferences_remove_account));
    }

    private void setPreferences() {
        namePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                Log.d("TAG", "onPreferenceChange: " + newValue);
                sharedPreferences
                        .edit()
                        .putString(getString(R.string.shared_preference_username), (String) newValue)
                        .apply();

                viewModelUser.setNameOfCurrentUser((String) newValue);

                initInformation();
                return false;
            }
        });

        languagePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                Log.d("TAG", "onPreferenceChange: " + newValue);
                sharedPreferences
                        .edit()
                        .putString(getString(R.string.shared_preference_language), (String) newValue)
                        .apply();

                initInformation();
                return false;
            }
        });

        notificationRestaurant.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                SettingTimePickerFragment dialog = new SettingTimePickerFragment(SettingFragment.this, getContext(),SettingTimePickerFragment.RESTAURANT);
                dialog.show(getActivity().getSupportFragmentManager(), null);
                return false;
            }
        });

        notificationNoRestaurant.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                SettingTimePickerFragment dialog = new SettingTimePickerFragment(SettingFragment.this, getContext(),SettingTimePickerFragment.NO_RESTAURANT);
                dialog.show(getActivity().getSupportFragmentManager(), null);
                return false;
            }
        });

        removeAccount.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                alertDialogRemoveAccount();
                return false;
            }
        });
    }

    public void alertDialogRemoveAccount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle(R.string.setting_account_title_alert);
        builder.setMessage(R.string.setting_account_message_alert);

        builder.setPositiveButton(getContext().getResources().getString(R.string.setting_account_button_alert), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                viewModelUser.deleteUser(getContext()).addOnCompleteListener(task -> {
                    getActivity().finish();
                });
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onFinishDialog(String type, int hour, int minutes) {

        if (type.equals(SettingTimePickerFragment.RESTAURANT)) {
            sharedPreferences
                    .edit()
                    .putInt(getString(R.string.shared_preference_hour_restaurant),hour)
                    .putInt(getString(R.string.shared_preference_minutes_restaurant),minutes)
                    .apply();
        }
        else if (type.equals(SettingTimePickerFragment.NO_RESTAURANT)) {
            sharedPreferences
                    .edit()
                    .putInt(getString(R.string.shared_preference_hour_no_restaurant),hour)
                    .putInt(getString(R.string.shared_preference_minutes_no_restaurant),minutes)
                    .apply();
        }
        else {
            Log.w("TAG", "onFinishDialog: ");
        }

        String timeStr = hourToString(hour,minutes);
        String toast = "New alarm at " + timeStr;
        Toast.makeText(getContext(), toast, Toast.LENGTH_SHORT).show();

        initInformation();

    }

    public String hourToString(int hour, int minutes) {

        String hourStr = "" + hour;
        String minuteStr = "" + minutes;
        if (hour < 10) {
            hourStr = "0"+hour;
        }
        if (minutes < 10) {
            minuteStr = "0"+minutes;
        }

        return hourStr + ":" + minuteStr;
    }
}
