package com.tonyocallimoutou.go4lunch.utils;

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

    private final String CHANNEL_ID = "CHANNEL_1";
    private final String KEY_EXTRA_DETAIL_ACTIVITY = "KEY_EXTRA_DETAIL_ACTIVITY";

    private final String KEY_TITLE = "TITLE";
    private final String KEY_CONTENT = "CONTENT";
    private static final String KEY_GO_TO_DETAIL = "DESTINATION";

    private String title;
    private String content;
    private boolean isBooked;


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("TAG", "onReceive: ");

        title = intent.getExtras().getString(KEY_TITLE);
        content = intent.getExtras().getString(KEY_CONTENT);
        isBooked = intent.getExtras().getBoolean(KEY_GO_TO_DETAIL);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            CharSequence name = context.getString(R.string.notification_channel_name);
            String description = context.getString(R.string.notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            Intent detailIntent;
            if (isBooked) {
                detailIntent = new Intent(context, DetailsActivity.class);
            }
            else {
                detailIntent = new Intent(context, MainActivity.class);
            }
            detailIntent.putExtra(KEY_EXTRA_DETAIL_ACTIVITY, "NOTIFICATION");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_person_outline_black_24dp)
                    .setContentTitle(title)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(content))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);


            int notificationId = 1;
            notificationManager.notify(notificationId, builder.build());
        }
    }

}
