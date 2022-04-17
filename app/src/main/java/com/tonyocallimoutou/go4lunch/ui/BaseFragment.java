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
import com.tonyocallimoutou.go4lunch.utils.SearchAction;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelRestaurant;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelUser;

import java.util.Timer;
import java.util.TimerTask;

public abstract class BaseFragment extends Fragment implements AutocompleteRecyclerViewAdapter.PredictionItemClickListener, SearchAction {

    private SearchView searchView;
    private AutocompleteFragment autocompleteFragment;

    private Timer timer;
    private int delay = 400; //400 ms

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
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
        if (searchView != null && !searchView.isIconified()) {
            searchView.setIconified(true);
            searchView.onActionViewCollapsed();
            closeFragment();
        }
    }

    public String getQueryHint() {
        return getString(R.string.search_hint_restaurant);
    }


    private void initSearch() {

        searchView.setQueryHint(getQueryHint());

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

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                doSearch(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.length() >= 3) {
                    if (timer != null) {
                        timer.cancel();
                    }
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            doSearch(s);
                        }
                    }, delay);
                }
                return false;
            }
        });
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

