package com.tonyocallimoutou.go4lunch.model;

import android.content.Context;

import androidx.annotation.Nullable;

import com.tonyocallimoutou.go4lunch.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class Message {

    private String message;
    private User userSender;
    private Date dateCreated;
    private boolean isDelete;
    @Nullable
    private User userDeleter;

    public Message() {
    }

    public Message(String message, User userSender) {
        this.message = message;
        this.userSender = userSender;
        this.dateCreated = new Date();
        isDelete = false;
        userDeleter = null;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUserSender() {
        return userSender;
    }

    public void setUserSender(User userSender) {
        this.userSender = userSender;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateString(Context context) {
        String str = context.getString(R.string.chat_message_info);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd ',' HH:mm");


        return str + " " +dateFormat.format(dateCreated.getTime());
    }

    public boolean getIsDelete() {
        return isDelete;
    }

    public void delete(User user) {
        this.userDeleter = user;
        this.isDelete = true;
    }

    @Nullable
    public User getUserDeleter() {
        return userDeleter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message1 = (Message) o;
        return isDelete == message1.isDelete && Objects.equals(message, message1.message) && Objects.equals(userSender, message1.userSender) && Objects.equals(dateCreated, message1.dateCreated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, userSender, dateCreated, isDelete);
    }
}
