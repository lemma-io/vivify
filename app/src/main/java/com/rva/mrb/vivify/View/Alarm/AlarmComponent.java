package com.rva.mrb.vivify.View.Alarm;

import com.rva.mrb.vivify.ApplicationComponent;
import com.rva.mrb.vivify.ApplicationModule;

import dagger.Component;

@Component(modules = {ApplicationModule.class, AlarmModule.class}, dependencies = ApplicationComponent.class)
public interface AlarmComponent {
    void inject(AlarmActivity alarmActivity);
}