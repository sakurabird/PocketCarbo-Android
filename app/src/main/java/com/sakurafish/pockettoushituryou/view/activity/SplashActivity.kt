package com.sakurafish.pockettoushituryou.view.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.sakurafish.pockettoushituryou.data.local.FoodsData
import com.sakurafish.pockettoushituryou.repository.FoodsRepository
import com.sakurafish.pockettoushituryou.shared.Pref
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class SplashActivity : AppCompatActivity(), HasAndroidInjector {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>
    @Inject
    lateinit var pref: Pref
    @Inject
    lateinit var compositeDisposable: CompositeDisposable
    @Inject
    lateinit var foodsRepository: FoodsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, Bundle())

        DataBindingUtil.setContentView<ViewDataBinding>(this, com.sakurafish.pockettoushituryou.R.layout.activity_splash)

        findViewById<View>(android.R.id.content).systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE

        setLaunchCount()

        findAllDataFromAssets()
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.dispose()
    }

    private fun setLaunchCount() {
        var launchCount = pref.getPrefInt(getString(com.sakurafish.pockettoushituryou.R.string.PREF_LAUNCH_COUNT))
        pref.setPref(getString(com.sakurafish.pockettoushituryou.R.string.PREF_LAUNCH_COUNT), ++launchCount)
    }

    private fun findAllDataFromAssets() {
        val dataVersion = resources.getInteger(com.sakurafish.pockettoushituryou.R.integer.data_version)
        if (dataVersion == pref.getPrefInt(getString(com.sakurafish.pockettoushituryou.R.string.PREF_DATA_VERSION))) {
            Timber.tag(TAG).d("DB is maintained latest version:%d", dataVersion)
            val disposable = Observable.interval(MINIMUM_LOADING_TIME, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { startNextActivity() }
            compositeDisposable.add(disposable)
            return
        }

        val disposable = Single.zip(foodsRepository.findAllFromAssets(),
                Single.timer(MINIMUM_LOADING_TIME, TimeUnit.MILLISECONDS),
                BiFunction { foodsFata: FoodsData, _: Any ->
                    Observable.empty<Any>()
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { this.startNextActivity() }
                .subscribe({
                    pref.setPref(getString(com.sakurafish.pockettoushituryou.R.string.PREF_DATA_VERSION), dataVersion)
                    Timber.tag(TAG).d("Succeeded in loading foods from Assets.")
                },
                        { throwable -> Timber.tag(TAG).e(throwable, "Failed to load foods from Assets.") })

        compositeDisposable.add(disposable)
    }

    private fun startNextActivity() {
        if (isFinishing) return
        startActivity(MainActivity.createIntent(this@SplashActivity))
        this@SplashActivity.finish()
    }

    override fun androidInjector(): AndroidInjector<Any> = androidInjector

    companion object {

        private val TAG = SplashActivity::class.java.simpleName

        private const val MINIMUM_LOADING_TIME = 1000L
    }
}
