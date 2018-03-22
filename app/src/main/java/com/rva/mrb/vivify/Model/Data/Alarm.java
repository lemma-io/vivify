package com.rva.mrb.vivify.Model.Data;

import android.util.Log;

import org.parceler.Parcel;

import java.util.Calendar;
import java.util.Date;

import io.realm.AlarmRealmProxy;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@Parcel (implementations = {AlarmRealmProxy.class},
    value = Parcel.Serialization.BEAN,
    analyze = {Alarm.class})
public class Alarm extends RealmObject {

    public static final String TAG = Alarm.class.getSimpleName();

    public static final int FLAG_NEXT_ALARM = 24;
    public static final int FLAG_SNOOZED_ALARM = 42;

    // used to set when to repeat an alarm
    // flipping bits to set days
    public static final int SUNDAY = 1;
    public static final int MONDAY = 2;
    public static final int TUESDAY = 4;
    public static final int WEDNESDAY = 8;
    public static final int THURSDAY = 16;
    public static final int FRIDAY = 32;
    public static final int SATURDAY = 64;

    @PrimaryKey
    private String id;
    private String alarmLabel;
    private boolean enabled;
    private String mWakeTime;
    private String daysOfWeek;
    private Date time;
    private String trackName;
    private String artist;
    private String trackId;
    private String trackImage;
    private int mediaType;
    private boolean snoozed;
    private boolean shuffle;
    private Date snoozedAt;
    private boolean vibrate;

    public Alarm() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlarmLabel() {
        return alarmLabel;
    }

    public void setAlarmLabel(String alarmLabel) {
        this.alarmLabel = alarmLabel;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getmWakeTime() {
        return mWakeTime;
    }

    public void setmWakeTime(String mWakeTime) {
        this.mWakeTime = mWakeTime;
    }

    public String getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(String daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Date getTime() {
        return time;
    }

    public Calendar getCal() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        return cal;
    }

    public Date updateTime() {
        if (getCal().before(Calendar.getInstance())) {
            // holds new date
            Calendar update = Calendar.getInstance();
            update.set(Calendar.HOUR_OF_DAY, getCal().get(Calendar.HOUR_OF_DAY));
            update.set(Calendar.MINUTE, getCal().get(Calendar.MINUTE));
            //update.set(Calendar.AM_PM, getCal().get(Calendar.AM_PM));
            update.set(Calendar.SECOND, 0);

            // checks to find the next available day
            update.add(Calendar.DAY_OF_YEAR, getNextDayEnabled());
//            Log.d(TAG, "Alarm time: " + update.getTime());
            time = update.getTime();
        }
        return time;
    }

    public Long getTimeInMillis() {
        return getCal().getTimeInMillis();
    }

    public int getNextDayEnabled() {
        // this method only gets called if the alarm is old
        // meaning we can roll the date ahead one day
        // right off the bat and then begin checking
        Calendar next = Calendar.getInstance();

        //next.add(Calendar.DAY_OF_YEAR, 1);

        // convert the Calendar day to a value we can use
        int todaysDay = mapToAlarmDays(next.get(Calendar.DAY_OF_WEEK));
//        Log.d("Day", "Binary day: " + todaysDay);

        // roll the calendar this many days forward
        int daysFromNow = 0;

//        Log.d("alarm.java", "repeat today: " + ((getDecDaysOfWeek() & todaysDay)==1));
//        Log.d("alarm.java", "getDecDaysOfWeek: " + getDecDaysOfWeek());
//        Log.d("alarm.java", "getDecDaysOfWeek & today: " + (getDecDaysOfWeek() & todaysDay));


        // we are using bitwise operations to store multiple values in a
        // single column since Realm does not support an array of primitive
        // data types. We are storing a string of up to 7 binary digits.We
        // can think of this as a row of switches each with an on/off button
        // corresponding to each day.
        if (getDecDaysOfWeek() == 0) {
            Calendar cal = Calendar.getInstance();
            boolean before = time.before(cal.getTime());
//            Log.d("date", "before current date: " + before);
            if (time.before(cal.getTime())) {
                daysFromNow = 1;
            } else {
                daysFromNow = 0;
            }
            return daysFromNow;
        }
        else if ((getDecDaysOfWeek() & todaysDay) == todaysDay) {
            Calendar calendar = Calendar.getInstance();
//            Log.d("alarm.java", "time: " + time);
//            Log.d("alarm.java", "current time: " + calendar.getTime());
//            Log.d("alarm.java", "before: " + !time.before(calendar.getTime()));


            if (!time.before(calendar.getTime())) {
//                Log.d("alarm object", "before: " + !time.before(calendar.getTime()));
                daysFromNow = 0;
                return daysFromNow;
            }

        }
            next.add(Calendar.DAY_OF_YEAR, 1);
            Calendar current = Calendar.getInstance();
            for (int days = 1; days <= 7; days++) {
                todaysDay = mapToAlarmDays(next.get(Calendar.DAY_OF_WEEK));
//                Log.d(TAG, "Next days is " + todaysDay);
                daysFromNow = days;

                // In this case we are checking to see if today is contained
                // in our binary repeat days value. This is done by utilizing the
                // & operation, remember that is will only flip the bit of the
                // digit corresponding to this day. If thats true then it must equal
                // the binary value of today
                if ((getDecDaysOfWeek() & todaysDay) == todaysDay) {
                    //if (!time.before(current.getTime())) {
//                    Log.d(TAG, "Fire when");
                    daysFromNow = days;
                    break;
                    //}

                }
                // If today is not contained within the binary string then
                // roll the date foreword one day
//                next.add(Calendar.DAY_OF_YEAR, 1);
            }


//        Log.d(TAG, "Next alarm occurence is in " + daysFromNow + " days");
        return daysFromNow;
    }

    /**
     * Match any Calendar.Day_Of_Week value to the corresponding int value in
     * Alarm so we can decipher our days enabled binary string
     * @param calendarDay any Calendar.Day_Of_Week (ex. Sunday = 1, Saturday = 7)
     * @return a binary value of that day
     */
    public int mapToAlarmDays(int calendarDay) {
        switch (calendarDay) {
            case 1:
                return SUNDAY;
            case 2:
                return MONDAY;
            case 3:
                return TUESDAY;
            case 4:
                return WEDNESDAY;
            case 5:
                return THURSDAY;
            case 6:
                return FRIDAY;
            case 7:
                return SATURDAY;
            default:
                return 0;
        }
    }

    public int getDecDaysOfWeek() {
        return (daysOfWeek != null) ? Integer.parseInt(daysOfWeek, 2) : 0;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getArtistName() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public String getTrackImage() {
        return trackImage;
    }

    public void setTrackImage(String trackImage) {
        this.trackImage = trackImage;
    }

    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    public void setSnoozed(boolean isSnoozed) { this.snoozed = isSnoozed; }

    public boolean isSnoozed() { return snoozed; }

    public void setShuffle(boolean shuffle) { this.shuffle = shuffle; }

    public boolean isShuffle() { return shuffle; }

    public Date getSnoozedAt() { return snoozedAt; }

    public void setSnoozedAt(Date snoozedAt) { this.snoozedAt = snoozedAt; }

    public boolean isVibrate() { return vibrate; }

    public void setVibrate(boolean vibrate) { this.vibrate = vibrate; }
}
