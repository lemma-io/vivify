package com.rva.mrb.vivify.Model.RealmHelper;

import com.rva.mrb.vivify.ApplicationComponent;
import com.rva.mrb.vivify.ApplicationModule;
import com.rva.mrb.vivify.Model.Service.AlarmScheduler;

import dagger.Component;

@Component(modules = {ApplicationModule.class, RealmHelperModule.class},
    dependencies = ApplicationComponent.class)
public interface RealmHelperComponent {
    void inject(AlarmScheduler alarmScheduler);
//    RealmHelper getRealmHelper();
}
