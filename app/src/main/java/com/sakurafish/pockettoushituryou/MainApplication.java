package com.sakurafish.pockettoushituryou;

import android.app.Application;
import android.support.annotation.NonNull;

import com.google.android.gms.ads.MobileAds;
import com.sakurafish.pockettoushituryou.di.ApplicationComponent;
import com.sakurafish.pockettoushituryou.di.ApplicationModule;
import com.sakurafish.pockettoushituryou.di.DaggerApplicationComponent;

import timber.log.Timber;

public class MainApplication extends Application {

    private static final String TAG = MainApplication.class.getSimpleName();

    private ApplicationComponent applicationComponent;

    @NonNull
    public ApplicationComponent getComponent() {
        return applicationComponent;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        // adMob
        MobileAds.initialize(this, getString(R.string.admob_app_id));
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }
}
