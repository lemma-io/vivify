package com.rva.mrb.vivify.View.Detail;

import android.content.Context;
import android.util.Log;

import com.rva.mrb.vivify.Model.Data.Alarm;
import com.rva.mrb.vivify.Model.Service.AlarmScheduler;
import com.rva.mrb.vivify.Model.Service.RealmService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DetailPresenterImpl implements DetailPresenter, RealmService.OnTransactionCallback {

    public static final String TAG = DetailPresenterImpl.class.getSimpleName();
    private final String TIME_FORMAT = "hh:mm a";
    private final RealmService mRealmService;
    private DetailView mDetailView = new DetailView.EmptyDetailView();


    public DetailPresenterImpl(RealmService realmService){ mRealmService = realmService; }

    public Alarm getAlarm(String index) {
        return mRealmService.getAlarm(index);
    }

    @Override
    public void onDeleteAlarm(String alarmid) {
        mRealmService.deleteAlarm(alarmid);
    }

    @Override
    public void onDeleteAlarm(Context context, Alarm alarm) {
        AlarmScheduler.disableAlarm(context, alarm.getId());
        if(alarm.isSnoozed()){
            AlarmScheduler.cancelSnoozedAlarm(context);
        }
        mRealmService.deleteAlarm(alarm);
    }

    @Override
    public void onSaveAlarm(Context context,String alarmid, String name, String time, boolean isSet, boolean isStandardTime, String repeat, String trackName, String artist, String trackId, String trackImage) {
        mRealmService.saveAlarm(alarmid, name, time, isSet, isStandardTime, repeat, trackName, artist, trackId, trackImage);
    }

    @Override
    public void onSaveAlarm(Alarm alarm, Context applicationContext) {
        Date d = getDate(alarm);
        alarm.setTime(d);
        Log.d("DetailPresenter", "date: " + alarm.getTime());
        Log.d("DetailPresenter", "artist: " + alarm.getArtistName());
        mRealmService.saveAlarm(alarm);
        if(alarm.isSnoozed()){
            AlarmScheduler.cancelSnoozedAlarm(applicationContext);
        }
        AlarmScheduler.setNextAlarm(applicationContext);
    }

    @Override
    public void onRealmSuccess() {

    }

    @Override
    public void onRealmError(Exception e) {

    }

    @Override
    public void onAddClick(Context context, String name, String time, boolean isSet,
                           boolean isStandardTime, String repeat, String trackName, String artist, String trackId, String trackImage) {
        mRealmService.addAlarm(name, time, isSet, isStandardTime, repeat, trackName, artist, trackId, trackImage);
        if (isSet) {
            String newestAlarmId;
            try {
                newestAlarmId = mRealmService.getNewestAlarmId();
                Log.d("New", "Alarm id is: " + newestAlarmId);
            } catch (Exception e) {
                Log.e(TAG, "Alarm not found, trying againg. " + e.getMessage());
                newestAlarmId = mRealmService.getNewestAlarmId();
            }
            if (newestAlarmId != null) {
                Log.d("realm", "Alarm id: " + newestAlarmId); // getAlarm.last()
//                AlarmScheduler.enableAlarmById(context, newestAlarmId);
            }
        }

    }

    @Override
    public void onAddClick(Alarm alarm, Context applicationContext) {
        Date d = getDate(alarm);
        alarm.setTime(d);
        mRealmService.addAlarm(alarm);
        mRealmService.updateAlarms();
        AlarmScheduler.setNextAlarm(applicationContext);
//        if (alarm.isEnabled()) {
//            String newestAlarmId;
//            try {
//                newestAlarmId = mRealmService.getNewestAlarmId();
//                Log.d("New", "Alarm id is: " + newestAlarmId);
//            } catch (Exception e) {
//                Log.e(TAG, "Alarm not found, trying againg. " + e.getMessage());
//                newestAlarmId = mRealmService.getNewestAlarmId();
//            }
//            if (newestAlarmId != null) {
//                Log.d("realm", "Alarm id: " + newestAlarmId); // getAlarm.last()
////                AlarmScheduler.enableAlarmById(context, newestAlarmId);
//            }
//        }
    }

    public String getCurrentTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TIME_FORMAT, Locale.US);
        String time = simpleDateFormat.format(cal.getTime());
        Log.d(TAG, "Current time: " + cal.getTime());
        return (time.indexOf("0")==0) ? time.substring(1): time;
    }
    public String getTime(int hour, int minute) {
        Calendar cal = Calendar.getInstance();
//        Log.d("Calendar", "Current time " + cal.getTime());
        cal.set(Calendar.HOUR_OF_DAY, hour);
//        Log.d("Calendar", "Hour set " + cal.getTime());
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
//        Log.d("Calendar", "Minute set " + cal.getTime());
//        Log.d("Alarm", "Literal Time " + cal.getTime());
        Calendar currentTime = Calendar.getInstance();
        if (cal.before(currentTime))
            cal.add(Calendar.DAY_OF_YEAR,1);

//        String am_pm = (cal.get(Calendar.AM_PM)==Calendar.AM) ? "AM" : "PM";
//        String hrString = String.valueOf(hour);
//        hrString = (hour > 12) ? String.valueOf(hour-12) : hrString;
//        String minString = String.valueOf(minute);
//        minString = (minute < 10) ? "0" + minString : minString;
//        return hrString + ":" + minString + " " + am_pm;

        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat(TIME_FORMAT, Locale.US);
        String time = simpleDateFormat.format(cal.getTime());
        Log.d(TAG, "Wake Time: " + cal.getTime());
        return (time.indexOf("0")==0) ? time.substring(1) : time;
    }

    public Date getDate(Alarm alarm) {
        Date date = alarm.getTime();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return getDate(alarm, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
    }

    public Date getDate(Alarm alarm, int hour, int minute) {
        Calendar cal = Calendar.getInstance();

        //        Log.d("Calendar", "Current time " + cal.getTime());
        cal.set(Calendar.HOUR_OF_DAY, hour);
//        Log.d("Calendar", "Hour set " + cal.getTime());
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
//        Log.d("Calendar", "Minute set " + cal.getTime());
//        Log.d("Alarm", "Literal Time " + cal.getTime());
        alarm.setTime(cal.getTime());
//        Calendar currentTime = Calendar.getInstance();
//        int todaysDay = alarm.mapToAlarmDays(cal.get(Calendar.DAY_OF_WEEK));
//        if (((alarm.getDecDaysOfWeek() & todaysDay) == todaysDay) || (alarm.getDecDaysOfWeek() == 0)) {
//            if (!cal.before(currentTime)) {
//                return cal.getTime();
//            }
//        }
        Log.d("DetailPresenter", "getNextDayEnabled: " + alarm.getNextDayEnabled());
        cal.add(Calendar.DAY_OF_YEAR, alarm.getNextDayEnabled());


//        String am_pm = (cal.get(Calendar.AM_PM)==Calendar.AM) ? "AM" : "PM";
//        String hrString = String.valueOf(hour);
//        hrString = (hour > 12) ? String.valueOf(hour-12) : hrString;
//        String minString = String.valueOf(minute);
//        minString = (minute < 10) ? "0" + minString : minString;
//        return hrString + ":" + minString + " " + am_pm;

//        SimpleDateFormat simpleDateFormat =
//                new SimpleDateFormat(TIME_FORMAT, Locale.US);
//        String time = simpleDateFormat.format(cal.getTime());
//        Log.d(TAG, "Wake Time: " + cal.getTime());
//        return (time.indexOf("0")==0) ? time.substring(1) : time;
        return cal.getTime();
    }

    @Override
    public int getCurrentHour() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    @Override
    public int getHour(Alarm alarm) {
        return (alarm.getTime() != null) ? alarm.getHour() : getCurrentHour();
    }


    @Override
    public int getCurrentMinute() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MINUTE);
    }

    @Override
    public int getMinute(Alarm alarm) {
        return (alarm.getTime() != null) ? alarm.getMinute() : getCurrentMinute();
    }

    @Override
    public String getNewestAlarm() {
        return mRealmService.getNewestAlarmId();
    }

    @Override
    public Date getTimeOfDay(Date date) {
        Calendar timeOfDay = Calendar.getInstance();
        timeOfDay.setTime(date);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        String s = sdf.format(timeOfDay.getTime());
        try {
            Date d = sdf.parse(s);
            return d;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void setView(DetailView view) {
        mDetailView = view;
    }

    @Override
    public void clearView() {
        mDetailView = new DetailView.EmptyDetailView();
    }

    @Override
    public void closeRealm() {
        mRealmService.closeRealm();
    }
}