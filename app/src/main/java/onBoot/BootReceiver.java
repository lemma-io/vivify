package onBoot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.rva.mrb.vivify.Model.Data.Alarm;
import com.rva.mrb.vivify.Model.Service.AlarmScheduler;
import com.rva.mrb.vivify.Model.Service.NotificationService;
import com.rva.mrb.vivify.Model.Service.RealmService;

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

            notificationService.setMissedAlarmNotification(a.getTime() + "", a.isSnoozed());
        }
        for (Alarm alarm : missedSnoozed){
            notificationService.setMissedAlarmNotification(alarm.getTime() + "", alarm.isSnoozed());
        }
        realmService.disableMissedAlarms();
        realmService.disableMissedSnoozed();
        AlarmScheduler.cancelSnoozedAlarm(context);
    }

    public void upcommingAlarmNotifications(RealmService realmService, NotificationService notificationService){
        Alarm nextAlarm = realmService.getNextAlarm();

        if (nextAlarm!=null){
            notificationService.setNotification(nextAlarm.getTime() +"", nextAlarm.isSnoozed());
        }
    }
}