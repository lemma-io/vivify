package com.rva.mrb.vivify;

import android.app.Application;

import com.facebook.stetho.Stetho;
//import com.rva.mrb.vivify.View.Search.DaggerSearchComponent;
import com.rva.mrb.vivify.View.Search.SearchComponent;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;
import com.bugfender.sdk.Bugfender;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class AlarmApplication extends Application {
    private ApplicationComponent mComponent;
    private SearchComponent mSearchComponent;
    private static final String BUGFENDER_APP_KEY = BuildConfig.BUGFENDER_APP_KEY;

    @Override
    public void onCreate() {
        super.onCreate();
        mComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();

        initRealmConfiguration();
        initSpotifyClient();
        initStethoBrowser();
        Bugfender.init(this, BUGFENDER_APP_KEY, BuildConfig.DEBUG);
        Bugfender.enableLogcatLogging();
        Bugfender.enableUIEventLogging(this);
    }

    private void initSpotifyClient() {
//        mSearchComponent = DaggerSearchComponent.builder()
//                .applicationModule(new ApplicationModule(this))
//                .searchModule(new )
    }

    private void initRealmConfiguration() {
        Realm.init(getApplicationContext());
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
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
