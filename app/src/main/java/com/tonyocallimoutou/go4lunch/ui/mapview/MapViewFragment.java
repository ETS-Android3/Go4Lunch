package com.tonyocallimoutou.go4lunch.ui.mapview;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener;
import com.mapbox.search.ApiType;
import com.mapbox.search.CategorySearchEngine;
import com.mapbox.search.CategorySearchOptions;
import com.mapbox.search.MapboxSearchSdk;
import com.mapbox.search.ResponseInfo;
import com.mapbox.search.SearchCallback;
import com.mapbox.search.SearchRequestTask;
import com.mapbox.search.result.SearchAddress;
import com.mapbox.search.result.SearchResult;
import com.tonyocallimoutou.go4lunch.MainActivity;
import com.tonyocallimoutou.go4lunch.R;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelFactory;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelUser;

import java.util.List;
import java.util.concurrent.Executor;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapViewFragment extends Fragment {

    @BindView(R.id.mapView)
    MapView mapView;

    CameraOptions cameraOptions;
    ViewModelUser viewModel;

    SearchRequestTask searchRequestTask;
    CategorySearchEngine categorySearchEngine;

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
        viewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(ViewModelUser.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map_view, container, false);
        ButterKnife.bind(this,view);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (cameraOptions != null) {
            initCamera();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void initLocalisation(Location location) {

        double positionLat = location.getLatitude();
        double positionLong = location.getLongitude();

        cameraOptions = new CameraOptions.Builder()
                .center(Point.fromLngLat(positionLong, positionLat))
                .zoom(15.0)
                .build();

        mapView.getMapboxMap().setCamera(cameraOptions);
    }


    @OnClick (R.id.fab_map_view)
    public void initCamera() {
        if (MainActivity.getUserLocation(getContext()) != null) {
            initLocalisation(MainActivity.getUserLocation(getContext()));
        }
        initDataRestaurant();
    }

    public void initDataRestaurant() {
        categorySearchEngine = MapboxSearchSdk.getCategorySearchEngine();

        CategorySearchOptions categorySearchOptions = new CategorySearchOptions.Builder()
                .limit(5)
                .build();

        searchRequestTask = categorySearchEngine.search("restaurant", categorySearchOptions, searchCallback);

    }

    private final SearchCallback searchCallback = new SearchCallback() {
        @Override
        public void onResults(@NonNull List<? extends SearchResult> list, @NonNull ResponseInfo responseInfo) {
            if (list.isEmpty()) {
                Log.i("SearchApiExample", "No category search results");
            } else {
                for (int i=0; i<list.size() ; i++) {
                    String id = list.get(i).getId();
                    String name = list.get(i).getName();
                    double distance;
                    if (list.get(i).getDistanceMeters() != null) {
                        distance = list.get(i).getDistanceMeters();
                    }
                    else {
                        distance = 0;
                    }

                    List<String> categories = list.get(i).getCategories();
                    SearchAddress address = list.get(i).getAddress();
                    viewModel.createRestaurant(id,name,distance,categories,address );
                }
            }
        }

        @Override
        public void onError(@NonNull Exception e) {
            Log.i("SearchApiExample", "Search error", e);
        }
    };

}