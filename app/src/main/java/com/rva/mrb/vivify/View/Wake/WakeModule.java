package com.rva.mrb.vivify.View.Wake;

import com.rva.mrb.vivify.Model.Service.RealmService;

import dagger.Module;
import dagger.Provides;

@Module
public class WakeModule {

    private final WakeActivity activity;

    public WakeModule(WakeActivity activity) {
        this.activity = activity;
    }

    @Provides
    WakePresenter providesAlertPresenterImpl(RealmService realmService) {
        return new WakePresenterImpl(realmService);
    }
}
