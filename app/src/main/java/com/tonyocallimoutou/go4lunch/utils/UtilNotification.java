package com.tonyocallimoutou.go4lunch.utils;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.tonyocallimoutou.go4lunch.MainActivity;
import com.tonyocallimoutou.go4lunch.R;
import com.tonyocallimoutou.go4lunch.model.User;
import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;
import com.tonyocallimoutou.go4lunch.ui.detail.DetailsActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UtilNotification {

    private static List<User> workmates;
    private static List<RestaurantDetails> restaurants;
    private static RestaurantDetails bookedRestaurant;
    private static User currentUser;
    private static Context context;

    private static String title;
    private static String content;
    private static int hour;
    private static boolean isBooked;

    public UtilNotification() {
        setNotification();
    }

    public static UtilNotification newInstance(@Nullable List<User> users,
                                               @Nullable List<RestaurantDetails> listRestaurants,
                                               @Nullable Context c,
                                               @Nullable User user) {
        if (users != null) {
            workmates = new ArrayList<>();
            workmates = users;
        }
        if (listRestaurants != null) {
            restaurants = new ArrayList<>();
            restaurants = listRestaurants;
        }
        if (c != null) {
            context = c;
        }
        if (user != null) {
            currentUser = user;
        }


        if (workmates != null && restaurants != null && context != null && currentUser != null) {
            return new UtilNotification();
        }
        return null;
    }



    private static void setNotification() {
        Log.d("TAG", "setNotification: " );


        if (currentUser.getBookedRestaurant() == null) {
            setNoRestaurant();
        }
        else {
            setRestaurant();
        }

        initNotification();
    }

    private static void setNoRestaurant() {
        title = context.getString(R.string.notification_title_no_restaurant);
        content = context.getString(R.string.notification_content_no_restaurant);
        hour = 11;
        isBooked = false;
    }

    private static void setRestaurant() {
        title = context.getString(R.string.notification_title);
        hour = 12;
        isBooked = true;

        for (RestaurantDetails result : restaurants) {
            if (result.getPlaceId().equals(currentUser.getBookedRestaurant().getPlaceId())) {
                bookedRestaurant = result;
            }
        }

        List<User> workmateLunch = WorkmatesLunch.getWorkmatesLunch(bookedRestaurant,workmates);
        User userToRemove = new User();
        for (User user: workmateLunch) {
            if (user.getUid().equals(currentUser.getUid())) {
                userToRemove = user;
            }
        }
        workmateLunch.remove(userToRemove);

        String strWorkmate = "";

        if (workmateLunch.size() == 0) {
            strWorkmate = context.getString(R.string.notification_content_no_workmates);
        }
        else {
            for (User user : workmateLunch) {
                strWorkmate += user.getUsername() + " ";
            }
        }

        content = bookedRestaurant.getName() + "-" + bookedRestaurant.getVicinity() +"\n" + strWorkmate;
    }

    private static void initNotification() {

        Intent notificationIntent = new Intent( context, NotificationReceiver. class ) ;
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION_TITLE, title);
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION_CONTENT, content);
        notificationIntent.putExtra(NotificationReceiver.BOOLEAN_IS_BOOKED,isBooked);
        Log.d("TAG", "initNotification: " + content);
        PendingIntent pendingIntent = PendingIntent. getBroadcast ( context, 0 , notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE ) ;


        Calendar now = Calendar.getInstance();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,hour);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);


        if (!now.before(cal)) {
            cal.set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR) + 1);
        }

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}
