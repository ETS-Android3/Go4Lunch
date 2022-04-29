package com.tonyocallimoutou.go4lunch.ui.listview;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tonyocallimoutou.go4lunch.R;
import com.tonyocallimoutou.go4lunch.model.User;
import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;
import com.tonyocallimoutou.go4lunch.model.places.search.Prediction;
import com.tonyocallimoutou.go4lunch.ui.BaseFragment;
import com.tonyocallimoutou.go4lunch.ui.autocomplete.AutocompleteFragment;
import com.tonyocallimoutou.go4lunch.ui.detail.DetailsActivity;
import com.tonyocallimoutou.go4lunch.utils.RestaurantMethod;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelRestaurant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListViewFragment extends BaseFragment {

    private static TextView lblNoRestaurant;
    private static RecyclerView mRecyclerView;

    private AutocompleteFragment autocompleteFragment;

    private static List<RestaurantDetails> mRestaurants = new ArrayList<>();
    private static List<RestaurantDetails> bookedRestaurant = new ArrayList<>();
    private static List<RestaurantDetails> nearbyRestaurant = new ArrayList<>();
    private static List<RestaurantDetails> nearbyWithoutBooked = new ArrayList<>();
    private static List<User> workmates = new ArrayList<>();

    private static List<Integer> newMessageList = new ArrayList<>();
    private static Map<String,Integer> numberNoReading = new HashMap<>();

    private static ListViewRecyclerViewAdapter adapter;
    private static Context mContext;
    private static FragmentActivity mActivity;

    private ViewModelRestaurant viewModelRestaurant;

    public ListViewFragment() {
        // Required empty public constructor
    }

    public static ListViewFragment newInstance() {
        ListViewFragment fragment = new ListViewFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        viewModelRestaurant = new ViewModelProvider(requireActivity()).get(ViewModelRestaurant.class);
        super.onCreate(savedInstanceState);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_view, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list_view_recycler_view);
        lblNoRestaurant = view.findViewById(R.id.lbl_no_restaurant);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        mContext = getContext();
        mActivity = getActivity();
        initRestaurantList();
        return view;
    }

    public static void initRestaurantList() {

        mRestaurants.clear();
        nearbyWithoutBooked.clear();
        if (nearbyRestaurant.size() != 0) {
            nearbyWithoutBooked = RestaurantMethod.getNearbyRestaurantWithoutBooked(nearbyRestaurant, bookedRestaurant);
        }
        mRestaurants.addAll(bookedRestaurant);
        mRestaurants.addAll(nearbyWithoutBooked);

        if (lblNoRestaurant != null) {
            if (mRestaurants.size() == 0) {
                lblNoRestaurant.setVisibility(View.VISIBLE);
            } else {
                lblNoRestaurant.setVisibility(View.GONE);
            }
        }

        if (mRecyclerView != null) {
            initNewMessageList();
            initAdapter();
        }

    }

    private static void initNewMessageList() {
        newMessageList.clear();
        for (RestaurantDetails restaurant : mRestaurants) {
            if (numberNoReading.get(restaurant.getPlaceId()) != null) {
                newMessageList.add(numberNoReading.get(restaurant.getPlaceId()));
            }
            else {
                newMessageList.add(0);
            }
        }
    }

    private static void initAdapter() {
        adapter = new ListViewRecyclerViewAdapter(mContext, mRestaurants, newMessageList, workmates, new ListViewRecyclerViewAdapter.ListItemClickListener() {
            @Override
            public void onListItemClick(int position) {
                RestaurantDetails restaurant = mRestaurants.get(position);
                DetailsActivity.navigate(mActivity, restaurant);
            }
        });

        mRecyclerView.setAdapter(adapter);
    }

    public static void setBookedRestaurant(List<RestaurantDetails> result) {
        bookedRestaurant.clear();
        bookedRestaurant.addAll(result);
        initRestaurantList();
    }

    public static void setNearbyRestaurant(List<RestaurantDetails> result) {
        nearbyRestaurant.clear();
        nearbyRestaurant.addAll(result);
        initRestaurantList();
    }

    public static void setWorkmates(List<User> result) {
        workmates.clear();
        workmates.addAll(result);
        initRestaurantList();
    }

    public void setPrediction() {
        viewModelRestaurant.getDetailPrediction().observe(this, result -> {
            mRestaurants.clear();
            mRestaurants.add(result);
            adapter.notifyDataSetChanged();
        });
    }

    public static void initPins(Map<String,Integer> result) {
        numberNoReading = result;
        initRestaurantList();
    }
}

