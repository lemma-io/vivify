package com.rva.mrb.vivify.View.Alarm;

import android.content.Context;

import com.rva.mrb.vivify.BasePresenter;
import com.rva.mrb.vivify.Model.Data.Alarm;
import com.rva.mrb.vivify.Model.Service.NotificationService;

import java.util.Date;

import io.realm.RealmResults;

public interface AlarmsPresenter extends BasePresenter<AlarmsView> {
    void onAddNewAlarm();
    RealmResults<Alarm> getAllAlarms();
    void checkMissedAlarms(Context context, NotificationService notificationService);
    Alarm getNextAlarmTime();
    void disableMissedAlarm(Alarm alarm);
    String getTimeTillPendingAlarm(String notice);
    String prettyDateFormat(Date alarmTime);
//    void onAlarmClick(int id);
//    String getMessage();
//    String getRSMessage();

}
