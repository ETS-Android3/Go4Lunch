package com.tonyocallimoutou.go4lunch.ui.mapview;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tonyocallimoutou.go4lunch.R;
import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;
import com.tonyocallimoutou.go4lunch.model.places.search.Prediction;
import com.tonyocallimoutou.go4lunch.ui.BaseFragment;
import com.tonyocallimoutou.go4lunch.ui.autocomplete.AutocompleteFragment;
import com.tonyocallimoutou.go4lunch.ui.detail.DetailsActivity;
import com.tonyocallimoutou.go4lunch.utils.CompareRestaurant;
import com.tonyocallimoutou.go4lunch.utils.RestaurantData;
import com.tonyocallimoutou.go4lunch.utils.RestaurantMethod;
import com.tonyocallimoutou.go4lunch.utils.UtilDistance;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelRestaurant;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapViewFragment extends BaseFragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, ViewTreeObserver.OnGlobalLayoutListener {

    @BindView(R.id.message_map_view)
    LinearLayout message_map_view;
    @BindView(R.id.fab_map_view)
    FloatingActionButton fabMap;

    private SupportMapFragment mapFragment;
    private static GoogleMap mGoogleMap;
    private CameraPosition cameraPosition;
    private static float cameraZoomDefault = 15;
    private View locationButton;

    private static ViewModelRestaurant viewModelRestaurant;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private static Location userLocation;


    private static List<RestaurantDetails> nearbyRestaurant = new ArrayList<>();
    private static List<RestaurantDetails> bookedRestaurant = new ArrayList<>();


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

        viewModelRestaurant = new ViewModelProvider(requireActivity()).get(ViewModelRestaurant.class);

        mGoogleMap = null;
        fabMap.setVisibility(View.INVISIBLE);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        return view;
    }

    // BASE FRAGMENT SEARCH

    @Override
    public void doSearch(String s) {
        viewModelRestaurant.setSearchRestaurant(s);
    }

    @Override
    public void onPredictionItemClick(Prediction prediction) {
        super.onPredictionItemClick(prediction);
        viewModelRestaurant.setDetailsRestaurantForPrediction(prediction);
        setPrediction();
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

                if (location != null) {
                    userLocation = location;

                    RestaurantData.newInstanceOfPosition(location);

                    viewModelRestaurant.setNearbyPlace(userLocation);

                    if (mGoogleMap == null) {
                        mapFragment.getMapAsync(MapViewFragment.this);
                    }
                }
                else {
                    showAlertDialogErrorLocation();
                }
            }
        });
    }

    public void showAlertDialogErrorLocation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle(R.string.title_alertDialog_error_location);
        builder.setMessage(R.string.message_alertDialog_error_location);
        builder.setCancelable(false);

        builder.setPositiveButton(getContext().getResources().getString(R.string.positive_button_alertDialog_permission), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getActivity().finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }



    // ON MAP READY

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getActivity());

        // Camera
        if (cameraPosition != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraPosition.target, cameraPosition.zoom));
        }

        mGoogleMap = googleMap;

        mGoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(),R.raw.style_json));

        SetupForUserLocation();
        mGoogleMap.setOnMarkerClickListener(this);

        mapFragment.getView().getViewTreeObserver().addOnGlobalLayoutListener(this);

    }

    @Override
    public void onGlobalLayout() {
        mapFragment.getView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
        initListForMarker();
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

    private static void initListForMarker() {
        mGoogleMap.clear();

        LatLng latLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, cameraZoomDefault));
        addMarker();
    }

    private static void addMarker() {
        List <RestaurantDetails> nearby = RestaurantMethod.getNearbyRestaurantWithoutBooked(nearbyRestaurant, bookedRestaurant);

        List<MarkerOptions> listMarkers =new ArrayList<>();

        for (RestaurantDetails result : nearby) {
            Double lat = result.getGeometry().getLocation().getLat();
            Double lng = result.getGeometry().getLocation().getLng();
            String placeName = result.getName();
            String vicinity = result.getVicinity();
            LatLng latLng = new LatLng(lat, lng);

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_nearby_restaurant))
                    .title(placeName + " : " + vicinity);

            listMarkers.add(markerOptions);

            mGoogleMap.addMarker(markerOptions)
                    .setTag(result);
        }

        for (RestaurantDetails result : bookedRestaurant) {

            Double lat = result.getGeometry().getLocation().getLat();
            Double lng = result.getGeometry().getLocation().getLng();
            String placeName = result.getName();
            String vicinity = result.getVicinity();
            LatLng latLng = new LatLng(lat, lng);

            mGoogleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_booked_restaurant))
                    .title(placeName + " : " + vicinity))
                    .setTag(result);

        }

        // ZOOM WITH NEARBY PLACE

        if (nearbyRestaurant.size() != 0) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (MarkerOptions marker : listMarkers) {
                builder.include(marker.getPosition());
            }
            builder.include(new LatLng(userLocation.getLatitude(),userLocation.getLongitude()));
            LatLngBounds bounds = builder.build();


            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 50);
            mGoogleMap.moveCamera(cu);
        }
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {

        RestaurantDetails markRestaurant = (RestaurantDetails) marker.getTag();

        // CAMERA
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        DetailsActivity.navigate(getActivity(),markRestaurant);

        return true;
    }

    // INIT BOOKED LIST
    public static void setBookedRestaurant(List<RestaurantDetails> results) {
        if (! CompareRestaurant.ListRestaurantIsEqual(bookedRestaurant,results)) {
            bookedRestaurant = results;
            if (mGoogleMap != null) {
                initListForMarker();
            }
        }
    }

    public static void setNearbyRestaurant(List<RestaurantDetails> results) {
        if (! CompareRestaurant.ListRestaurantIsEqual(nearbyRestaurant,results)) {
            nearbyRestaurant = results;
            if (mGoogleMap != null) {
                initListForMarker();
            }
        }
    }

    // INIT PREDICTION

    public void setPrediction() {
        viewModelRestaurant.getDetailPrediction().observe(this, restaurant ->  {

            mGoogleMap.clear();

            Double lat = restaurant.getGeometry().getLocation().getLat();
            Double lng = restaurant.getGeometry().getLocation().getLng();
            String placeName = restaurant.getName();
            String vicinity = restaurant.getVicinity();
            LatLng latLng = new LatLng(lat, lng);

            mGoogleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_nearby_restaurant))
                    .title(placeName + " : " + vicinity))
                    .setTag(restaurant);

            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,cameraZoomDefault));
        });

    }
}