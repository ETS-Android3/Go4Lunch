package com.tonyocallimoutou.go4lunch.ui.mapview;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tonyocallimoutou.go4lunch.R;
import com.tonyocallimoutou.go4lunch.model.Places.NearbyPlace;
import com.tonyocallimoutou.go4lunch.model.Places.RestaurantsResult;
import com.tonyocallimoutou.go4lunch.repository.RestaurantRepository;
import com.tonyocallimoutou.go4lunch.ui.listview.ListViewRecyclerViewAdapter;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelFactory;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelRestaurant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapViewFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener{

    @BindView(R.id.message_map_view)
    LinearLayout message_map_view;
    @BindView(R.id.fab_map_view)
    FloatingActionButton fabMap;

    SupportMapFragment mapFragment;
    GoogleMap mGoogleMap;
    CameraPosition cameraPosition;
    float cameraZoomDefault = 15;
    View locationButton;

    ViewModelRestaurant viewModel;

    FusedLocationProviderClient fusedLocationProviderClient;
    Location userLocation;


    List<RestaurantsResult> nearbyRestaurant = new ArrayList<>();
    List<RestaurantsResult> bookedRestaurant = new ArrayList<>();


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

        viewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(ViewModelRestaurant.class);

        fabMap.setVisibility(View.INVISIBLE);

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
            getDeviceLocationAndInitMap();
            message_map_view.setVisibility(View.GONE);
            fabMap.setVisibility(View.VISIBLE);
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


    // GET DEVICE LOCATION
    @SuppressLint("MissingPermission")
    public void getDeviceLocationAndInitMap() {
        Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();

        locationResult.addOnSuccessListener(new OnSuccessListener<Location>() {
            @SuppressLint("FragmentLiveDataObserve")
            @Override
            public void onSuccess(Location location) {

                userLocation = location;
                String userPosition = userLocation.getLatitude() + "," + userLocation.getLongitude();

                Log.d("TAG", "user Location: " + userPosition);
                viewModel.setNearbyPlace(userPosition);

                if (mGoogleMap == null) {
                    mapFragment.getMapAsync(MapViewFragment.this);
                }
            }
        });
    }



    // ON MAP READY

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getActivity());

        // Camera
        if (mGoogleMap == null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(userLocation.getLatitude(),
                            userLocation.getLongitude()), cameraZoomDefault));
        } else {
            if (cameraPosition != null) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraPosition.target, cameraPosition.zoom));
            }
        }

        mGoogleMap = googleMap;

        mGoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(),R.raw.style_json));

        SetupForUserLocation();

        // Click on Marker = Restaurant
        viewModel.getNearbyPlaceLiveData().observe(MapViewFragment.this, nearbyPlace -> {
            Log.d("TAG", "OBSERVER : " + nearbyPlace.getResults().size());
            initListForMarker(nearbyPlace);
        });
        mGoogleMap.setOnMarkerClickListener(this);


    }


    // SETUP USER LOCATION

    @SuppressLint({"MissingPermission", "ResourceType"})
    public void SetupForUserLocation() {
        mGoogleMap.setMyLocationEnabled(true);
        locationButton = mapFragment.getView().findViewById(0x2);
        if (locationButton != null) {
            locationButton.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.fab_map_view)
    public void cameraOnLocation() {
        locationButton.callOnClick();
        getDeviceLocationAndInitMap();
    }


    // Restaurant

    public void initListForMarker(NearbyPlace restaurants) {
        mGoogleMap.clear();
        nearbyRestaurant.clear();
        bookedRestaurant.clear();

        nearbyRestaurant = restaurants.getResults();

        viewModel.getBookedRestaurantsCollection().get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot document : list) {
                                RestaurantsResult restaurant = document.toObject(RestaurantsResult.class);
                                bookedRestaurant.add(restaurant);
                            }
                        }

                        addMarkerOnMap(nearbyRestaurant, bookedRestaurant);
                    }
                });
    }

    public void addMarkerOnMap(List<RestaurantsResult> nearbyRestaurant, List<RestaurantsResult> bookedRestaurant) {
        if (nearbyRestaurant != null) {
            Log.d("TAG", "addMarkerOnPlace: Nearby =  "+ nearbyRestaurant.size());
            for (int i=0; i<nearbyRestaurant.size(); i++) {
                if (! bookedRestaurant.contains(nearbyRestaurant.get(i))) {

                    Double lat = nearbyRestaurant.get(i).getGeometry().getLocation().getLat();
                    Double lng = nearbyRestaurant.get(i).getGeometry().getLocation().getLng();
                    String placeName = nearbyRestaurant.get(i).getName();
                    String vicinity = nearbyRestaurant.get(i).getVicinity();
                    String id = nearbyRestaurant.get(i).getPlaceId();
                    LatLng latLng = new LatLng(lat, lng);


                    mGoogleMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(placeName + " : " + vicinity))
                            .setTag(id);
                }
            }
        }

        if (bookedRestaurant != null) {
            Log.d("TAG", "addMarkerOnPlace: Booked =  "+ bookedRestaurant.size());
            for (int i=0; i<bookedRestaurant.size(); i++) {
                Double lat = bookedRestaurant.get(i).getGeometry().getLocation().getLat();
                Double lng = bookedRestaurant.get(i).getGeometry().getLocation().getLng();
                String placeName = bookedRestaurant.get(i).getName();
                String vicinity = bookedRestaurant.get(i).getVicinity();
                String id = bookedRestaurant.get(i).getPlaceId();
                LatLng latLng = new LatLng(lat, lng);


                mGoogleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(placeName + " : " + vicinity))
                        .setTag(id);
            }

        }
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {

        // CAMERA
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),cameraZoomDefault));
        Log.d("TAG", "onMarkerClick: " + marker.getTitle());
        return true;
    }
}