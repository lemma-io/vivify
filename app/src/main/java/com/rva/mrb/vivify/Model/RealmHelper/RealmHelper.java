package com.rva.mrb.vivify.Model.RealmHelper;

import android.util.Log;

import com.rva.mrb.vivify.Model.Data.Alarm;
import com.rva.mrb.vivify.Model.Service.RealmService;

public class RealmHelper{

    private RealmService realmService;
    public RealmHelper(RealmService realmService) {
        this.realmService = realmService;
    }

    public Alarm getAlarmById(String id) {
        return realmService.getAlarm(id);
    }

    public Alarm getNextEnabledAlarm() {
        Log.d("RealmHelper", "AlarmTime: " + realmService.getNextPendingAlarm().getTime());
        return realmService.getNextPendingAlarm();
    }

    public String message() {
        return realmService.getMessage();
    }
}
