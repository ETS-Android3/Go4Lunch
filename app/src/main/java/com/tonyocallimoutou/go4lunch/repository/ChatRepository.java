package com.tonyocallimoutou.go4lunch.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.tonyocallimoutou.go4lunch.model.Chat;
import com.tonyocallimoutou.go4lunch.model.Message;
import com.tonyocallimoutou.go4lunch.model.User;
import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;
import com.tonyocallimoutou.go4lunch.utils.UtilChatId;

import java.util.Collections;
import java.util.List;

public class ChatRepository {

    private static final String CHAT_COLLECTION = "chats";
    private static volatile ChatRepository instance;

    private ChatRepository() { }

    public static ChatRepository getInstance() {
        ChatRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized(ChatRepository.class) {
            if (instance == null) {
                instance = new ChatRepository();
            }
            return instance;
        }
    }

    public CollectionReference getChatCollection(){
        return FirebaseFirestore.getInstance().collection(CHAT_COLLECTION);
    }


    public void getAllMessageForChat(Chat chat, MutableLiveData<List<Message>> liveData){
        getChatCollection().document(chat.getId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("TAG", "Listen failed.", error);
                    return;
                }
                Chat chat = value.toObject(Chat.class);
                for (Message message : chat.getMessages()) {
                    Log.d("TAG", "onEvent: "+ message.getIsDelete());
                }
                liveData.setValue(chat.getMessages());
            }
        });
    }

    public void createChat(@Nullable RestaurantDetails restaurant, List<User> users, MutableLiveData<Chat> liveData) {
        String id = UtilChatId.getChatIdWithUsers(restaurant, users);
        getChatCollection().get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                boolean isExisting = false;
                for (DocumentSnapshot document : list) {
                    Chat chatDocument = document.toObject(Chat.class);
                    if (chatDocument.getId().equals(id)) {
                        isExisting = true;
                        liveData.setValue(chatDocument);
                    }
                }
                if (! isExisting) {
                    Chat newChat = new Chat(restaurant, users);
                    getChatCollection().document(newChat.getId()).set(newChat);
                    liveData.setValue(newChat);
                }
            }
        });
    }

    public void createMessagesInChat(String message, User user, Chat chat) {
        Message newMessage = new Message(message,user);

        chat.getMessages().add(newMessage);
        getChatCollection().document(chat.getId()).set(chat);

    }

    public void removeMessageInChat(Message messageToRemove, Chat chat, User userDeleter) {
        Log.d("TAG", "removeMessageInChat: ");

        getChatCollection().document(chat.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Chat chatDocument = document.toObject(Chat.class);
                        for (Message message : chatDocument.getMessages()) {
                            if (message.equals(messageToRemove)) {
                                message.delete(userDeleter);
                                Log.d("TAG", "onComplete: ");
                            }
                        }
                        getChatCollection().document(chat.getId()).set(chatDocument);
                    }
                }
            }
        });
    }

}