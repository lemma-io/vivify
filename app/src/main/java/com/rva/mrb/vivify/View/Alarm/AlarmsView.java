package com.rva.mrb.vivify.View.Alarm;

import com.rva.mrb.vivify.Model.Data.Alarm;

import io.realm.RealmResults;

public interface AlarmsView {

    void showAlarms(RealmResults<Alarm> alarms);
    void showAddNewAlarmView();

    class EmptyAlarmsList implements AlarmsView {

        @Override
        public void showAlarms(RealmResults<Alarm> alarms) {

        }

        @Override
        public void showAddNewAlarmView(){}
    }
}
