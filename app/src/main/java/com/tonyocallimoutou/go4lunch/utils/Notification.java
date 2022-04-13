package com.tonyocallimoutou.go4lunch.utils;

import static android.content.Context.ALARM_SERVICE;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tonyocallimoutou.go4lunch.MainActivity;
import com.tonyocallimoutou.go4lunch.R;
import com.tonyocallimoutou.go4lunch.model.User;
import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;
import com.tonyocallimoutou.go4lunch.ui.detail.DetailsActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Notification {
    private static List<User> workmates = new ArrayList<>();
    private static User currentUser;
    private static Context context;

    private static String title;
    private static String content;
    private static int hour;

    private static final String KEY_TITLE = "TITLE";
    private static final String KEY_CONTENT = "CONTENT";
    private static final String KEY_GO_TO_DETAIL = "DESTINATION";
    private static boolean isbooked;

    private Notification(List<User> users) {
        workmates = users;
    }

    public static Notification newInstance(List<User> users) {
        return new Notification(users);
    }

    public static void setNotification(Context c, User user) {
        currentUser = user;
        context = c;


        if (currentUser.getBookedRestaurant() == null) {
            setNoRestaurant();
        }
        else {
            setRestaurant();
        }

        Intent alarmIntent = new Intent(context, NotificationReceiver.class);
        alarmIntent.putExtra(KEY_TITLE, title);
        alarmIntent.putExtra(KEY_CONTENT, content);
        alarmIntent.putExtra(KEY_GO_TO_DETAIL,isbooked);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_MUTABLE);

        Calendar now = Calendar.getInstance();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 7);
        cal.set(Calendar.MINUTE, 48);
        cal.set(Calendar.SECOND, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (!now.before(cal)) {
            cal.set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR) + 1);
        }

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    private static void setNoRestaurant() {
        title = context.getString(R.string.notification_title_no_restaurant);
        content = context.getString(R.string.notification_content_no_restaurant);
        hour = 11;
        isbooked = false;
    }

    private static void setRestaurant() {
        hour = 12;
        isbooked = true;
        title = context.getString(R.string.notification_title);

        RestaurantDetails restaurant = currentUser.getBookedRestaurant();
        List<User> workmateLunch = WorkmatesLunch.getWorkmatesLunch(restaurant,workmates);
        User userToRemove = new User();
        for (User user: workmateLunch) {
            if (user.getUid().equals(currentUser.getUid())) {
                userToRemove = user;
            }
        }
        workmateLunch.remove(userToRemove);

        String strWorkmate = "";

        if (workmates.size() == 0) {
            strWorkmate = context.getString(R.string.notification_content_no_workmates);
        }
        else {
            for (User user : workmateLunch) {
                strWorkmate += user.getUsername() + " ";
            }
        }

        content = restaurant.getName() + "-" + restaurant.getVicinity() + '\n';
    }
}
