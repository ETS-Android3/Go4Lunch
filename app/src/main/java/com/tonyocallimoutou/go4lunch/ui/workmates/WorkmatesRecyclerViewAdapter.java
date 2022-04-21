package com.tonyocallimoutou.go4lunch.ui.workmates;

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
import com.tonyocallimoutou.go4lunch.ui.chat.ChatActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WorkmatesRecyclerViewAdapter extends RecyclerView.Adapter<WorkmatesRecyclerViewAdapter.ViewHolder> {

    private final List<User> mUsers;
    private final Context mContext;
    private final WorkmatesItemClickListener workmatesItemClickListener;


    public WorkmatesRecyclerViewAdapter(Context context, List<User> users, WorkmatesItemClickListener listener) {
        mContext = context;
        mUsers = users;
        workmatesItemClickListener = listener;
    }


    @NonNull
    @Override
    public WorkmatesRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_workmates_item,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmatesRecyclerViewAdapter.ViewHolder holder, int position) {
        User user = mUsers.get(position);

        initText(holder, user);

        if (user.getUrlPicture() != null) {
            Glide.with(mContext)
                    .load(user.getUrlPicture())
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.workmatePicture);
        }
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public interface WorkmatesItemClickListener{
        void onWorkmatesItemClick(int position);
    }

    public void initText(WorkmatesRecyclerViewAdapter.ViewHolder holder, User user) {
        String text = user.getUsername()+ " ";
        if (user.getBookedRestaurant() == null) {
            text += mContext.getString(R.string.workmate_has_not_decided_yet);
            holder.workmateText.setTextColor(ContextCompat.getColor(mContext,R.color.grey));
        }
        else {
            String type = user.getBookedRestaurant().getTypes().get(0);
            String name = '(' + user.getBookedRestaurant().getName() + ')';
            text += mContext.getString(R.string.workmate_is_eating) + " "+ type + name;
            holder.workmateText.setTextColor(ContextCompat.getColor(mContext,R.color.black_reverse));
        }

        holder.workmateText.setText(text);

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.profile_picture_workmate)
        ImageView workmatePicture;
        @BindView(R.id.workmates_text)
        TextView workmateText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    workmatesItemClickListener.onWorkmatesItemClick(position);
                }
            });
        }
    }
}
