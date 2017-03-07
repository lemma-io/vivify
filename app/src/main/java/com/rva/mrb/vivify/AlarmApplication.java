package com.rva.mrb.vivify;

import android.app.Application;

import com.facebook.stetho.Stetho;
//import com.rva.mrb.vivify.View.Search.DaggerSearchComponent;
import com.rva.mrb.vivify.View.Search.SearchComponent;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class AlarmApplication extends Application {
    private ApplicationComponent mComponent;
    private SearchComponent mSearchComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();

        initRealmConfiguration();
        initSpotifyClient();
        initStethoBrowser();
    }

    private void initSpotifyClient() {
//        mSearchComponent = DaggerSearchComponent.builder()
//                .applicationModule(new ApplicationModule(this))
//                .searchModule(new )
    }

    private void initRealmConfiguration() {
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }

    public ApplicationComponent getComponent() {
        return DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public void initStethoBrowser() {
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());
    }
}
