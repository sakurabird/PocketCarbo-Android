package com.sakurafish.pockettoushituryou.di;

import com.sakurafish.pockettoushituryou.MainApplication;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {

    void inject(MainApplication application);

    ActivityComponent plus(ActivityModule module);
}
