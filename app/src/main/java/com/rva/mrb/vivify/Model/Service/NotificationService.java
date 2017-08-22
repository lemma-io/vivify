package com.rva.mrb.vivify.Model.Service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.rva.mrb.vivify.R;
import com.rva.mrb.vivify.View.Alarm.AlarmActivity;

/**
 * Created by Bao on 8/20/17.
 */

public class NotificationService {
    private NotificationManager mNotificationManager;
    private Context mContext;
    private SharedPreferences sharedPref;

    public NotificationService(Context context) {
        this.mContext = context;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
    }

    public void setNotification(String time, boolean isSnoozed) {
        boolean key = sharedPref.getBoolean("notification_key", true);
        if(key) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
                    .setSmallIcon(R.drawable.drag_clock)
                    .setContentText("Time: " + time)
                    .setAutoCancel(false)
                    .setOngoing(true);
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

    public void cancelNotification() { mNotificationManager.cancelAll(); }
}
