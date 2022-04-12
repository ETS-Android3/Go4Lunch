package com.tonyocallimoutou.go4lunch.ui.autocomplete;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tonyocallimoutou.go4lunch.R;
import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;
import com.tonyocallimoutou.go4lunch.model.places.search.Prediction;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AutocompleteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AutocompleteFragment extends Fragment {

    @BindView(R.id.autocomplete_recycler_view)
    RecyclerView mRecyclerView;

    private static AutocompleteRecyclerViewAdapter adapter;

    private static List<Prediction> mPredictions = new ArrayList<>();

    public AutocompleteFragment() {
        // Required empty public constructor
    }

    public static AutocompleteFragment newInstance() {
        AutocompleteFragment fragment = new AutocompleteFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_autocomplete, container, false);

        ButterKnife.bind(this,view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        adapter = new AutocompleteRecyclerViewAdapter(mPredictions);
        mRecyclerView.setAdapter(adapter);
        return view;
    }

    public static void setPredictions(List<Prediction> results) {
        mPredictions.clear();
        mPredictions.addAll(results);
        Log.d("TAG", "setPredictions: " + mPredictions.size());
        adapter.notifyDataSetChanged();
    }
}