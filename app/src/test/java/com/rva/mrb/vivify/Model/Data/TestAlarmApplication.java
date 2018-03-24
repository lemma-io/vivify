package com.rva.mrb.vivify.Model.Data;

import com.rva.mrb.vivify.AlarmApplication;
import com.rva.mrb.vivify.ApplicationComponent;
import com.rva.mrb.vivify.ApplicationModule;
import com.rva.mrb.vivify.DaggerApplicationComponent;

public class TestAlarmApplication extends AlarmApplication {

    protected ApplicationComponent initDagger() {
        return DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }
}
