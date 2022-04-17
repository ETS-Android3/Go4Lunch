package com.tonyocallimoutou.go4lunch.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModelProvider;

import com.tonyocallimoutou.go4lunch.MainActivity;
import com.tonyocallimoutou.go4lunch.R;
import com.tonyocallimoutou.go4lunch.model.places.RestaurantDetails;
import com.tonyocallimoutou.go4lunch.ui.detail.DetailsActivity;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelFactory;
import com.tonyocallimoutou.go4lunch.viewmodel.ViewModelRestaurant;

public class NotificationReceiver extends BroadcastReceiver {

    public static final String CHANNEL_ID = "CHANNEL_1";
    public static final String BOOLEAN_IS_BOOKED = "IS_BOOKED";
    public static final String NOTIFICATION_TITLE = "TITLE";
    public static final String NOTIFICATION_CONTENT = "CONTENT";


    private Context context;
    private boolean isBooked;
    private String title;
    private String content;
    private Notification notification;


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("TAG", "onReceive: ");
        this.context = context;

        isBooked = intent.getBooleanExtra(BOOLEAN_IS_BOOKED,false);
        title = intent.getStringExtra(NOTIFICATION_TITLE);
        content = intent.getStringExtra(NOTIFICATION_CONTENT);
        Log.d("TAG", "onReceive: " + intent.getStringExtra(NOTIFICATION_CONTENT));

        initNotification();
        getNotification();
    }

    private void initNotification() {
        Intent activityIntent;
        if (isBooked) {
            activityIntent = new Intent(context, DetailsActivity.class);
            activityIntent.putExtra(DetailsActivity.KEY_EXTRA_DETAIL_ACTIVITY, true);
        }
        else {
            activityIntent = new Intent(context, MainActivity.class);
        }
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(context, 0, activityIntent, PendingIntent.FLAG_MUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationReceiver.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_person_outline_black_24dp)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(content))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true);

        notification = builder.build();
    }

    private void getNotification() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            CharSequence name = context.getString(R.string.notification_channel_name);
            String description = context.getString(R.string.notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationChannel.setDescription(description);
            notificationManager.createNotificationChannel(notificationChannel) ;

            notificationManager.notify(1 , notification) ;
        }
    }

}
