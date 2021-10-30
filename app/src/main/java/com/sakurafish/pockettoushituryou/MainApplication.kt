package com.sakurafish.pockettoushituryou

import android.app.Application
import androidx.databinding.ktx.BuildConfig
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.util.*

@HiltAndroidApp
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

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
}
