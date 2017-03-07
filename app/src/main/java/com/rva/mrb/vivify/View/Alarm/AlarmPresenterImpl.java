package com.rva.mrb.vivify.View.Alarm;

import com.rva.mrb.vivify.Model.Data.Alarm;
import com.rva.mrb.vivify.Model.Service.RealmService;

import io.realm.RealmResults;

public class AlarmPresenterImpl implements AlarmsPresenter {

    private final RealmService mRealmService;

    private AlarmsView mAlarmsView = new AlarmsView.EmptyAlarmsList();
    private boolean alarmsWereShown = false;

    public AlarmPresenterImpl(RealmService realmService){
        mRealmService = realmService;
    }

    public String getRSMessage(){
        return mRealmService.getMessage();
    }

    @Override
    public RealmResults<Alarm> getAllAlarms() {
//        RealmList<Alarm> alarms = new RealmList<Alarm>();
//        for (Alarm alarm : mRealmService.getAllAlarms())
//            alarms.add(alarm);
        return mRealmService.getAllAlarms();
    }


    public String getNextAlarmTime() {
        return mRealmService.getNextAlarm();
    }

    public String getTimeTillPendingAlarm(String notice) {
        return null;
    }

    public String getMessage(){
        return "SUCESSFULL!!!";
    }

//    @Override
//    public void onAlarmClick(int id) {
//
//    }

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
    public void clearView() {
        mAlarmsView = new AlarmsView.EmptyAlarmsList();
    }

    @Override
    public void closeRealm() {
        mRealmService.closeRealm();
    }
}
