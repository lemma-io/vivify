package com.rva.mrb.vivify.View.Alarm;

import com.rva.mrb.vivify.Model.Service.RealmService;

import dagger.Module;
import dagger.Provides;

@Module
public class AlarmModule {

    private final AlarmActivity activity;
    public AlarmModule(AlarmActivity activity){ this.activity = activity; };

    @Provides
    AlarmsPresenter providesAlarmPresenterImpl(RealmService realmService){
        return new AlarmPresenterImpl(realmService);
    }

}
