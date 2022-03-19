package com.tonyocallimoutou.go4lunch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.search.MapboxSearchSdk;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelFactory;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelUser;

import java.util.Arrays;
import java.util.List;

public class LocationPermissionActivity extends AppCompatActivity implements PermissionsListener, LocationListener  {

    private ViewModelUser viewModel;
    private static PermissionsManager permissionsManager;
    private static LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_permission);

        MapboxSearchSdk.initialize(this.getApplication(), getString(R.string.mapbox_access_token), LocationEngineProvider.getBestLocationEngine(this));


        viewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(ViewModelUser.class);

        initStartActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.createUser();

    }

    // INIT
    private void initStartActivity() {

        if (!viewModel.isCurrentLogged()) {
            startSignInActivity();
        }
        else {
            initLocationPermission();
        }
    }

    // INIT SIGN IN

    private void startSignInActivity() {
        List<AuthUI.IdpConfig> provider = Arrays.asList(
                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        startActivity(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(provider)
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.logo)
                        .build()
        );

        initLocationPermission();
    }

    // Localisation Permission

    public void initLocationPermission() {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Permission sensitive logic called here
            Log.d("TAG", "initLocalisationPermission: OK");

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                showAlertDialog(this);
            }
            else {
                initLocation();
                startMainActivity();
            }

        } else {
            Log.d("TAG", "initLocalisationPermission: Demande");

            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] < 0) {
            Log.d("TAG", "onRequestPermissionsResult: SnackBar");
        }
    }

    @Override
    public void onExplanationNeeded(List<String> list) {
        Log.d("TAG", "onExplanationNeeded: MapFrag" + list);
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            // Permission sensitive logic called here
            Log.d("TAG", "onPermissionResult: MapFrag1");
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                showAlertDialog(this);
            }
            else {
                initLocation();
                startMainActivity();
            }
        } else {
            // User denied the permission
            Log.d("TAG", "onPermissionResult: MapFrag2");
            showAlertDialog(this);

        }
    }

    public static void showAlertDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(R.string.title_alertDialog_permission);
        builder.setMessage(R.string.message_alertDialog_permission);
        builder.setCancelable(false);

        builder.setPositiveButton(context.getResources().getString(R.string.positive_button_alertDialog_permission), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                intent.setData(uri);
                context.startActivity(intent);
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    // START MAIN ACTIVITY

    public void startMainActivity() {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // INIT LOCATION

    public void initLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    public static Location getUserLocation(Context context) {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        else {
            Location userLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (userLocation == null) {
                LocationEngine locationEngine = LocationEngineProvider.getBestLocationEngine(context);
            }
            return userLocation;
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

}