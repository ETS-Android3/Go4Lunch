package com.tonyocallimoutou.go4lunch.ui.workmates;

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
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class WorkmatesFragment extends Fragment {


    private static TextView lblWorkmates;
    @BindView(R.id.workmates_recycler_view)
    RecyclerView mRecyclerView;

    private static ViewModelUser viewModelUser;
    private static List<User> mUsers = new ArrayList<>();

    private static List<User> workmates = new ArrayList<>();

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
        viewModelUser = new ViewModelProvider(requireActivity()).get(ViewModelUser.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_workmates, container, false);
        ButterKnife.bind(this,view);

        lblWorkmates = view.findViewById(R.id.lbl_no_workmates);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        adapter = new WorkmatesRecyclerViewAdapter(getContext(),mUsers);
        initUserList();
        mRecyclerView.setAdapter(adapter);
        return view;

    }

    public static void initUserList() {

        if (adapter != null) {

            mUsers.clear();

            mUsers.addAll(workmates);

            List<User> userToRemove = new ArrayList<>();
            for (User user : mUsers) {
                if (user.getUid().equals(viewModelUser.getCurrentUser().getUid())) {
                    userToRemove.add(user);
                }
            }
            mUsers.removeAll(userToRemove);

            if (lblWorkmates != null) {
                if (mUsers.size() == 0) {
                    lblWorkmates.setVisibility(View.VISIBLE);
                } else {
                    lblWorkmates.setVisibility(View.GONE);
                }
            }


            adapter.notifyDataSetChanged();
        }

    }

    public static void setWorkmates(List<User> result) {
        workmates = result;
        initUserList();
    }
}