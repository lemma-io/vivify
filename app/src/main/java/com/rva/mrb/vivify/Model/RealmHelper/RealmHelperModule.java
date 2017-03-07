package com.rva.mrb.vivify.Model.RealmHelper;

import com.rva.mrb.vivify.Model.Service.RealmService;
import com.rva.mrb.vivify.Model.Service.AlarmScheduler;

import dagger.Module;
import dagger.Provides;

@Module
public class RealmHelperModule {

    private final AlarmScheduler scheduler;

    public RealmHelperModule(AlarmScheduler alarmScheduler) {
        this.scheduler = alarmScheduler;
    }

    @Provides
    RealmHelper providesAlarmHelper(RealmService realmService) {
        return new RealmHelper(realmService);
    }
}
