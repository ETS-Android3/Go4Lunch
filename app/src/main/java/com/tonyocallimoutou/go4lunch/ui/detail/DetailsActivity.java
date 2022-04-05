package com.tonyocallimoutou.go4lunch.ui.detail;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.tonyocallimoutou.go4lunch.R;
import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;
import com.tonyocallimoutou.go4lunch.model.User;
import com.tonyocallimoutou.go4lunch.utils.RestaurantData;
import com.tonyocallimoutou.go4lunch.utils.RestaurantRate;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelRestaurant;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailsActivity extends AppCompatActivity {

    @BindView(R.id.detail_relative_layout)
    RelativeLayout view;
    @BindView(R.id.detail_restaurant_picture)
    ImageView restaurantPicture;
    @BindView(R.id.detail_name_restaurant)
    TextView restaurantName;
    @BindView(R.id.detail_booked_restaurant)
    FloatingActionButton fabBooked;
    @BindView(R.id.rate3)
    ImageView restaurantRate3;
    @BindView(R.id.rate2)
    ImageView restaurantRate2;
    @BindView(R.id.rate1)
    ImageView restaurantRate1;
    @BindView(R.id.detail_restaurant_address)
    TextView restaurantAddress;
    @BindView(R.id.detail_call)
    RelativeLayout restaurantCall;
    @BindView(R.id.detail_like)
    RelativeLayout restaurantLike;
    @BindView(R.id.detail_website)
    RelativeLayout restaurantWebsite;
    @BindView(R.id.detail_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.lbl_no_workmates)
    TextView lblWorkmates;


    DetailRecyclerViewAdapter adapter;

    boolean isBooked;

    private static RestaurantDetails restaurant;
    private static List<User> workmates = new ArrayList<>();

    private ViewModelUser viewModelUser;
    private ViewModelRestaurant viewModelRestaurant;

    private static final int MY_PERMISSION_REQUEST_CODE_CALL_PHONE = 555;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        viewModelUser = new ViewModelProvider(this).get(ViewModelUser.class);
        viewModelRestaurant = new ViewModelProvider(this).get(ViewModelRestaurant.class);

        initRecyclerView();
        initWorkmatesList();
        setInformation();
    }


    public void initRecyclerView(){

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new DetailRecyclerViewAdapter(this,workmates);
        recyclerView.setAdapter(adapter);
    }

    public void initWorkmatesList() {
        isBooked = restaurant.getWorkmatesId().contains(viewModelUser.getCurrentUser().getUid());

        if (isBooked) {
            workmates.add(viewModelUser.getCurrentUser());
        }

        List<User> removeUser = new ArrayList<>();

        if (restaurant.getWorkmatesId().size() != 0) {
            for (User user : workmates) {
                if ( ! restaurant.getWorkmatesId().contains(user.getUid())) {
                    removeUser.add(user);
                }
            }

            workmates.removeAll(removeUser);
        }
        else {
            workmates.clear();
        }


        if (workmates.size() == 0) {
            lblWorkmates.setVisibility(View.VISIBLE);
        }
        else {
            lblWorkmates.setVisibility(View.GONE);
        }

        adapter.notifyDataSetChanged();
    }


    public void setInformation () {

        RestaurantData.newInstance(restaurant);
        RestaurantRate.newInstance(RestaurantData.getRate(),restaurantRate1,restaurantRate2,restaurantRate3);

        RestaurantRate.setImage();

        restaurantName.setText(RestaurantData.getRestaurantName());
        restaurantAddress.setText(RestaurantData.getTypeAndAddress());
        Glide.with(this).load(RestaurantData.getPicture()).into(restaurantPicture);

        setFAB();
    }

    @OnClick(R.id.detail_booked_restaurant)
    public void bookedRestaurant() {
        if (isBooked) {
            viewModelRestaurant.cancelBookedRestaurant(restaurant);
        }
        else {
            viewModelRestaurant.bookedThisRestaurant(restaurant);
        }

        initWorkmatesList();

    }

    public void setFAB() {
        if (isBooked) {
            //Set FAB
        }
        else {
            // Set FAB
        }
    }



    @OnClick(R.id.detail_call)
    public void call() {
        callOrPermission();
    }

    @OnClick(R.id.detail_like)
    public void like() {
        Log.e("TAG", "Like: " );
    }

    @OnClick(R.id.detail_website)
    public void website() {
        if (RestaurantData.getWebsite() != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(RestaurantData.getWebsite()));
            startActivity(intent);
        }
        else {
            Toast.makeText(this, "Test", Toast.LENGTH_SHORT).show();
        }
    }


    // CALL

    private void callOrPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) { // 23

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                askForPermission();
            } else {
                callRestaurant();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void askForPermission() {
         requestPermissions(
                new String[]{Manifest.permission.CALL_PHONE},
                MY_PERMISSION_REQUEST_CODE_CALL_PHONE
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE_CALL_PHONE: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callRestaurant();
                }
                else {
                    explain();
                }
                break;
            }
        }
    }

    public void explain() {

        Toast.makeText(this, getString(R.string.explain_call), Toast.LENGTH_SHORT).show();
    }

    private void callRestaurant() {
        Log.d("TAG", "call: " + RestaurantData.getPhone() );
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + RestaurantData.getPhone()));

        startActivity(intent);
    }


    // INIT

    public static void navigate(Activity activity, RestaurantDetails result ) {
        restaurant = result;
        Intent intent = new Intent(activity, DetailsActivity.class);
        ActivityCompat.startActivity(activity, intent, null);
    }

    public static void setWorkmates(List<User> result) {
        workmates = result;
    }
}