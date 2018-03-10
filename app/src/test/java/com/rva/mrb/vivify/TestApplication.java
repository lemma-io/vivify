package com.rva.mrb.vivify;

public class TestApplication extends AlarmApplication {

    protected ApplicationComponent initDagger() {
        return DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    protected void setupRealm() {}
}
