package com.rva.mrb.vivify.View.Detail;

import com.rva.mrb.vivify.ApplicationComponent;
import com.rva.mrb.vivify.ApplicationModule;
import com.rva.mrb.vivify.View.Search.SearchModule;

import dagger.Component;

@Component(modules = {ApplicationModule.class, DetailModule.class}, dependencies = {ApplicationComponent.class})
public interface DetailComponent {

    void inject(DetailActivity newAlarmActivity);
}
