package com.tonyocallimoutou.go4lunch.ui.autocomplete;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tonyocallimoutou.go4lunch.R;
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
public class AutocompleteFragment extends Fragment{

    @BindView(R.id.autocomplete_recycler_view)
    RecyclerView mRecyclerView;

    private static TextView textNoPrediction;
    private static AutocompleteRecyclerViewAdapter adapter;
    private static boolean isConnected;
    private static Context context;


    private static List<Prediction> mPredictions = new ArrayList<>();

    private static AutocompleteRecyclerViewAdapter.PredictionItemClickListener mPredictionItemClickListener;

    public AutocompleteFragment() {
        // Required empty public constructor
    }

    public static AutocompleteFragment newInstance(AutocompleteRecyclerViewAdapter.PredictionItemClickListener predictionItemClickListener) {
        mPredictionItemClickListener = predictionItemClickListener;
        return new AutocompleteFragment();
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

        context = getContext();

        textNoPrediction = view.findViewById(R.id.autocomplete_no_prediction);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        adapter = new AutocompleteRecyclerViewAdapter(getContext(), mPredictions,mPredictionItemClickListener);
        initList();
        mRecyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        mPredictions.clear();
    }

    private static void initList() {
        initTextPrediction();

        if (mPredictions.size() == 0) {
            textNoPrediction.setVisibility(View.VISIBLE);
        }
        else {
            textNoPrediction.setVisibility(View.GONE);
        }
    }

    private static void initTextPrediction() {
        if (isConnected) {
            textNoPrediction.setText(context.getString(R.string.no_prediction));
        }
        else {
            textNoPrediction.setText(context.getString(R.string.no_connection));
        }
    }

    public static void setPredictions(List<Prediction> results) {
        mPredictions.clear();
        mPredictions.addAll(results);
        initList();
        adapter.notifyDataSetChanged();
    }

    public static void initWithConnection(boolean result) {
        isConnected = result;
        if (context != null) {
            initTextPrediction();
        }
    }
}