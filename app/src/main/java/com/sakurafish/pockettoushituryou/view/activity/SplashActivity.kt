package com.sakurafish.pockettoushituryou.view.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject


class SplashActivity : AppCompatActivity(), HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DataBindingUtil.setContentView<ViewDataBinding>(this, com.sakurafish.pockettoushituryou.R.layout.activity_splash)

        findViewById<View>(android.R.id.content).systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE

        startNextActivity()
    }

    private fun startNextActivity() {
        if (isFinishing) return
        startActivity(MainActivity.createIntent(this@SplashActivity))
        this@SplashActivity.finish()
    }

    override fun androidInjector(): AndroidInjector<Any> = androidInjector
}
