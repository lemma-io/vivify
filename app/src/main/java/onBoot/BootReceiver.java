package onBoot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.rva.mrb.vivify.Model.Data.Alarm;
import com.rva.mrb.vivify.Model.Service.AlarmScheduler;
import com.rva.mrb.vivify.Model.Service.NotificationService;
import com.rva.mrb.vivify.Model.Service.RealmService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;

/**
 * Created by Bao on 8/24/17.
 */

public class BootReceiver extends BroadcastReceiver
{

    public void onReceive(Context context, Intent intent)
    {
        RealmService realmService = new RealmService(Realm.getDefaultInstance());
        NotificationService notificationService = new NotificationService(context);
        handleMissedAlarms(context, realmService, notificationService);
        upcommingAlarmNotifications(realmService, notificationService);
    }

    public void handleMissedAlarms(Context context, RealmService realmService, NotificationService notificationService){
        List<Alarm> missedAlarms = realmService.getMissedAlarms();
        List<Alarm> missedSnoozed = realmService.getMissedSnoozedAlarms();
        for (Alarm a : missedAlarms){

            notificationService.setMissedAlarmNotification(prettyDateFormat(a.getTime()) + "", a.isSnoozed());
        }
        for (Alarm alarm : missedSnoozed){
            notificationService.setMissedAlarmNotification(prettyDateFormat(alarm.getTime()) + "", alarm.isSnoozed());
        }
        realmService.disableMissedAlarms();
        realmService.disableMissedSnoozed();
        AlarmScheduler.cancelSnoozedAlarm(context);
    }

    public void upcommingAlarmNotifications(RealmService realmService, NotificationService notificationService){
        Alarm nextAlarm = realmService.getNextAlarm();

        if (nextAlarm!=null){
            notificationService.setNotification(prettyDateFormat(nextAlarm.getTime()) +"", nextAlarm.isSnoozed());
        }
    }

    public String prettyDateFormat(Date time) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        String daySuffix = getDaySuffix(cal.get(Calendar.DAY_OF_MONTH));
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d'" + daySuffix + "' hh:m a");
        return sdf.format(time.getTime());
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
}