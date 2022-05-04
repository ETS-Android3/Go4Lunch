package com.tonyocallimoutou.go4lunch.ui.chat;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

    private List<Message> listMessage;
    private final Context mContext;
    private final User currentUser;
    private final ChatDeleteMessageListener chatDeleteMessageListener;

    public ChatRecyclerViewAdapter(Context context, User user, List<Message> messages, ChatDeleteMessageListener listener) {
        mContext = context;
        listMessage = messages;
        currentUser = user;
        chatDeleteMessageListener = listener;
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


        ImageView workmatePicture;
        LinearLayout containerMessage;
        TextView messageTextView;
        TextView messageInfo;
        ImageView removeMessageUser;
        ImageView copyPaste;
        LinearLayout containerMoreAction;

        // Current User or others
        if (userSender.getUid().equals(currentUser.getUid())) {
            holder.view.setVisibility(View.GONE);
            containerMessage = holder.containerMessageCurrentUser;
            messageTextView = holder.messageTextViewCurrentUser;
            workmatePicture = holder.workmatePictureCurrentUser;
            messageInfo = holder.messageInfoCurrentUser;
            containerMoreAction = holder.containerMoreActionCurrentUser;
            removeMessageUser = holder.removeMessageCurrentUser;
            copyPaste = holder.copyPasteCurrentUser;

        }
        else {
            holder.viewCurrentUser.setVisibility(View.GONE);
            containerMessage = holder.containerMessage;
            messageTextView = holder.messageTextView;
            workmatePicture = holder.workmatePicture;
            messageInfo = holder.messageInfo;
            containerMoreAction = holder.containerMoreAction;
            removeMessageUser =holder.removeMessage;
            copyPaste = holder.copyPaste;
        }


        // If last message is same user
        if (position != 0) {
            if (listMessage.get(position - 1).getUserSender().getUid().equals(userSender.getUid())) {
                workmatePicture.setVisibility(View.INVISIBLE);
            }
        }

        // Init
        messageTextView.setText(message.getMessage());
        messageInfo.setText(message.getDateString(mContext));
        if (userSender.getUrlPicture() != null) {
            Glide.with(mContext)
                    .load(message.getUserSender().getUrlPicture())
                    .apply(RequestOptions.circleCropTransform())
                    .into(workmatePicture);
        }

        // If message is delete
        if (message.getIsDelete()) {
            String str = mContext.getString(R.string.chat_message_delete_by) + " " + message.getUserDeleter().getUsername();
            messageTextView.setText(str);
            int backgroundColor;
            if (userSender.getUid().equals(currentUser.getUid())) {
                backgroundColor = R.color.grey_message_delete;
            }
            else {
                backgroundColor = R.color.light_grey_message_delete;
            }
            containerMessage.getBackground().setTint(mContext.getResources().getColor(backgroundColor));
        }
        // ON Click On message
        else {
            messageTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (containerMoreAction.getVisibility() == View.VISIBLE) {
                        containerMoreAction.setVisibility(View.GONE);
                    }
                    else {
                        if (messageInfo.getVisibility() == View.GONE) {
                            messageInfo.setVisibility(View.VISIBLE);
                        } else {
                            messageInfo.setVisibility(View.GONE);
                        }
                    }
                }
            });

            messageTextView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    messageInfo.setVisibility(View.GONE);
                    if (containerMoreAction.getVisibility() == View.GONE) {
                        containerMoreAction.setVisibility(View.VISIBLE);
                    }
                    else {
                        containerMoreAction.setVisibility(View.GONE);
                    }
                    return true;
                }
            });
        }

        // Remove and Copy Paste

        removeMessageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatDeleteMessageListener.deleteMessage(message);
            }
        });

        copyPaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager)mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("copy",message.getMessage());
                clipboard.setPrimaryClip(clip);
                containerMoreAction.setVisibility(View.GONE);

                Toast.makeText(mContext, mContext.getString(R.string.chat_copy_paste_toast), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listMessage.size();
    }

    public interface ChatDeleteMessageListener {
        void deleteMessage(Message message);
    }

    public void initAdapter(List<Message> messages) {
        listMessage = messages;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.chat_workmates_sender)
        RelativeLayout view;
        @BindView(R.id.chat_profile_image)
        ImageView workmatePicture;
        @BindView(R.id.chat_container_message)
        LinearLayout containerMessage;
        @BindView(R.id.chat_message_text_view)
        TextView messageTextView;
        @BindView(R.id.chat_message_info)
        TextView messageInfo;
        @BindView(R.id.chat_container_more_action)
        LinearLayout containerMoreAction;
        @BindView(R.id.chat_message_remove)
        ImageView removeMessage;
        @BindView(R.id.chat_message_copy_paste)
        ImageView copyPaste;

        @BindView(R.id.chat_current_user_sender)
        RelativeLayout viewCurrentUser;
        @BindView(R.id.chat_profile_image_current_user)
        ImageView workmatePictureCurrentUser;
        @BindView(R.id.chat_container_message_current_user)
        LinearLayout containerMessageCurrentUser;
        @BindView(R.id.chat_message_text_view_current_user)
        TextView messageTextViewCurrentUser;
        @BindView(R.id.chat_message_info_current_user)
        TextView messageInfoCurrentUser;
        @BindView(R.id.chat_container_more_action_current_user)
        LinearLayout containerMoreActionCurrentUser;
        @BindView(R.id.chat_message_remove_current_user)
        ImageView removeMessageCurrentUser;
        @BindView(R.id.chat_message_copy_paste_current_user)
        ImageView copyPasteCurrentUser;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
