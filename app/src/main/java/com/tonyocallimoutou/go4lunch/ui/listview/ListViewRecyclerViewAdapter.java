package com.tonyocallimoutou.go4lunch.ui.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tonyocallimoutou.go4lunch.R;
import com.tonyocallimoutou.go4lunch.model.User;
import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;
import com.tonyocallimoutou.go4lunch.utils.RestaurantData;
import com.tonyocallimoutou.go4lunch.utils.RestaurantRate;
import com.tonyocallimoutou.go4lunch.utils.WorkmatesLunch;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListViewRecyclerViewAdapter extends RecyclerView.Adapter<ListViewRecyclerViewAdapter.ViewHolder> {

    private List<RestaurantDetails> mRestaurants;
    private final Context mContext;
    private final ListItemClickListener mListItemClickListener;
    private List<User> mWorkmates;
    private final List<Integer> newMessageInt;

    public ListViewRecyclerViewAdapter(Context context,
                                       List<RestaurantDetails> restaurants,
                                       List<Integer> listInt,
                                       List<User> workmates,
                                       ListItemClickListener listItemClickListener) {
        mRestaurants = restaurants;
        mContext = context;
        newMessageInt = listInt;
        mListItemClickListener = listItemClickListener;
        mWorkmates = workmates;
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
        RestaurantDetails restaurant = mRestaurants.get(position);
        int newMessage = newMessageInt.get(position);

        if (newMessage != 0) {
            holder.layoutPins.setVisibility(View.VISIBLE);
            String str = "("+ newMessage +")";
            holder.nbrNewMessage.setText(str);
        }

        RestaurantData.newInstance(mContext, restaurant);
        RestaurantRate.newInstance(restaurant,holder.rateOne, holder.rateTwo, holder.rateThree, mWorkmates);

        RestaurantRate.setImage();

        holder.restaurantName.setText(RestaurantData.getRestaurantName());
        holder.restaurantTypeAndAddress.setText(RestaurantData.getTypeAndAddress());
        holder.restaurantHours.setText(RestaurantData.getOpeningHour());
        if (RestaurantData.getPicture() != null) {
            Glide.with(mContext).load(RestaurantData.getPicture()).into(holder.restaurantPicture);
        }

        if (RestaurantData.getDistance() != null) {
            holder.restaurantDistance.setText(RestaurantData.getDistance());
        }
        else {
            holder.restaurantDistance.setText(mContext.getString(R.string.restaurant_data_no_data));
        }


        if (RestaurantData.getNbrWorkmates() != null) {
            holder.imgWorkmates.setVisibility(View.VISIBLE);
            holder.nbrWorkmates.setVisibility(View.VISIBLE);
            holder.nbrWorkmates.setText(RestaurantData.getNbrWorkmates());
        }
        else {
            holder.imgWorkmates.setVisibility(View.INVISIBLE);
            holder.nbrWorkmates.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mRestaurants.size()  ;
    }

    public interface ListItemClickListener{
        void onListItemClick(int position);
    }

    public void initAdapter(List<RestaurantDetails> restaurants, List<User> workmates) {
        mRestaurants = restaurants;
        mWorkmates = workmates;
        notifyDataSetChanged();
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
        @BindView(R.id.list_view_nbr_workmate)
        TextView nbrWorkmates;
        @BindView(R.id.list_view_item_workmate)
        ImageView imgWorkmates;
        @BindView(R.id.rate1)
        ImageView rateOne;
        @BindView(R.id.rate2)
        ImageView rateTwo;
        @BindView(R.id.rate3)
        ImageView rateThree;
        @BindView(R.id.pins_new_message)
        LinearLayout layoutPins;
        @BindView(R.id.pins_new_message_txt)
        TextView nbrNewMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    mListItemClickListener.onListItemClick(position);
                }
            });
        }
    }
}
