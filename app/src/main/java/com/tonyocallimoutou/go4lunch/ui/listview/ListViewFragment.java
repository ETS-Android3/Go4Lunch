package com.tonyocallimoutou.go4lunch.ui.listview;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tonyocallimoutou.go4lunch.R;
import com.tonyocallimoutou.go4lunch.model.Places.RestaurantsResult;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelRestaurant;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListViewFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ViewModelRestaurant viewModelRestaurant;
    private List<RestaurantsResult> mRestaurants = new ArrayList<>();

    private static List<RestaurantsResult> bookedRestaurant = new ArrayList<>();
    private static List<RestaurantsResult> nearbyRestaurant = new ArrayList<>();

    ListViewRecyclerViewAdapter adapter;

    private int sizeOfListRestaurantNearbyPlace = 10;

    public ListViewFragment() {
        // Required empty public constructor
    }

    public static ListViewFragment newInstance() {
        ListViewFragment fragment = new ListViewFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModelRestaurant = new ViewModelProvider(requireActivity()).get(ViewModelRestaurant.class);

        viewModelRestaurant.setBookedRestaurantList();
        //view setNearbyPlace
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_view, container, false);
        mRecyclerView = (RecyclerView) view;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        adapter = new ListViewRecyclerViewAdapter(getContext(),mRestaurants);
        initRestaurantList();
        mRecyclerView.setAdapter(adapter);
        return view;
    }

    public void initRestaurantList() {

        mRestaurants.addAll(nearbyRestaurant);

        mRestaurants.addAll(bookedRestaurant);

        adapter.notifyDataSetChanged();
    }

    public static void setBookedRestaurant(List<RestaurantsResult> result) {
        bookedRestaurant = result;
    }

    public static void setNearbyRestaurant(List<RestaurantsResult> result) {
        nearbyRestaurant = result;
    }

    public static Resources getResourcesForData() {
        return Resources.getSystem();
    }
}