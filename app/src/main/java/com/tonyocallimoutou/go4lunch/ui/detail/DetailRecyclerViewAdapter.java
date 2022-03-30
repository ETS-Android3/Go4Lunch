package com.tonyocallimoutou.go4lunch.ui.detail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.tonyocallimoutou.go4lunch.R;
import com.tonyocallimoutou.go4lunch.model.User;
import com.tonyocallimoutou.go4lunch.ui.workmates.WorkmatesRecyclerViewAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailRecyclerViewAdapter extends RecyclerView.Adapter<DetailRecyclerViewAdapter.ViewHolder>{

    private final List<User> mUsers;
    private final Context mContext;


    public DetailRecyclerViewAdapter(Context context, List<User> users) {
        mContext = context;
        mUsers = users;
    }


    @NonNull
    @Override
    public DetailRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_workmates_item,null);
        return new DetailRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailRecyclerViewAdapter.ViewHolder holder, int position) {
        User user = mUsers.get(position);

        holder.workmateText.setText(user.getUsername() + " " + mContext.getString(R.string.workmate_is_eating));

        Glide.with(mContext)
                .load(user.getUrlPicture())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.workmatePicture);
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.profile_picture_workmate)
        ImageView workmatePicture;
        @BindView(R.id.workmates_text)
        TextView workmateText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
