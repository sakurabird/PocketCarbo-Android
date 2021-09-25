package com.sakurafish.pockettoushituryou

import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.sakurafish.pockettoushituryou.di.ApplicationModule
import com.sakurafish.pockettoushituryou.di.DaggerApplicationComponent
import com.sakurafish.pockettoushituryou.di.applyAutoInjector
import com.sakurafish.pockettoushituryou.di.module.DatabaseModule
import dagger.android.support.DaggerApplication
import timber.log.Timber

class MainApplication : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()

        applyAutoInjector()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        initAdMob()
    }

    private fun initAdMob() {
        MobileAds.initialize(this, getString(R.string.admob_app_id))
        val testDeviceIds = resources.getStringArray(R.array.admob_test_device).toList()
        val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
        MobileAds.setRequestConfiguration(configuration)
    }

    override fun applicationInjector() = DaggerApplicationComponent
        .builder()
        .application(this)
        .context(this.applicationContext)
        .applicationModule(ApplicationModule(this))
        .databaseModule(DatabaseModule(this))
        .build()
}
