package com.tonyocallimoutou.go4lunch.ui.chat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.tonyocallimoutou.go4lunch.R;
import com.tonyocallimoutou.go4lunch.model.Chat;
import com.tonyocallimoutou.go4lunch.model.Message;
import com.tonyocallimoutou.go4lunch.model.User;
import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;
import com.tonyocallimoutou.go4lunch.ui.detail.DetailsActivity;
import com.tonyocallimoutou.go4lunch.ui.workmates.WorkmatesRecyclerViewAdapter;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelChat;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelFactory;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatActivity extends AppCompatActivity {

    @BindView(R.id.chat_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.chat_edit_text)
    EditText newMessageEditText;

    private ViewModelChat viewModelChat;
    private ViewModelUser viewModelUser;
    private ChatRecyclerViewAdapter adapter;

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

        viewModelChat.createChat(restaurant,listReceiver);

        initRecyclerView();
    }

    public void initRecyclerView() {

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        initChat();
    }


    public void initChat() {

        viewModelChat.getCurrentChatLivedata().observe(this, chatResult -> {
            currentChat = chatResult;
            viewModelChat.setAllMessageForChat(currentChat);
        });

        viewModelChat.getAllMessage().observe(this, messageResult -> {
            listMessages = messageResult;
            adapter = new ChatRecyclerViewAdapter(this, viewModelUser.getCurrentUser(), listMessages);
            recyclerView.setAdapter(adapter);

            recyclerView.smoothScrollToPosition(adapter.getItemCount());
        });
    }


    @OnClick(R.id.chat_send_button)
    public void sendMessage() {
        if (!TextUtils.isEmpty(newMessageEditText.getText())) {
            String message = newMessageEditText.getText().toString();
            Log.d("TAG", "sendMessage: " + message);

            viewModelChat.createMessagesInChat(message, currentChat);

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


}