package com.tonyocallimoutou.go4lunch.ui.chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tonyocallimoutou.go4lunch.R;
import com.tonyocallimoutou.go4lunch.model.Chat;
import com.tonyocallimoutou.go4lunch.model.Message;
import com.tonyocallimoutou.go4lunch.model.User;
import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;
import com.tonyocallimoutou.go4lunch.ui.BaseActivity;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelChat;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelFactory;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatActivity extends BaseActivity implements ChatRecyclerViewAdapter.ChatDeleteMessageListener {

    @BindView(R.id.chat_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.chat_edit_text)
    EditText newMessageEditText;
    @BindView(R.id.chat_text_resume)
    TextView chatResume;

    private static LinearLayout layoutErrorConnection;

    private ViewModelChat viewModelChat;
    private ViewModelUser viewModelUser;
    private ChatRecyclerViewAdapter adapter;

    private static boolean isConnected;

    private Chat currentChat;
    private static RestaurantDetails restaurant;
    private static List<User> listReceiver = new ArrayList<>();
    private List<Message> listMessages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModelChat = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(ViewModelChat.class);
        viewModelUser = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(ViewModelUser.class);

        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        layoutErrorConnection = findViewById(R.id.layout_error_connection);
        viewModelChat.createChat(restaurant,listReceiver);

        initChat();
    }

    @Override
    public void deleteMessage(Message message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.chat_title_alert_dialog_delete);
        builder.setMessage(R.string.chat_message_alert_dialog_delete);
        builder.setCancelable(true);

        builder.setPositiveButton(getString(R.string.chat_positive_alert_dialog_delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                viewModelChat.removeMessageInChat(message,currentChat);
            }
        });
        builder.setNegativeButton(getString(R.string.chat_negative_alert_dialog_delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("TAG", "onClick: ");
            }
        });


        AlertDialog alert = builder.create();
        alert.show();
    }

    private static void initLayoutConnection() {
        if (!isConnected) {
            layoutErrorConnection.setVisibility(View.VISIBLE);
        }
        else {
            layoutErrorConnection.setVisibility(View.GONE);
        }
    }

    public void initChat() {

        initTextResume();

        viewModelChat.getCurrentChatLivedata().observe(this, chatResult -> {
            currentChat = chatResult;
            Log.d("TAG", "initChat: " + currentChat.getMessages().size());
            viewModelChat.setAllMessageForChat(currentChat);
        });

        viewModelChat.getAllMessage().observe(this, messageResult -> {
            listMessages = messageResult;
            initRecyclerView();
        });
    }

    public void initRecyclerView() {

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatRecyclerViewAdapter(this, viewModelUser.getCurrentUser(), listMessages, this);
        recyclerView.setAdapter(adapter);

        recyclerView.smoothScrollToPosition(adapter.getItemCount());
    }

    private void initTextResume() {
        if (restaurant != null) {
            String str = getString(R.string.chat_for_restaurant) + " " + restaurant.getName();
            chatResume.setText(str);
        }
        else {
            StringBuilder userStr = new StringBuilder();
            List<User> newList = new ArrayList<>();
            newList.addAll(listReceiver);
            newList.remove(viewModelUser.getCurrentUser());

            for (User user : newList) {
                userStr.append(user.getUsername());
                userStr.append(", ");
            }
            String str = getString(R.string.chat_with_user) + " " + userStr;
            chatResume.setText(str);
        }
    }


    @OnClick(R.id.chat_send_button)
    public void sendMessage() {
        if (!TextUtils.isEmpty(newMessageEditText.getText())) {
            String message = newMessageEditText.getText().toString();

            if (isConnected) {
                viewModelChat.createMessagesInChat(message, currentChat);
            }
            else {
                Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
            }

            hideKeyboard();
        }
    }

    private void hideKeyboard() {

        newMessageEditText.setText("");
        InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }


    public static void navigate(Activity activity, List<User> result , @Nullable RestaurantDetails restaurantResult) {
        restaurant = restaurantResult;
        listReceiver = result;
        Intent intent = new Intent(activity, ChatActivity.class);
        ActivityCompat.startActivity(activity, intent, null);
    }

    public static void initConnection(boolean result) {
        isConnected = result;
        if (layoutErrorConnection != null) {
            initLayoutConnection();
        }
    }


}