package com.sakurafish.pockettoushituryou.ui.ads

import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.sakurafish.pockettoushituryou.R
import com.sakurafish.pockettoushituryou.shared.Pref
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdsHelper @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val pref: Pref
) {

    // Need 5 minutes Interval
    val isIntervalOK: Boolean
        get() {
            val now = System.currentTimeMillis()
            val lastClickTime = lastClickTimeMillis
            if (lastClickTime == 0L) return true
            val passedTime = now - lastClickTime
            return passedTime >= CLICK_DELAY_MILLIS
        }

    val adRequest: AdRequest = AdRequest.Builder().build()

    var lastClickTimeMillis: Long
        get() = pref.getPrefLong(appContext.getString(R.string.PREF_LAST_AD_CLICK_TIME), 0)
        set(date) = pref.setPrefLong(appContext.getString(R.string.PREF_LAST_AD_CLICK_TIME), date)

    fun getIntervalTimeMillis(now: Long, lastClickTimeMillis: Long): Long {
        var intervalTimeMillis = CLICK_DELAY_MILLIS
        if (lastClickTimeMillis != 0L) {
            val passedTimeMillis = now - lastClickTimeMillis
            intervalTimeMillis = CLICK_DELAY_MILLIS - passedTimeMillis
            if (intervalTimeMillis > CLICK_DELAY_MILLIS) {
                intervalTimeMillis = CLICK_DELAY_MILLIS
            }
        }
        return intervalTimeMillis
    }

    companion object {
        // Intent action filter string
        const val ACTION_BANNER_CLICK = "ACTION_BANNER_CLICK"

        // Intent extras key string
        const val INTENT_EXTRAS_KEY_CLASS = "INTENT_EXTRAS_KEY_CLASS"

        // click interval time millisecond
        const val CLICK_DELAY_MILLIS = (1000 * 60 * 5).toLong() // 5 min
    }
}
