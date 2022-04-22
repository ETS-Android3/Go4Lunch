package com.tonyocallimoutou.go4lunch.ui.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.tonyocallimoutou.go4lunch.R;
import com.tonyocallimoutou.go4lunch.model.Message;
import com.tonyocallimoutou.go4lunch.model.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<ChatRecyclerViewAdapter.ViewHolder> {

    private final List<Message> listMessage;
    private final Context mContext;
    private final User currentUser;

    public ChatRecyclerViewAdapter(Context context, User user, List<Message> messages) {
        mContext = context;
        listMessage = messages;
        currentUser = user;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_chat_item,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = listMessage.get(position);
        User userSender = message.getUserSender();

        TextView messageTextView;
        ImageView workmatePicture;

        // Current User or others
        if (userSender.getUid().equals(currentUser.getUid())) {
            holder.view.setVisibility(View.GONE);
            messageTextView = holder.messageTextViewCurrentUser;
            workmatePicture = holder.workmatePictureCurrentUser;
        }
        else {
            holder.viewCurrentUser.setVisibility(View.GONE);
            messageTextView = holder.messageTextView;
            workmatePicture = holder.workmatePicture;
        }

        // If last message is same user
        if (position != 0) {
            if (listMessage.get(position - 1).getUserSender().getUid().equals(userSender.getUid())) {
                workmatePicture.setVisibility(View.INVISIBLE);
            }
        }

        // Init
        messageTextView.setText(message.getMessage());
        if (userSender.getUrlPicture() != null) {
            Glide.with(mContext)
                    .load(message.getUserSender().getUrlPicture())
                    .apply(RequestOptions.circleCropTransform())
                    .into(workmatePicture);
        }
    }

    @Override
    public int getItemCount() {
        return listMessage.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.chat_workmates_sender)
        RelativeLayout view;
        @BindView(R.id.chat_profile_image)
        ImageView workmatePicture;
        @BindView(R.id.chat_message_text_view)
        TextView messageTextView;

        @BindView(R.id.chat_current_user_sender)
        RelativeLayout viewCurrentUser;
        @BindView(R.id.chat_profile_image_current_user)
        ImageView workmatePictureCurrentUser;
        @BindView(R.id.chat_message_text_view_current_user)
        TextView messageTextViewCurrentUser;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
