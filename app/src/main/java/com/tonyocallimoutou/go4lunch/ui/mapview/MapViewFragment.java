package com.tonyocallimoutou.go4lunch.ui.mapview;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapInitOptions;
import com.mapbox.maps.MapOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.MapboxMap;
import com.mapbox.maps.ResourceOptions;
import com.mapbox.maps.plugin.Plugin;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener;
import com.tonyocallimoutou.go4lunch.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapViewFragment extends Fragment implements PermissionsListener{

    @BindView(R.id.mapView)
    MapView mapView;

    int LOCATION_REFRESH_TIME = 15000; // 15 seconds to update
    int LOCATION_REFRESH_DISTANCE = 500; // 500 meters to update

    PermissionsManager permissionsManager;
    LocationManager locationManager;

    public MapViewFragment() {
        // Required empty public constructor
    }

    public static MapViewFragment newInstance() {
        MapViewFragment fragment = new MapViewFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map_view, container, false);
        ButterKnife.bind(this,view);


        initLocalisationPermission();

        return view;
    }

    // Localisation Permission

    public void initLocalisationPermission() {
        if (permissionsManager.areLocationPermissionsGranted(getContext())) {
            // Permission sensitive logic called here
            Log.d("TAG", "initLocalisationPermission: MapFragPrincipal");

            locationManager = (LocationManager) getContext().getSystemService(getContext().LOCATION_SERVICE);

            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            initPosition(location);

        }
        else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());
        }
    }

    @Override
    public void onExplanationNeeded(List<String> list) {
        Log.d("TAG", "onExplanationNeeded: MapFrag");
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            // Permission sensitive logic called here
            Log.d("TAG", "onPermissionResult: MapFra1");
        }
        else {
            // User denied the permission
            Log.d("TAG", "onPermissionResult: MapFrag2");
        }
    }

    public void initPosition(Location location) {
        Log.d("TAG", "initPosition: coucou");

        double positionLat = location.getLatitude();
        double positionLong = location.getLongitude();

        CameraOptions cameraOptions = new CameraOptions.Builder()
                .center(Point.fromLngLat(positionLong, positionLat))
                .zoom(15.0)
                .build();

        mapView.getMapboxMap().setCamera(cameraOptions);
    }

    @OnClick (R.id.fab_map_view)
    public void initCamera() {
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        initPosition(location);
    }
}