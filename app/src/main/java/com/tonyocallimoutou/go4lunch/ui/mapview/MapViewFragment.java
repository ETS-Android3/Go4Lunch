package com.tonyocallimoutou.go4lunch.ui.mapview;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.maps.CameraOptions;
import com.mapbox.search.CategorySearchEngine;
import com.mapbox.search.CategorySearchOptions;
import com.mapbox.search.MapboxSearchSdk;
import com.mapbox.search.ResponseInfo;
import com.mapbox.search.SearchCallback;
import com.mapbox.search.SearchRequestTask;
import com.mapbox.search.result.SearchAddress;
import com.mapbox.search.result.SearchResult;
import com.tonyocallimoutou.go4lunch.LocationPermissionActivity;
import com.tonyocallimoutou.go4lunch.MainActivity;
import com.tonyocallimoutou.go4lunch.R;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelFactory;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelUser;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapViewFragment extends Fragment implements OnMapReadyCallback {

    @BindView(R.id.mapView)
    MapView mapView;

    MapboxMap mMapboxMap;
    com.mapbox.mapboxsdk.maps.Style mStyle;

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
        Mapbox.getInstance(getContext(),getString(R.string.mapbox_access_token));

        MapboxSearchSdk.initialize(getActivity().getApplication(),getString(R.string.mapbox_access_token));
        viewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(ViewModelUser.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map_view, container, false);

        ButterKnife.bind(this,view);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
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
        mapView.onResume();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void initLocalisationCamera() {

        Location location = LocationPermissionActivity.getUserLocation(getContext());

        LatLng userLocation = new LatLng();

        userLocation.setLongitude(location.getLongitude());
        userLocation.setLatitude(location.getLatitude());

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(userLocation)
                .zoom(15.0)
                .build();

        mMapboxMap.setCameraPosition(cameraPosition);

        initSymbol(mStyle);
    }


    @OnClick (R.id.fab_map_view)
    public void initCamera() {
        initLocalisationCamera();
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {

        mMapboxMap = mapboxMap;

        mapboxMap.setStyle(Style.MAPBOX_STREETS, new com.mapbox.mapboxsdk.maps.Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull com.mapbox.mapboxsdk.maps.Style style) {

                mStyle = style;

                initLocalisationCamera();
                initDataRestaurant();
            }
        });
    }


    public void initDataRestaurant() {
        categorySearchEngine = MapboxSearchSdk.getCategorySearchEngine();

        Location location = LocationPermissionActivity.getUserLocation(getContext());

        CategorySearchOptions categorySearchOptions = new CategorySearchOptions.Builder()
                .proximity(Point.fromLngLat(location.getLongitude(), location.getLatitude()))
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

    public void initSymbol(@NonNull com.mapbox.mapboxsdk.maps.Style style ) {

        SymbolManager symbolManager = new SymbolManager(mapView, mMapboxMap, style);

        Log.d("TAG", "initSymbol: ");

        symbolManager.setIconAllowOverlap(true);
        symbolManager.setTextAllowOverlap(true);

        Location location = LocationPermissionActivity.getUserLocation(getContext());

        SymbolOptions symbolOptions = new SymbolOptions()
                .withLatLng(new LatLng(location.getLatitude(), location.getLongitude()))
                .withIconSize(1.3f);

        symbolManager.create(symbolOptions);
    }
}