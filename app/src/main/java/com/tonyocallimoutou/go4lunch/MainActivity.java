package com.tonyocallimoutou.go4lunch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tonyocallimoutou.go4lunch.model.Places.RestaurantsResult;
import com.tonyocallimoutou.go4lunch.model.User;
import com.tonyocallimoutou.go4lunch.ui.workmates.WorkmatesRecyclerViewAdapter;
import com.tonyocallimoutou.go4lunch.utils.Data;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelFactory;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelRestaurant;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawer;
    private BottomNavigationView navigationView;
    private ViewModelUser viewModelUser;
    private ViewModelRestaurant viewModelRestaurant;
    private View sideView;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        viewModelUser = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(ViewModelUser.class);
        viewModelRestaurant = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(ViewModelRestaurant.class);
        navigationView = findViewById(R.id.bottom_nav_view);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        return true;
    }

    private void initActionBar() {
        actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        mDrawer = findViewById(R.id.drawer_layout);
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

        FirebaseUser user = viewModelUser.getCurrentUser();

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
                Log.d("TAG", "onNavigationItemSelected: 1");
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

    // InitData

    public void initData() {

        List<User> workmatesList = new ArrayList<>();
        List<RestaurantsResult> restaurantsResults = new ArrayList<>();

        viewModelUser.getUsersCollection().get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot document : list) {
                                User user = document.toObject(User.class);

                                if (!user.getUid().equals(viewModelUser.getCurrentUser().getUid())) {
                                    workmatesList.add(user);
                                }
                            }

                        }

                        Data.newInstanceOfWorkmates(workmatesList);
                    }
                });

        viewModelRestaurant.getBookedRestaurantsCollection().get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot document : list) {
                                RestaurantsResult restaurant = document.toObject(RestaurantsResult.class);
                                restaurantsResults.add(restaurant);

                            }
                        }

                        Data.newInstanceOfBookedRestaurant(restaurantsResults);

                    }
                });
    }
}