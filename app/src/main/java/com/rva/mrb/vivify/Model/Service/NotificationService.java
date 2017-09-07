package com.rva.mrb.vivify.Model.Service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.rva.mrb.vivify.R;
import com.rva.mrb.vivify.View.Alarm.AlarmActivity;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Bao on 8/20/17.
 */

public class NotificationService {
    private NotificationManager mNotificationManager;
    private Context mContext;
    private SharedPreferences sharedPref;


    private static final AtomicInteger counter = new AtomicInteger(2);

    public static int nextValue() {
        return counter.getAndIncrement();
    }


    public NotificationService(Context context) {
        this.mContext = context;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
    }

    public void setNotification(String time, boolean isSnoozed) {
        boolean key = sharedPref.getBoolean("notification_key", true);
        if(key) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
                    .setSmallIcon(R.drawable.ic_vivify_notification)
                    .setContentText("Time: " + time)
                    .setAutoCancel(false)
                    .setOngoing(true)
                    .setShowWhen(false);
            Intent notificationIntent = new Intent(mContext, AlarmActivity.class);
            PendingIntent intent = PendingIntent.getActivity(mContext, 0,
                    notificationIntent, 0);
            if (isSnoozed) {
                builder.setContentTitle("Snoozed Alarm");
            } else {
                builder.setContentTitle("Upcoming Alarm");
            }
            builder.setContentIntent(intent);

            mNotificationManager.notify(1, builder.build());
        }
        else {
            cancelNotification();
        }
    }

    public void cancelNotification() { mNotificationManager.cancel(1); }

    public void setMissedAlarmNotification(String time, boolean isSnoozed){
        boolean key = sharedPref.getBoolean("notification_key", true);
        if(key) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
                    .setSmallIcon(R.drawable.ic_vivify_notification)
                    .setContentText("Time: " + time)
                    .setAutoCancel(true)
                    .setShowWhen(false);
            Intent notificationIntent = new Intent(mContext, AlarmActivity.class);
            PendingIntent intent = PendingIntent.getActivity(mContext, 0,
                    notificationIntent, 0);
            if (isSnoozed) {
                builder.setContentTitle("Missed Snoozed Alarm!");
            } else {
                builder.setContentTitle("Missed Alarm!");
            }
            builder.setContentIntent(intent);

            mNotificationManager.notify(nextValue(), builder.build());
        }
    }
}
