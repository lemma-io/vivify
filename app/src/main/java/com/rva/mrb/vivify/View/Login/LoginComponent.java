package com.rva.mrb.vivify.View.Login;

import com.rva.mrb.vivify.ApplicationComponent;
import com.rva.mrb.vivify.ApplicationModule;
import com.rva.mrb.vivify.View.Login.LoginModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Bao on 9/28/16.
 */

@Singleton
@Component(modules = {ApplicationModule.class, LoginModule.class}, dependencies ={ApplicationComponent.class})
public interface LoginComponent {
    void inject(LoginActivity loginActivity);
}
