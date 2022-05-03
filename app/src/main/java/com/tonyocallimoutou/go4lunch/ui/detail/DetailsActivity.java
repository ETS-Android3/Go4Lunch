package com.tonyocallimoutou.go4lunch.ui.detail;

import android.Manifest;
import android.app.Activity;
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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tonyocallimoutou.go4lunch.R;
import com.tonyocallimoutou.go4lunch.model.User;
import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;
import com.tonyocallimoutou.go4lunch.ui.BaseActivity;
import com.tonyocallimoutou.go4lunch.ui.MainActivity;
import com.tonyocallimoutou.go4lunch.ui.chat.ChatActivity;
import com.tonyocallimoutou.go4lunch.utils.RestaurantData;
import com.tonyocallimoutou.go4lunch.utils.RestaurantRate;
import com.tonyocallimoutou.go4lunch.utils.UtilNotification;
import com.tonyocallimoutou.go4lunch.utils.WorkmatesLunch;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelFactory;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelRestaurant;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailsActivity extends BaseActivity {

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
    @BindView(R.id.detail_img_like)
    ImageView likeButton;
    @BindView(R.id.detail_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.lbl_no_workmates)
    TextView lblWorkmates;
    @BindView(R.id.fab_chat_restaurant)
    FloatingActionButton goToChat;

    DetailRecyclerViewAdapter adapter;

    boolean isBooked;
    boolean isLike;

    private static RestaurantDetails restaurant;
    private User currentUser;
    private List<User> workmates = new ArrayList<>();
    private List<User> workmatesLunch = new ArrayList<>();

    private ViewModelUser viewModelUser;
    private ViewModelRestaurant viewModelRestaurant;

    private static final int MY_PERMISSION_REQUEST_CODE_CALL_PHONE = 555;
    public static final String KEY_EXTRA_DETAIL_ACTIVITY = "KEY_EXTRA_DETAIL_ACTIVITY";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        viewModelUser = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(ViewModelUser.class);
        viewModelRestaurant = new ViewModelProvider(this,ViewModelFactory.getInstance()).get(ViewModelRestaurant.class);


        initDataWithoutCurrentUserLiveData();

        // Come From Notification
        Bundle extras = getIntent().getExtras();
        if (restaurant == null && extras != null) {
            if (extras.getBoolean(KEY_EXTRA_DETAIL_ACTIVITY)) {
                viewModelUser.setCurrentUserLiveData();

                viewModelUser.getCurrentUserLiveData().observe(this, currentUserResults -> {
                    if (currentUserResults != null) {
                        currentUser = currentUserResults;
                        restaurant = currentUser.getBookedRestaurant();

                        // remove Restaurant before click on Notification
                        if(restaurant == null) {
                            Intent intent = new Intent(this, MainActivity.class);
                            startActivity(intent);
                        }
                        else {
                            UtilNotification.newInstance(null,null,this,currentUserResults);
                            initWorkmatesList();
                        }

                    }
                });
            }
            else {
                Log.w("TAG", "onCreate: ");
            }
        }
        else {
            initLiveDataCurrentUser();
        }
    }

    public void initRecyclerView(){

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new DetailRecyclerViewAdapter(this,workmatesLunch);
        recyclerView.setAdapter(adapter);
    }

    public void initWorkmatesList() {
        isBooked = restaurant.getWorkmatesId().contains(currentUser.getUid());
        isLike = currentUser.getLikeRestaurantId().contains(restaurant.getPlaceId());

        workmatesLunch = WorkmatesLunch.getWorkmatesLunch(restaurant,workmates);

        if (workmatesLunch.size() == 0) {
            lblWorkmates.setVisibility(View.VISIBLE);
        }
        else {
            lblWorkmates.setVisibility(View.GONE);
        }

        goToChat.setVisibility(View.GONE);
        for (User user : workmatesLunch) {
            if (user.getUid().equals(currentUser.getUid())) {
                goToChat.setVisibility(View.VISIBLE);
            }
        }


        if (adapter == null) {
            initRecyclerView();
        }
        else {
            adapter.initAdapter(workmatesLunch);
        }
        setInformation();
    }


    public void setInformation () {
        List<User> users = new ArrayList<>();
        users.addAll(workmates);

        for (User user : users) {
            if (user.getUid().equals(currentUser.getUid())) {
                workmates.add(currentUser);
                workmates.remove(user);
            }
        }

        RestaurantData.newInstance(this, restaurant);
        RestaurantRate.newInstance(restaurant,restaurantRate1,restaurantRate2,restaurantRate3, workmates);

        RestaurantRate.setImage();

        restaurantName.setText(RestaurantData.getRestaurantName());
        restaurantAddress.setText(RestaurantData.getTypeAndAddress());
        if (RestaurantData.getPicture() != null) {
            Glide.with(this).load(RestaurantData.getPicture()).into(restaurantPicture);
        }

        setFAB();
        setLike();
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

    private void setFAB() {
        if (isBooked) {
            fabBooked.setColorFilter(ContextCompat.getColor(this,R.color.green));
        }
        else {
            fabBooked.setColorFilter(ContextCompat.getColor(this,R.color.red));
        }
    }

    private void setLike() {
        if (isLike) {
            likeButton.setImageDrawable(getDrawable(R.drawable.ic_star_gold_24dp));
        }
        else {
            likeButton.setImageDrawable(getDrawable(R.drawable.ic_star_border_black_24dp));
        }
    }




    @OnClick(R.id.detail_call)
    public void call() {
        callOrPermission();
    }

    @OnClick(R.id.detail_like)
    public void like() {
        likeRestaurant();
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

    @OnClick(R.id.fab_chat_restaurant)
    public void goToChat() {
        ChatActivity.navigate(this,workmatesLunch,restaurant);
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
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + RestaurantData.getPhone()));

        startActivity(intent);
    }

    // Like

    private void likeRestaurant() {
        if (isLike) {
            viewModelRestaurant.dislikeThisRestaurant(restaurant);
            Toast.makeText(this, getString(R.string.you_not_like_it), Toast.LENGTH_SHORT).show();
        }
        else {
            viewModelRestaurant.likeThisRestaurant(restaurant);
            Toast.makeText(this, getString(R.string.you_like_it), Toast.LENGTH_SHORT).show();
        }

    }

    // INIT

    public void initDataWithoutCurrentUserLiveData() {
        viewModelUser.setWorkmatesList();
        viewModelRestaurant.setBookedRestaurantList();

        viewModelUser.getWorkmates().observe(this, workmates -> {
            this.workmates = workmates;
            UtilNotification.newInstance(workmates,null,null,null);
        });

        viewModelRestaurant.getBookedRestaurantLiveData().observe(this, bookedRestaurantResult -> {
            UtilNotification.newInstance(null,bookedRestaurantResult,null,null);
            if (restaurant != null) {
                for (RestaurantDetails result : bookedRestaurantResult) {
                    if (result.getPlaceId().equals(restaurant.getPlaceId())) {
                        restaurant = result;
                    }
                }
            }
        });
    }

    public void initLiveDataCurrentUser() {
        viewModelUser.setCurrentUserLiveData();

        viewModelUser.getCurrentUserLiveData().observe(this, currentUserResults -> {
            if (currentUserResults != null) {
                UtilNotification.newInstance(null,null,this,currentUserResults);
                currentUser = currentUserResults;
                initWorkmatesList();
            }
        });

    }

    public static void navigate(Activity activity, RestaurantDetails result ) {
        restaurant = result;
        Intent intent = new Intent(activity, DetailsActivity.class);
        ActivityCompat.startActivity(activity, intent, null);
    }
}