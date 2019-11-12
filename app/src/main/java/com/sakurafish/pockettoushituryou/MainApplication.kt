package com.sakurafish.pockettoushituryou

import com.crashlytics.android.Crashlytics
import com.google.android.gms.ads.MobileAds
import com.sakurafish.pockettoushituryou.di.ApplicationModule
import com.sakurafish.pockettoushituryou.di.DaggerApplicationComponent
import com.sakurafish.pockettoushituryou.di.applyAutoInjector
import dagger.android.support.DaggerApplication
import io.fabric.sdk.android.Fabric
import timber.log.Timber

class MainApplication : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()
        applyAutoInjector()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        Fabric.with(this, Crashlytics())

        // adMob
        MobileAds.initialize(this, getString(R.string.admob_app_id))
    }

    override fun applicationInjector() = DaggerApplicationComponent
            .builder()
            .application(this)
            .context(this.applicationContext)
            .applicationModule(ApplicationModule(this))
            .build()
}
