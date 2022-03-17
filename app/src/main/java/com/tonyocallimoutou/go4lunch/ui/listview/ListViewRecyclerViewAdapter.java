package com.tonyocallimoutou.go4lunch.ui.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tonyocallimoutou.go4lunch.R;
import com.tonyocallimoutou.go4lunch.model.Restaurant;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListViewRecyclerViewAdapter extends RecyclerView.Adapter<ListViewRecyclerViewAdapter.ViewHolder> {

    private final List<Restaurant> mRestaurants;
    private final Context mContext;

    public ListViewRecyclerViewAdapter(Context context, List<Restaurant> restaurants) {
        mRestaurants = restaurants;
        mContext = context;
    }

    @NonNull
    @Override
    public ListViewRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_list_view_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewRecyclerViewAdapter.ViewHolder holder, int position) {
        Restaurant restaurant = mRestaurants.get(position);

        holder.restaurantName.setText(restaurant.getName());
    }

    @Override
    public int getItemCount() {
        return mRestaurants.size()  ;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.list_view_restaurant_name)
        TextView restaurantName;
        @BindView(R.id.list_view_restaurant_distance)
        TextView restaurantDistance;
        @BindView(R.id.list_view_restaurant_picture)
        ImageView restaurantPicture;
        @BindView(R.id.list_view_restaurant_hours)
        TextView restaurantHours;
        @BindView(R.id.list_view_restaurant_type_and_address)
        TextView restaurantTypeAndAddress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
