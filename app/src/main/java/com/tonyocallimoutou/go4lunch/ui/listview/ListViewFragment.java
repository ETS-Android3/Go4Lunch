package com.tonyocallimoutou.go4lunch.ui.listview;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tonyocallimoutou.go4lunch.R;
import com.tonyocallimoutou.go4lunch.model.Places.RestaurantsResult;
import com.tonyocallimoutou.go4lunch.ui.mapview.MapViewFragment;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelFactory;
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
    private ViewModelRestaurant viewModel;
    private List<RestaurantsResult> mRestaurants = new ArrayList<>();

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
        viewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(ViewModelRestaurant.class);

        initRestaurantList();
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
        mRecyclerView.setAdapter(adapter);
        return view;
    }

    public void initRestaurantList() {

        Log.d("TAG", "initRestaurantList: ");
        if (mRestaurants != null) {
            mRestaurants.clear();
        }

        viewModel.getNearbyPlaceLiveData().observe(this, nearbyPlace -> {
            if (nearbyPlace != null) {
                Log.d("TAG", "initRestaurantList: " + nearbyPlace.getResults().size());
                for (int i=0; i< nearbyPlace.getResults().size(); i++) {
                    if (i < sizeOfListRestaurantNearbyPlace) {
                        mRestaurants.add(nearbyPlace.getResults().get(i));
                        Log.d("TAG", "New List: ");
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });

        viewModel.getBookedRestaurantsCollection().get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot document : list) {
                                RestaurantsResult restaurant = document.toObject(RestaurantsResult.class);
                                mRestaurants.add(restaurant);
                                adapter.notifyDataSetChanged();

                            }
                        }

                    }
                });
    }
}