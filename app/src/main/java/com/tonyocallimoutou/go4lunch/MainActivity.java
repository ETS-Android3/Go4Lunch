package com.tonyocallimoutou.go4lunch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;
import com.tonyocallimoutou.go4lunch.model.User;
import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;
import com.tonyocallimoutou.go4lunch.ui.autocomplete.AutocompleteFragment;
import com.tonyocallimoutou.go4lunch.ui.detail.DetailsActivity;
import com.tonyocallimoutou.go4lunch.ui.listview.ListViewFragment;
import com.tonyocallimoutou.go4lunch.ui.mapview.MapViewFragment;
import com.tonyocallimoutou.go4lunch.ui.workmates.WorkmatesFragment;
import com.tonyocallimoutou.go4lunch.utils.UtilNotification;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelFactory;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelRestaurant;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;
    @BindView(R.id.bottom_nav_view)
    BottomNavigationView navigationView;

    private SearchView searchView;
    private ViewModelUser viewModelUser;
    private ViewModelRestaurant viewModelRestaurant;
    private View sideView;
    private ActionBar actionBar;

    private List<RestaurantDetails> nearbyRestaurant = new ArrayList<>();
    private List<RestaurantDetails> bookedRestaurant = new ArrayList<>();
    private List<User> workmates = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModelUser = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(ViewModelUser.class);
        viewModelRestaurant = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(ViewModelRestaurant.class);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initActionBar();
        initBottomNavigationView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (viewModelUser.isCurrentLogged()) {
            viewModelUser.createUser();
            initSideView();
            initData();
        }
        else {
            startSignInActivity();
        }
    }


    // SIGN IN ACTIVITY

    public void startSignInActivity() {
        List<AuthUI.IdpConfig> provider = Arrays.asList(
                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        startActivity(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(provider)
                        .setIsSmartLockEnabled(false,true)
                        .setLogo(R.drawable.logo)
                        .build()
        );
    }


    // INIT ACTION BAR

    private void initActionBar() {
        actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mDrawer.isOpen()) {
                    mDrawer.close();
                } else {
                    mDrawer.open();
                }
                return true;
            case R.id.search_menu:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // INIT BOTTOM NAVIGATION

    private void initBottomNavigationView() {
        BottomNavigationView navigationView = findViewById(R.id.bottom_nav_view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_map, R.id.navigation_list, R.id.navigation_workmates)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    //INIT SIDE VIEW

    private void initSideView() {

        NavigationView nav = findViewById(R.id.side_menu_nav_view);
        nav.setNavigationItemSelectedListener(this);
        sideView = nav.getHeaderView(0);

        FirebaseUser user = viewModelUser.getCurrentFirebaseUser();

        if (user.getPhotoUrl() != null) {
            setProfilePicture(user.getPhotoUrl());
        }
        setTextUser(user);
    }

    private void setProfilePicture(Uri profilePictureUrl) {
        ImageView profilePicture = sideView.findViewById(R.id.profile_picture_header_side_view);

        Glide.with(this)
                .load(profilePictureUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(profilePicture);
    }

    private void setTextUser(FirebaseUser user) {
        TextView email = sideView.findViewById(R.id.user_email);
        TextView name = sideView.findViewById(R.id.user_name);

        email.setText(user.getEmail());
        name.setText(user.getDisplayName());
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.navigation_your_lunch:
                yourLunch();
                break;
            case R.id.navigation_setting:
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.navigation_logout:
                mDrawer.close();
                viewModelUser.signOut(this);
                navigationView.setSelectedItemId(R.id.navigation_map);

                startSignInActivity();

                break;
            default:
                break;
        }
        return true;
    }

    // Your Lunch
    public void yourLunch() {
        RestaurantDetails restaurant = viewModelRestaurant.getRestaurantOfCurrentUser();
        if (restaurant != null) {
            DetailsActivity.navigate(this,restaurant);
        }
        else {
            Snackbar.make(mDrawer, getString(R.string.your_lunch), Snackbar.LENGTH_LONG).show();
        }
    }

    // InitData

    public void initData() {
        viewModelUser.setCurrentUserLiveData();
        viewModelUser.setWorkmatesList();
        viewModelRestaurant.setBookedRestaurantList();
        if (nearbyRestaurant.size() == 0) {
            viewModelRestaurant.setNearbyPlace(null);
        }
        viewModelRestaurant.setSearchRestaurant(null);

        viewModelRestaurant.getBookedRestaurantLiveData().observe(this, restaurantsResults -> {
            bookedRestaurant.clear();
            bookedRestaurant.addAll(restaurantsResults);
            UtilNotification.newInstance(null,restaurantsResults,null,null);
            ListViewFragment.setBookedRestaurant(restaurantsResults);
            MapViewFragment.setBookedRestaurant(restaurantsResults);
        });

        viewModelUser.getWorkmates().observe(this, workmates -> {
            UtilNotification.newInstance(workmates,null,null,null);
            WorkmatesFragment.setWorkmates(workmates);
            ListViewFragment.setWorkmates(workmates);
        });

        viewModelRestaurant.getNearbyRestaurantLiveData().observe(this, restaurantsResults -> {
            for (RestaurantDetails restaurant : restaurantsResults) {
                restaurant.getWorkmatesId().clear();
            }
            nearbyRestaurant.clear();
            nearbyRestaurant.addAll(restaurantsResults);
            ListViewFragment.setNearbyRestaurant(restaurantsResults);
            MapViewFragment.setNearbyRestaurant(restaurantsResults);
        });

        viewModelUser.getCurrentUserLiveData().observe(this, currentUserResults -> {
            if (currentUserResults != null) {
                UtilNotification.newInstance(null,null,this,currentUserResults);
            }
        });

        viewModelRestaurant.getPredictionLiveData().observe(this, predictionsResults -> {
            AutocompleteFragment.setPredictions(predictionsResults);
        });
    }
}