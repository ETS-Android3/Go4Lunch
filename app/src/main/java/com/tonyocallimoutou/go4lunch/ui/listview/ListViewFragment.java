package com.tonyocallimoutou.go4lunch.ui.listview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tonyocallimoutou.go4lunch.R;
import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;
import com.tonyocallimoutou.go4lunch.ui.detail.DetailsActivity;
import com.tonyocallimoutou.go4lunch.utils.RestaurantMethod;
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
    private static List<RestaurantDetails> mRestaurants = new ArrayList<>();

    private static List<RestaurantDetails> bookedRestaurant = new ArrayList<>();
    private static List<RestaurantDetails> nearbyRestaurant = new ArrayList<>();

    private static ListViewRecyclerViewAdapter adapter;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_view, container, false);
        mRecyclerView = (RecyclerView) view;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        adapter = new ListViewRecyclerViewAdapter(getContext(), mRestaurants, new ListViewRecyclerViewAdapter.ListItemClickListener() {
            @Override
            public void onListItemClick(int position) {
                RestaurantDetails restaurant = mRestaurants.get(position);
                DetailsActivity.navigate(getActivity(), restaurant);
            }
        });


        initRestaurantList();
        mRecyclerView.setAdapter(adapter);
        return view;
    }

    public static void initRestaurantList() {

        mRestaurants.clear();

        if (nearbyRestaurant.size() != 0) {
            nearbyRestaurant = RestaurantMethod.getNearbyRestaurantWithoutBooked(nearbyRestaurant,bookedRestaurant);
        }
        mRestaurants.addAll(bookedRestaurant);
        mRestaurants.addAll(nearbyRestaurant);

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

    }

    public static void setBookedRestaurant(List<RestaurantDetails> result) {
        bookedRestaurant = result;
        initRestaurantList();
    }

    public static void setNearbyRestaurant(List<RestaurantDetails> result) {
        nearbyRestaurant = result;
        initRestaurantList();
    }

}