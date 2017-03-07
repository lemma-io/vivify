package com.rva.mrb.vivify.Model.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.rva.mrb.vivify.View.Wake.WakeActivity;

public class WakeService extends Service {

    private Intent alertIntent;
    private AlarmBinder alarmBinder = new AlarmBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Service", "Creating Service");
        alertIntent = new Intent(getApplicationContext(), WakeActivity.class);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("Service", "Bind Service");
        return alarmBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        super.onStartCommand(intent, flag, startId);
        Log.d("Service", "Starting Service");
//        Intent intent1 = new Intent(getApplicationContext(), sdfAlertActivity.class);

        if (intent != null) {
            // TODO since were are not doing anything with these now only pass an id
            Bundle extras = intent.getExtras();
            String trackId = (String) extras.get("trackId");
            String trackImage = (String) extras.get("trackImage");
            String alarmId = (String) extras.get("alarmId");
            alertIntent.putExtra("trackId", trackId);
            alertIntent.putExtra("trackImage", trackImage);
            alertIntent.putExtra("alarmId", alarmId);
            alertIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(alertIntent);
        }
//        else
//            return START_NOT_STICKY;
        return START_STICKY;


    }
    public class AlarmBinder extends Binder {
        public WakeService getService() {
            return WakeService.this;
        }
    }
}
