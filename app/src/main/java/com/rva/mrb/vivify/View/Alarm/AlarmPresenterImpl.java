package com.rva.mrb.vivify.View.Alarm;

import android.content.Context;

import com.rva.mrb.vivify.Model.Data.Alarm;
import com.rva.mrb.vivify.Model.Service.AlarmScheduler;
import com.rva.mrb.vivify.Model.Service.NotificationService;
import com.rva.mrb.vivify.Model.Service.RealmService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.RealmResults;

public class AlarmPresenterImpl implements AlarmsPresenter {

    private final RealmService mRealmService;

    private AlarmsView mAlarmsView = new AlarmsView.EmptyAlarmsList();
    private boolean alarmsWereShown = false;

    public AlarmPresenterImpl(RealmService realmService){ mRealmService = realmService; }

    public String getRSMessage(){
        return mRealmService.getMessage();
    }

    @Override
    public RealmResults<Alarm> getAllAlarms() {
        return mRealmService.getAllAlarms().sort("timeOfDay");
    }

    public void disableMissedAlarm(Alarm alarm){
        mRealmService.disableAlarmById(alarm.getId());
    }

    public Alarm getNextAlarmTime() {
        return mRealmService.getNextAlarm();
    }

    public String getTimeTillPendingAlarm(String notice) {
        return null;
    }

    public String getMessage(){
        return "SUCESSFULL!!!";
    }

    @Override
    public void onAddNewAlarm() {
        mAlarmsView.showAddNewAlarmView();
    }

    @Override
    public void setView(AlarmsView view) {
        mAlarmsView = view;
        if(!alarmsWereShown) {
            mAlarmsView.showAlarms(mRealmService.getAllAlarms());
            alarmsWereShown = true;
        }
    }

    public String prettyDateFormat(Date alarmTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(alarmTime);
        String daySuffix = getDaySuffix(cal.get(Calendar.DAY_OF_MONTH));
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d'" + daySuffix + "' hh:mm a");
        return sdf.format(alarmTime.getTime());
    }

    private String getDaySuffix(int day) {
        if (day >= 11 && day <= 13)
            return "th";
        switch (day % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    @Override
    public void checkMissedAlarms(Context context, NotificationService notificationService){
        List<Alarm> missedAlarms = mRealmService.getMissedAlarms();
        List<Alarm> missedSnoozed = mRealmService.getMissedSnoozedAlarms();
        for (Alarm a : missedAlarms){
            notificationService.setMissedAlarmNotification(prettyDateFormat(a.getTime()) + "", a.isSnoozed());
        }
        for (Alarm alarm : missedSnoozed){
            notificationService.setMissedAlarmNotification(prettyDateFormat(alarm.getTime()) + "", alarm.isSnoozed());
        }
        mRealmService.disableMissedAlarms();
        mRealmService.disableMissedSnoozed();
        //AlarmScheduler.cancelSnoozedAlarm(context);
    }

    @Override
    public void clearView() {
        mAlarmsView = new AlarmsView.EmptyAlarmsList();
    }

    @Override
    public void closeRealm() {
        mRealmService.closeRealm();
    }
}
