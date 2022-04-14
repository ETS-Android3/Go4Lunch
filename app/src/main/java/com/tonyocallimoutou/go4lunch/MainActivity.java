package com.tonyocallimoutou.go4lunch;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
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
import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;
import com.tonyocallimoutou.go4lunch.ui.autocomplete.AutocompleteFragment;
import com.tonyocallimoutou.go4lunch.ui.detail.DetailsActivity;
import com.tonyocallimoutou.go4lunch.ui.listview.ListViewFragment;
import com.tonyocallimoutou.go4lunch.ui.mapview.MapViewFragment;
import com.tonyocallimoutou.go4lunch.ui.workmates.WorkmatesFragment;
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

    private AutocompleteFragment autocompleteFragment;
    private SearchView searchView;
    private  ViewModelUser viewModelUser;
    private static ViewModelRestaurant viewModelRestaurant;
    private View sideView;
    private ActionBar actionBar;

    private List<RestaurantDetails> nearbyRestaurant = new ArrayList<>();
    private List<RestaurantDetails> bookedRestaurant = new ArrayList<>();

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
                Log.d("TAG", "onOptionsItemSelected: search");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // INIT SEARCH ACTION

    private void initSearch() {

        searchView.setQueryHint(getString(R.string.search_hint_restaurant));
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autocompleteFragment = new AutocompleteFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.host_fragment_autocomplete, autocompleteFragment);
                fragmentTransaction.commit();
                Log.d("TAG", "onClick: ");
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                getSupportFragmentManager()
                        .beginTransaction()
                        .remove(autocompleteFragment)
                        .commit();
                Log.d("TAG", "onClose: ");
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d("TAG", "onQueryTextSubmit: ");
                viewModelRestaurant.setSearchRestaurant(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                return false;
            }
        });
    }

    // INIT BOTTOM NAVIGATION

    private void initBottomNavigationView() {

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
                Log.d("TAG", "onNavigationItemSelected: 2");
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
        viewModelUser.setWorkmatesList();
        viewModelRestaurant.setBookedRestaurantList();
        if (nearbyRestaurant.size() == 0) {
            viewModelRestaurant.setNearbyPlace(null);
        }
        viewModelRestaurant.setSearchRestaurant(null);

        viewModelRestaurant.getBookedRestaurantLiveData().observe(this, restaurantsResults -> {
            bookedRestaurant.clear();
            bookedRestaurant.addAll(restaurantsResults);
            ListViewFragment.setBookedRestaurant(restaurantsResults);
            MapViewFragment.setBookedRestaurant(restaurantsResults);
        });

        viewModelUser.getWorkmates().observe(this, workmates -> {
            WorkmatesFragment.setWorkmates(workmates);
            ListViewFragment.setWorkmates(workmates);
            DetailsActivity.setWorkmates(workmates);
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

        viewModelRestaurant.getPredictionLiveData().observe(this, predictionsResults -> {
            AutocompleteFragment.setPredictions(predictionsResults);
        });
    }
}