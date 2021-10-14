package com.sakurafish.pockettoushituryou

import androidx.databinding.ktx.BuildConfig
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.sakurafish.pockettoushituryou.di.ApplicationModule
import com.sakurafish.pockettoushituryou.di.DaggerApplicationComponent
import com.sakurafish.pockettoushituryou.di.applyAutoInjector
import com.sakurafish.pockettoushituryou.di.module.DatabaseModule
import dagger.android.support.DaggerApplication
import timber.log.Timber
import java.util.*

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
        MobileAds.initialize(this) {}
        val ids = ArrayList(
            resources.getStringArray(R.array.admob_test_device)
                .toMutableList()
        )
        val configuration = RequestConfiguration.Builder().setTestDeviceIds(ids).build()
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
