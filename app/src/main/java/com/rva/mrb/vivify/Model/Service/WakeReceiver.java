package com.rva.mrb.vivify.Model.Service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

import com.rva.mrb.vivify.AlarmApplication;
import com.rva.mrb.vivify.ApplicationModule;

import javax.inject.Inject;

public class WakeReceiver extends WakefulBroadcastReceiver {
//    @Inject
//    RealmService realmService;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("Reciever", "Something happened?");
        Toast.makeText(context, "AlarmManager Worked!!", Toast.LENGTH_LONG).show();

//        Log.d("Realm", realmService.getMessage());
        Bundle extras = intent.getExtras();
        String trackId = (String) extras.get("trackId");
        String trackImage = (String) extras.get("trackImage");
        String alarmId = (String) extras.get("alarmId");

        Intent alert = new Intent(context, WakeService.class);
        alert.putExtra("trackId", trackId);
        alert.putExtra("trackImage", trackImage);
        alert.putExtra("alarmId", alarmId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(alert);
    }
}
