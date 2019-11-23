package com.sakurafish.pockettoushituryou.view.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.sakurafish.pockettoushituryou.shared.Pref
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject


class SplashActivity : AppCompatActivity(), HasAndroidInjector {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>
    @Inject
    lateinit var pref: Pref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, Bundle())

        DataBindingUtil.setContentView<ViewDataBinding>(this, com.sakurafish.pockettoushituryou.R.layout.activity_splash)

        findViewById<View>(android.R.id.content).systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE

        setLaunchCount()

        startNextActivity()
    }

    private fun setLaunchCount() {
        var launchCount = pref.getPrefInt(getString(com.sakurafish.pockettoushituryou.R.string.PREF_LAUNCH_COUNT))
        pref.setPref(getString(com.sakurafish.pockettoushituryou.R.string.PREF_LAUNCH_COUNT), ++launchCount)
    }

    private fun startNextActivity() {
        if (isFinishing) return
        startActivity(MainActivity.createIntent(this@SplashActivity))
        this@SplashActivity.finish()
    }

    override fun androidInjector(): AndroidInjector<Any> = androidInjector
}
