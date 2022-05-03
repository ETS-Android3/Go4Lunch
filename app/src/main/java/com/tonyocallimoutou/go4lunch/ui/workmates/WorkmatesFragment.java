package com.tonyocallimoutou.go4lunch.ui.workmates;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tonyocallimoutou.go4lunch.R;
import com.tonyocallimoutou.go4lunch.model.User;
import com.tonyocallimoutou.go4lunch.model.places.search.Prediction;
import com.tonyocallimoutou.go4lunch.ui.BaseFragment;
import com.tonyocallimoutou.go4lunch.ui.chat.ChatActivity;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelRestaurant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class WorkmatesFragment extends BaseFragment {


    private static TextView lblWorkmates;
    private static RecyclerView mRecyclerView;

    private static Context context;
    private static Activity activity;

    private static ViewModelRestaurant viewModelRestaurant;
    private static List<User> workmatesWithoutUser = new ArrayList<>();
    private static List<User> workmates = new ArrayList<>();
    private static List<Integer> newMessageList = new ArrayList<>();
    private static Map<String,Integer> numberNoReading = new HashMap<>();
    private static User currentUser;

    private static WorkmatesRecyclerViewAdapter adapter;


    public WorkmatesFragment() {
        // Required empty public constructor
    }

    public static WorkmatesFragment newInstance() {
        WorkmatesFragment fragment = new WorkmatesFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModelRestaurant = new ViewModelProvider(requireActivity()).get(ViewModelRestaurant.class);
    }

    // BASE FRAGMENT SEARCH


    @Override
    public String getQueryHint() {
        return getString(R.string.search_hint_workmates);
    }

    @Override
    public void doSearch(String s) {
        viewModelRestaurant.setSearchWorkmates(s,workmatesWithoutUser);
    }

    @Override
    public void onPredictionItemClick(Prediction prediction) {
        super.onPredictionItemClick(prediction);
        workmatesWithoutUser.clear();
        for (User user : workmates) {
            if (user.getUid().equals(prediction.getPlaceId())) {
                workmatesWithoutUser.add(user);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_workmates, container, false);
        ButterKnife.bind(this,view);

        lblWorkmates = view.findViewById(R.id.lbl_no_workmates);
        mRecyclerView = view.findViewById(R.id.workmates_recycler_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        adapter = new WorkmatesRecyclerViewAdapter(getContext(), workmatesWithoutUser, newMessageList, new WorkmatesRecyclerViewAdapter.WorkmatesItemClickListener() {
            @Override
            public void onWorkmatesItemClick(int position) {

                User user = workmatesWithoutUser.get(position);
                List<User> users = new ArrayList<>();
                users.add(user);
                ChatActivity.navigate(getActivity(), users, null);
            }
        });
        initUserList();
        mRecyclerView.setAdapter(adapter);
        return view;

    }

    public static void initUserList() {

        if (adapter != null && currentUser != null) {

            workmatesWithoutUser.clear();

            workmatesWithoutUser.addAll(workmates);

            List<User> userToRemove = new ArrayList<>();
            for (User user : workmatesWithoutUser) {
                if (user.getUid().equals(currentUser.getUid())) {
                    userToRemove.add(user);
                }
            }
            workmatesWithoutUser.removeAll(userToRemove);

            if (lblWorkmates != null) {
                if (workmatesWithoutUser.size() == 0) {
                    lblWorkmates.setVisibility(View.VISIBLE);
                } else {
                    lblWorkmates.setVisibility(View.GONE);
                }
            }

            initNewMessageList();

            adapter.initAdapter(workmatesWithoutUser);
        }

    }

    private static void initNewMessageList() {
        newMessageList.clear();
        for (User user : workmatesWithoutUser) {
            if (numberNoReading.get(user.getUid()) != null) {
                newMessageList.add(numberNoReading.get(user.getUid()));
            }
            else {
                newMessageList.add(0);
            }
        }
    }


    public static void setWorkmates(List<User> result) {
        workmates = result;
        initUserList();
    }

    public static void setCurrentUser(User result) {
        currentUser = result;
        initUserList();
    }

    public static void initPins(Map<String,Integer> result) {
        numberNoReading = result;
        initUserList();
    }
}