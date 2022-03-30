package com.tonyocallimoutou.go4lunch.ui.workmates;

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
import com.tonyocallimoutou.go4lunch.model.User;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class WorkmatesFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ViewModelUser viewModelUser;
    private List<User> mUsers = new ArrayList<>();

    private static List<User> workmates = new ArrayList<>();

    WorkmatesRecyclerViewAdapter adapter;


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
        viewModelUser = new ViewModelProvider(requireActivity()).get(ViewModelUser.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_workmates, container, false);
        mRecyclerView = (RecyclerView) view;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        adapter = new WorkmatesRecyclerViewAdapter(getContext(),mUsers);
        initUserList();
        mRecyclerView.setAdapter(adapter);
        return view;

    }

    public void initUserList() {

        mUsers.addAll(workmates);

        adapter.notifyDataSetChanged();

    }

    public static void setWorkmates(List<User> result) {
        workmates = result;
    }
}