package com.tonyocallimoutou.go4lunch.viewmodel;

import android.util.Log;

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
import com.tonyocallimoutou.go4lunch.utils.UtilChatId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewModelChat extends ViewModel {

    private ChatRepository chatRepository;
    private UserRepository userRepository;

    private MutableLiveData<Chat> currentChatLiveData = new MutableLiveData<>();
    private MutableLiveData<Map<String,Integer>> mapLiveData = new MutableLiveData<>();
    private Map<String,MutableLiveData<List<Message>>> mapListMessageLiveData = new HashMap<>();

    public ViewModelChat(ChatRepository chatRepository, UserRepository userRepository) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
    }

    public LiveData<List<Message>> getAllMessage(Chat chat) {
        return mapListMessageLiveData.get(chat.getId());
    }

    public void readMessage(Chat chat) {
        chatRepository.readMessage(userRepository.getCurrentUser(),chat);
    }
    public void createChat(@Nullable RestaurantDetails restaurant, List<User> users) {
        if (! users.contains(userRepository.getCurrentUser()) && userRepository.getCurrentUser() != null) {
            users.add(userRepository.getCurrentUser());
        }
        String id = UtilChatId.getChatIdWithUsers(restaurant, users);
        if (mapListMessageLiveData.get(id) == null) {
            mapListMessageLiveData.put(id, new MutableLiveData<>());
        }
        chatRepository.createChat(restaurant,users,currentChatLiveData,mapListMessageLiveData.get(id));
    }

    public LiveData<Chat> getCurrentChatLivedata() {
        return currentChatLiveData;
    }

    public void createMessagesInChat(String message, Chat chat) {
        chatRepository.createMessagesInChat(message, userRepository.getCurrentUser(), chat);
    }

    public void removeMessageInChat(Message messageToRemove, Chat chat) {
        chatRepository.removeMessageInChat(messageToRemove,chat, userRepository.getCurrentUser());
    }

    public void setPinsNoReadingMessage(User currentUser) {
        chatRepository.setPinsNoReadingMessage(currentUser,mapLiveData);
    }

    public MutableLiveData<Map<String, Integer>> getNumberNoReadingMessageMap() {
        return mapLiveData;
    }
}
