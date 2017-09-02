package com.rva.mrb.vivify.View.Alarm;

import android.content.Context;

import com.rva.mrb.vivify.Model.Data.Alarm;
import com.rva.mrb.vivify.Model.Service.AlarmScheduler;
import com.rva.mrb.vivify.Model.Service.NotificationService;
import com.rva.mrb.vivify.Model.Service.RealmService;

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

    @Override
    public void checkMissedAlarms(Context context, NotificationService notificationService){
        List<Alarm> missedAlarms = mRealmService.getMissedAlarms();
        List<Alarm> missedSnoozed = mRealmService.getMissedSnoozedAlarms();
        for (Alarm a : missedAlarms){

            notificationService.setMissedAlarmNotification(a.getTime() + "", a.isSnoozed());
        }
        for (Alarm alarm : missedSnoozed){
            notificationService.setMissedAlarmNotification(alarm.getTime() + "", alarm.isSnoozed());
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
