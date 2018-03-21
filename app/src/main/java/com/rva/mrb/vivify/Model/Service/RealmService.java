package com.rva.mrb.vivify.Model.Service;

import android.util.Log;

import com.rva.mrb.vivify.Model.Data.Alarm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;

public class RealmService {

    public static final String TAG = RealmService.class.getSimpleName();

    private final Realm mRealm;

    public RealmService(final Realm realm) {
        mRealm = realm;
    }

    public RealmResults<Alarm> getAllAlarms() {
        return mRealm.where(Alarm.class).findAll();
    }

    public Alarm getAlarm(final String alarmId) {
        Log.d(TAG, "Alarm id: " + mRealm.where(Alarm.class)
                .equalTo("id", alarmId).findFirst().getId());
        return mRealm.where(Alarm.class).equalTo("id", alarmId).findFirst();
    }

    public static Alarm getAlarmById(final String alarmId) {
        final Realm realm = Realm.getDefaultInstance();
        Log.d(TAG, "Alarm id: " + realm.where(Alarm.class)
                .equalTo("id", alarmId).findFirst().getId());
        Log.d(TAG, "Alarm time: " + realm.where(Alarm.class)
                .equalTo("id", alarmId).findFirst().getTime());
        return realm.where(Alarm.class).equalTo("id", alarmId).findFirst();
    }

    public String getNewestAlarmId() {
        return mRealm.where(Alarm.class).findAllSorted("createdAt").first().getId();
    }
    public Alarm getNewestAlarm() {
        return mRealm.where(Alarm.class).findAllSorted("createdAt").first();
    }

    public static Alarm getNextPendingAlarm() {
        final Realm realm = Realm.getDefaultInstance();
        Log.d(TAG, "Number of Alarm objects " +
                realm.where(Alarm.class).findAll().size());
        Log.d(TAG, "Number of enabled Alarm objects " +
                realm.where(Alarm.class).equalTo("enabled", true).findAll().size());
        Log.d(TAG, "Pending alarm date: " +
                realm.where(Alarm.class).equalTo("enabled", true)
                        .findAllSorted("time").first().getTime());
        return realm.where(Alarm.class).equalTo("enabled", true).equalTo("snoozed", false)
                .findAllSorted("time").first();
    }

    public static Alarm getNextSnoozedAlarm() {
        final Realm realm = Realm.getDefaultInstance();
        return realm.where(Alarm.class).equalTo("snoozed", true).findAllSorted("snoozedAt").first();
    }

    public static void enableAlarm(final String alarmId) {
        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Alarm alarm = realm.where(Alarm.class).equalTo("id", alarmId).findFirst();
                Log.d(TAG, "Alarm is: " + alarm.isEnabled());
                alarm.setEnabled(!alarm.isEnabled());
                alarm.setSnoozed(false);
            }
        });

        updateAlarms();
    }

    public static void updateAlarms() {
        Log.d(TAG, "Updating Alarms");
        final Realm realm = Realm.getDefaultInstance();
        RealmResults<Alarm> enabledAlarms = realm.where(Alarm.class).equalTo("enabled", true).findAll();
        Log.d(TAG, "Number of enabled Alarms " + enabledAlarms.size());

        Log.d(TAG, "Current Date:" + Calendar.getInstance().getTime());
        RealmResults<Alarm> enabledOldAlarms = realm.where(Alarm.class).equalTo("enabled", true)
                .lessThan("time", Calendar.getInstance().getTime()).findAll();
        Log.d(TAG, "Number of enabled old Alarms " + enabledAlarms.size());

        // find all enabled alarms whose Times are old
        RealmResults<Alarm> oldAlarms = realm.where(Alarm.class).equalTo("enabled", true)
                .lessThan("time", Calendar.getInstance().getTime()).findAll();
        Log.d(TAG, "First Alarm Time is: " + enabledAlarms.size());
        for(final Alarm update : oldAlarms) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    update.updateTime();
                    Log.d(TAG, "Update Alarm Time is: " + update.getTime());
                }
            });
        }
    }
    // Return the the newest alarm by pulling the highest alarm id
    public Alarm getNextAlarm() {
        if (mRealm.where(Alarm.class).equalTo("enabled",true).findAll().size() > 0) {
            if (mRealm.where(Alarm.class).equalTo("snoozed",true).findAll().size() > 0) {
                if (mRealm.where(Alarm.class).equalTo("enabled", true).findAll()
                        .sort("time").first().getTime().after(mRealm.where(Alarm.class)
                                .equalTo("enabled", true)
                                .equalTo("snoozed", true).findAll()
                                .sort("snoozedAt").first().getSnoozedAt())) {
                    return mRealm.where(Alarm.class)
                            .equalTo("enabled", true)
                            .equalTo("snoozed", true).findAll()
                            .sort("snoozedAt").first();
                }
                else
                    return mRealm.where(Alarm.class).equalTo("enabled", true).findAll()
                            .sort("time").first();
            }
            else {
                return mRealm.where(Alarm.class).equalTo("enabled", true).findAll()
                        .sort("time").first();
            }
        }
        else
            return null;
    }

    public void saveAlarm(final String alarmId, final String name, final String time,
                          final boolean isSet, final boolean isStandardTime, final String repeat,
                          final String trackName, final String artist, final String trackId,
                          final String trackImage) {
        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> {
            Alarm editAlarm = realm1.where(Alarm.class).equalTo("id", alarmId).findFirst();
            editAlarm.setAlarmLabel(name);
            editAlarm.setEnabled(isSet);
            editAlarm.setDaysOfWeek(repeat);
            editAlarm.setTrackName(trackName);
            editAlarm.setArtist(artist);
            editAlarm.setTrackId(trackId);
            editAlarm.setTrackImage(trackImage);
            editAlarm.setSnoozed(false);
        });
    }

    public void saveAlarm(final Alarm updatedAlarm) {
        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> {
            Alarm editAlarm = realm1.where(Alarm.class)
                                    .equalTo("id", updatedAlarm.getId())
                                    .findFirst();
            editAlarm.setAlarmLabel(updatedAlarm.getAlarmLabel());
//            editAlarm.setId(updatedAlarm.getId());
            editAlarm.setmWakeTime(updatedAlarm.getmWakeTime());
            editAlarm.setTime(updatedAlarm.getTime());
            editAlarm.setEnabled(updatedAlarm.isEnabled());
            editAlarm.setDaysOfWeek(updatedAlarm.getDaysOfWeek());
            editAlarm.setTrackName(updatedAlarm.getTrackName());
            editAlarm.setArtist(updatedAlarm.getArtistName());
            editAlarm.setTrackId(updatedAlarm.getTrackId());
            editAlarm.setTrackImage(updatedAlarm.getTrackImage());
            editAlarm.setMediaType(updatedAlarm.getMediaType());
            editAlarm.setShuffle(updatedAlarm.isShuffle());
            editAlarm.setVibrate(updatedAlarm.isVibrate());
            editAlarm.setSnoozed(false);
            editAlarm.setSnoozedAt(null);
        });

    }

    public void deleteAlarm(final String alarmId) {
        final Realm realm = Realm.getDefaultInstance();
//        final RealmResults<Alarm> results = realm.where(Alarm.class).equalTo("id", alarmId).findAll();
        realm.executeTransactionAsync(realm1 ->
            realm1.where(Alarm.class).equalTo("id", alarmId).findAll().deleteAllFromRealm());
    }

    public void deleteAlarm(final Alarm alarm) {
        final Realm realm = Realm.getDefaultInstance();
//        final RealmResults<Alarm> results = realm.where(Alarm.class).equalTo("id", alarm.getId()).findAll();
        realm.executeTransactionAsync(realm1 ->
            realm1.where(Alarm.class).equalTo("id", alarm.getId()).findAll().deleteAllFromRealm());
    }

    public void addAlarm(final String name, final String time,
                         final boolean isSet, final boolean isStandardTime,
                         final String repeat, final String trackName, final String artist,
                         final String trackId, final String trackImage) {
        mRealm.executeTransaction(realm -> {
            Alarm alarm = realm.createObject(Alarm.class, UUID.randomUUID().toString());
//            alarm.setId(UUID.randomUUID().toString());
            alarm.setAlarmLabel(name);
            alarm.setEnabled(isSet);
            alarm.setDaysOfWeek(repeat);
            alarm.setTrackName(trackName);
            alarm.setArtist(artist);
            alarm.setTrackId(trackId);
            alarm.setTrackImage(trackImage);
            alarm.setSnoozed(false);
        });
    }

    public void addAlarm(final Alarm newalarm) {
        mRealm.executeTransaction(new Realm.Transaction() {
            Alarm alarm;
            @Override
            public void execute(final Realm realm) {
                alarm = realm.createObject(Alarm.class,UUID.randomUUID().toString());
//                alarm.setId(UUID.randomUUID().toString());
                alarm.setAlarmLabel(newalarm.getAlarmLabel());
                alarm.setmWakeTime(newalarm.getmWakeTime());
                alarm.setTime(newalarm.getTime());
                alarm.setEnabled(newalarm.isEnabled());
                alarm.setDaysOfWeek(newalarm.getDaysOfWeek());
                alarm.setTrackName(newalarm.getTrackName());
                alarm.setArtist(newalarm.getArtistName());
                alarm.setTrackId(newalarm.getTrackId());
                alarm.setTrackImage(newalarm.getTrackImage());
                alarm.setMediaType(newalarm.getMediaType());
                alarm.setShuffle(newalarm.isShuffle());
                alarm.setVibrate(newalarm.isVibrate());
                alarm.setSnoozed(false);
                alarm.setSnoozedAt(null);
            }
        });
    }

    public static void snoozeAlarmById(final String alarmId, final Date date) {
        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> {
            Alarm alarm = realm1.where(Alarm.class).equalTo("id", alarmId).findFirst();
            Log.d("RealmService", "setting snooze");
            alarm.setSnoozed(true);
            alarm.setSnoozedAt(date);
        });
    }

    public static void disableAlarmById(final String alarmId) {
        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> {
            Alarm alarm = realm1.where(Alarm.class).equalTo("id", alarmId).findFirst();

            if(alarm.getDecDaysOfWeek() == 0){
                alarm.setEnabled(false);
            }
            alarm.setSnoozed(false);
            alarm.setSnoozedAt(null);
        });
        updateAlarms();
    }

    public static void disableAlarm(final String alarmId) {
        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> {
            Alarm alarm = realm1.where(Alarm.class).equalTo("id", alarmId).findFirst();

            alarm.setEnabled(false);
            alarm.setSnoozed(false);
            alarm.setSnoozedAt(null);
        });
        updateAlarms();
    }

    public List<Alarm> getMissedAlarms(){
        Date now = Calendar.getInstance().getTime();
        List<Alarm> missed = new ArrayList<>();
        RealmResults<Alarm> alarmList = mRealm.where(Alarm.class).equalTo("enabled", true).equalTo("snoozed", false).findAll();
        for (Alarm a : alarmList) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(a.getTime());
            cal.add(Calendar.MILLISECOND, 5000);
            if (cal.getTime().before(now)){
                missed.add(a);
            }
        }
        return missed;
    }

    public List<Alarm> getMissedSnoozedAlarms(){
        Date now = Calendar.getInstance().getTime();
        List<Alarm> missed = new ArrayList<>();
        RealmResults<Alarm> alarmList = mRealm.where(Alarm.class).equalTo("enabled", true).equalTo("snoozed", true).findAll();
        for (Alarm a : alarmList) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(a.getSnoozedAt());
            cal.add(Calendar.MILLISECOND, 5000);
            if (cal.getTime().before(now)){
                missed.add(a);
            }
        }
        return missed;
    }

    public void disableMissedAlarms(){
        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> {
            Calendar cal = Calendar.getInstance();
            Date now = cal.getTime();
            RealmResults<Alarm> alarmList = realm1.where(Alarm.class).equalTo("enabled", true).equalTo("snoozed", false)
                    .findAll();
            for (final Alarm a:alarmList) {
                cal.setTime(a.getTime());
                cal.add(Calendar.MILLISECOND, 5000);
                if (cal.getTime().before(now)){
                    if(a.getDecDaysOfWeek() == 0){
                        a.setEnabled(false);
                    }
                    a.setSnoozed(false);
                    a.setSnoozedAt(null);
                }
            }
        });
        updateAlarms();
    }

    public void disableMissedSnoozed(){
        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> {
            Calendar cal = Calendar.getInstance();
            Date now = cal.getTime();
            RealmResults<Alarm> alarmList = realm1.where(Alarm.class).equalTo("enabled", true).equalTo("snoozed", true)
                    .findAll();
            for (final Alarm a:alarmList) {
                cal.setTime(a.getSnoozedAt());
                cal.add(Calendar.MILLISECOND, 5000);
                if (cal.getTime().before(now)){
                    if(a.getDecDaysOfWeek() == 0){
                        a.setEnabled(false);
                    }
                    a.setSnoozed(false);
                    a.setSnoozedAt(null);
                }
            }
        });
        updateAlarms();
    }

    public String getMessage(){
        return "From realmService!!";
    }

    public void closeRealm() {
        mRealm.close();
    }

    public interface OnTransactionCallback {
        void onRealmSuccess();
        void onRealmError(final Exception e);
    }
}
