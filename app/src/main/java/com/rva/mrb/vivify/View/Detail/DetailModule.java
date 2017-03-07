package com.rva.mrb.vivify.View.Detail;

import com.rva.mrb.vivify.Model.Service.RealmService;

import dagger.Module;
import dagger.Provides;

@Module
public class DetailModule {

    @Provides
    DetailPresenter providesNewAlarmPresenterImpl(RealmService realmService){
        return new DetailPresenterImpl(realmService);
    }
}
