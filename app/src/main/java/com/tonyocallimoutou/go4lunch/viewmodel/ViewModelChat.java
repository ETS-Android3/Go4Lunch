package com.tonyocallimoutou.go4lunch.viewmodel;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tonyocallimoutou.go4lunch.model.Chat;
import com.tonyocallimoutou.go4lunch.model.Message;
import com.tonyocallimoutou.go4lunch.model.User;
import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;
import com.tonyocallimoutou.go4lunch.repository.ChatRepository;
import com.tonyocallimoutou.go4lunch.repository.UserRepository;

import java.util.List;

public class ViewModelChat extends ViewModel {

    private ChatRepository chatRepository;
    private UserRepository userRepository;

    private MutableLiveData<List<Message>> allMessageLiveData = new MutableLiveData<>();
    private MutableLiveData<Chat> currentChatLiveData = new MutableLiveData<>();

    public ViewModelChat(ChatRepository chatRepository, UserRepository userRepository) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
    }

    public void setAllMessageForChat(Chat chat){
        chatRepository.getAllMessageForChat(chat, allMessageLiveData);
    }

    public LiveData<List<Message>> getAllMessage() {
        return allMessageLiveData;
    }

    public void createChat(@Nullable RestaurantDetails restaurant, List<User> users) {
        if (! users.contains(userRepository.getCurrentUser())) {
            users.add(userRepository.getCurrentUser());
        }
        chatRepository.createChat(restaurant,users,currentChatLiveData);
    }

    public LiveData<Chat> getCurrentChatLivedata() {
        return currentChatLiveData;
    }

    public void createMessagesInChat(String message, Chat chat) {
        chatRepository.createMessagesInChat(message, userRepository.getCurrentUser(), chat);
    }

}
