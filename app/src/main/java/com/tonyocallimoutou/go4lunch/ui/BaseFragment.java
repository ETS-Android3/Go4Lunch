package com.tonyocallimoutou.go4lunch.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.tonyocallimoutou.go4lunch.R;
import com.tonyocallimoutou.go4lunch.model.places.search.Prediction;
import com.tonyocallimoutou.go4lunch.ui.autocomplete.AutocompleteFragment;
import com.tonyocallimoutou.go4lunch.ui.autocomplete.AutocompleteRecyclerViewAdapter;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelRestaurant;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelUser;

public abstract class BaseFragment extends Fragment implements SearchView.OnQueryTextListener, AutocompleteRecyclerViewAdapter.PredictionItemClickListener {

    private SearchView searchView;
    private AutocompleteFragment autocompleteFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("TAG", "onCreate: ");
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.actionbar_menu, menu);
        searchView = (SearchView) menu.findItem(R.id.search_menu).getActionView();
        initSearch();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            searchView.onActionViewCollapsed();
            closeFragment();
        }
        Log.d("TAG", "onPause: ");
    }


    private void initSearch() {

        searchView.setQueryHint(getString(R.string.search_hint));

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autocompleteFragment = AutocompleteFragment.newInstance(BaseFragment.this);
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.host_fragment_autocomplete, autocompleteFragment);
                fragmentTransaction.commit();
                Log.d("TAG", "onClick: ");
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                closeFragment();
                Log.d("TAG", "onClose: ");
                return false;
            }
        });

        searchView.setOnQueryTextListener(this);
    }

    private void closeFragment() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .remove(autocompleteFragment)
                .commit();
    }

    @Override
    public void onPredictionItemClick(Prediction prediction) {
        searchView.setIconified(true);
        searchView.onActionViewCollapsed();
        closeFragment();
    }
}

