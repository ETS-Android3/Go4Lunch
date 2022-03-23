package com.tonyocallimoutou.go4lunch.ui.mapview;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.model.PlacesSearchResult;
import com.tonyocallimoutou.go4lunch.R;
import com.tonyocallimoutou.go4lunch.Retrofit.PlaceDetail;
import com.tonyocallimoutou.go4lunch.model.Restaurant;
import com.tonyocallimoutou.go4lunch.utils.GetListFromFirestore;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelFactory;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapViewFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, OnCompleteListener<Location> {

    @BindView(R.id.message_map_view)
    LinearLayout message_map_view;
    @BindView(R.id.fab_map_view)
    FloatingActionButton fabMap;

    SupportMapFragment mapFragment;
    GoogleMap mGoogleMap;
    CameraPosition cameraPosition;
    View locationButton;

    ViewModelUser viewModel;

    PlacesClient placesClient;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location userLocation;

    PlaceDetail listPlace;


    // Bundle
    private final String KEY_LOCATION = "KEY_LOCATION";
    private final String KEY_CAMERA_POSITION = "KEY_CAMERA_POSITION";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map_view, container, false);
        ButterKnife.bind(this, view);

        viewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(ViewModelUser.class);

        fabMap.setVisibility(View.INVISIBLE);

        // INIT PLACE
        Places.initialize(getActivity().getApplicationContext(), getString(R.string.map_api_key));
        placesClient = Places.createClient(getContext());
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // BUNDLE
        if (savedInstanceState != null) {
            userLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            initFragment();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (mGoogleMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mGoogleMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, userLocation);
        }
        super.onSaveInstanceState(outState);
    }

    // WHEN PERMISSION IS GRANTED

    public void initFragment() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            message_map_view.setVisibility(View.GONE);
            fabMap.setVisibility(View.VISIBLE);
            mapFragment.getMapAsync(this);
        }
    }


    // INIT PERMISSION

    @OnClick(R.id.button_message_map_view)
    public void initPermissionManager() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) == true) {
            showAlertDialog();
        } else {
            askForPermission();
        }
    }

    public void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle(R.string.title_alertDialog_permission);
        builder.setMessage(R.string.message_alertDialog_permission);
        builder.setCancelable(false);

        builder.setPositiveButton(getContext().getResources().getString(R.string.positive_button_alertDialog_permission), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                askForPermission();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void askForPermission() {
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    initFragment();
                }
            });


    // ON MAP READY

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getActivity());

        // Camera
        if (mGoogleMap == null) {
            getDeviceLocation();
        } else {
            if (cameraPosition != null) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraPosition.target, cameraPosition.zoom));
            }
        }

        mGoogleMap = googleMap;

        mGoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(),R.raw.style_json));


        // Click on Marker = Restaurant
        addMarkerOnPlace(listPlace);
        mGoogleMap.setOnMarkerClickListener(this);

        // User
        initUserLocationGoogleMap();

    }

    // GET DEVICE LOCATION
    @SuppressLint("MissingPermission")
    public void getDeviceLocation() {
        Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();

        locationResult.addOnCompleteListener(this);
    }

    @Override
    public void onComplete(@NonNull Task<Location> task) {
        if (task.isSuccessful()) {
            // Set the map's camera position to the current location of the device.
            userLocation = task.getResult();
            if (userLocation != null) {
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(userLocation.getLatitude(),
                                userLocation.getLongitude()), 15));

                initMarkerRestaurant();
            }
        }
    }

    // GET USER LOCATION

    @SuppressLint({"MissingPermission", "ResourceType"})
    public void initUserLocationGoogleMap() {
        mGoogleMap.setMyLocationEnabled(true);
        locationButton = mapFragment.getView().findViewById(0x2);
        if (locationButton != null) {
            locationButton.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.fab_map_view)
    public void cameraOnLocation() {
        locationButton.callOnClick();
    }


    // Restaurant

    public void initMarkerRestaurant() {
        Log.d("TAG", "initMarkerRestaurant: ");
        String userPosition = userLocation.getLatitude() + "," + userLocation.getLongitude();
        viewModel.getNearbyPlace(userPosition);

        final Observer<PlaceDetail> observerPlaceDetail = new Observer<PlaceDetail>() {
            @Override
            public void onChanged(PlaceDetail placeDetail) {
                listPlace = placeDetail;
            }
        };

        viewModel.getPlacesLiveData().observe(this, observerPlaceDetail);

    }

    public void addMarkerOnPlace(PlaceDetail placeDetail) {
        if (placeDetail != null) {
            for (int i=0; i<placeDetail.getResults().size(); i++) {
                Double lat = placeDetail.getResults().get(i).getGeometry().getLocation().getLat();
                Double lng = placeDetail.getResults().get(i).getGeometry().getLocation().getLng();
                String placeName = placeDetail.getResults().get(i).getName();
                String vicinity = placeDetail.getResults().get(i).getVicinity();
                String id = placeDetail.getResults().get(i).getPlaceId();
                LatLng latLng = new LatLng(lat, lng);


                mGoogleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(placeName + " : " + vicinity))
                        .setTag(id);

                Log.d("TAG", "addMarkerOnPlace: ");
            }
        }
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        Toast.makeText(getContext(), "Clicked: " +
                        marker.getTitle() + "\nPlace ID:" + marker.getId(),
                Toast.LENGTH_SHORT).show();

        return true;
    }
}