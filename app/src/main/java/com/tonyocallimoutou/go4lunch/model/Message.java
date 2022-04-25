package com.tonyocallimoutou.go4lunch.model;

import android.content.Context;

import com.tonyocallimoutou.go4lunch.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class Message {

    private String message;
    private User userSender;
    private Date dateCreated;

    public Message() {
    }

    public Message(String message, User userSender) {
        this.message = message;
        this.userSender = userSender;
        this.dateCreated = new Date();
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
}
