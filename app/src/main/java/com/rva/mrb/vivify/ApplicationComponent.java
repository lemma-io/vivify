package com.rva.mrb.vivify;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Bao on 6/24/16.
 */
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {
    void inject(AlarmApplication app);
    void inject(BaseActivity activity);
}
