package com.tonyocallimoutou.go4lunch.ui.detail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tonyocallimoutou.go4lunch.R;
import com.tonyocallimoutou.go4lunch.model.Places.RestaurantsResult;
import com.tonyocallimoutou.go4lunch.model.User;
import com.tonyocallimoutou.go4lunch.ui.listview.ListViewRecyclerViewAdapter;
import com.tonyocallimoutou.go4lunch.ui.workmates.WorkmatesFragment;
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

    @BindView(R.id.detail_restaurant_picture)
    ImageView restaurantPicture;
    @BindView(R.id.detail_name_restaurant)
    TextView restaurantName;
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


    DetailRecyclerViewAdapter adapter;

    boolean isBooked;

    private static RestaurantsResult restaurant;
    private static List<User> workmates = new ArrayList<>();

    private ViewModelUser viewModelUser;
    private ViewModelRestaurant viewModelRestaurant;

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

        workmates.add(viewModelUser.getCurrentUser());

        if (restaurant.getWorkmatesId().size() != 0) {
            for (User user : workmates) {
                if ( ! restaurant.getWorkmatesId().contains(user.getUid())) {
                    workmates.remove(user);
                }
            }
        }
        else {
            workmates.clear();
        }


        adapter.notifyDataSetChanged();
    }


    public void setInformation () {

        RestaurantData.newInstance(restaurant);

        int rate = RestaurantData.getRate();

        RestaurantRate.newInstance(rate,restaurantRate1,restaurantRate2,restaurantRate3);

        RestaurantRate.setImage();

        restaurantName.setText(RestaurantData.getRestaurantName());
        restaurantAddress.setText(RestaurantData.getTypeAndAddress());
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


    public static void navigate(Activity activity, RestaurantsResult result ) {
        restaurant = result;
        Intent intent = new Intent(activity, DetailsActivity.class);
        ActivityCompat.startActivity(activity, intent, null);
    }

    public static void setWorkmates(List<User> result) {
        workmates = result;
    }
}