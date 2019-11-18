package com.sakurafish.pockettoushituryou.view.helper

import android.content.Context
import android.widget.Toast
import com.sakurafish.pockettoushituryou.R
import com.sakurafish.pockettoushituryou.shared.Pref
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView
import javax.inject.Inject
import javax.inject.Singleton

/**
 * deano2390/MaterialShowcaseView を使用してチュートリアルを表示するに当たってのヘルパーメソッド
 */
@Singleton
class ShowcaseHelper @Inject constructor(
        private val context: Context,
        private val pref: Pref
) {

    // true: 表示済みをあらわす
    val isShowcaseMainActivityFinished: Boolean
        get() = pref.getPrefBool(PREF_KEY_SHOWCASE_MAINACTIVITY_FINISHED, false)

    // true: 表示済みをあらわす
    val isShowcaseFoodListFragmentFinished: Boolean
        get() = pref.getPrefBool(PREF_KEY_SHOWCASE_FOODLISTFRAGMENT_FINISHED, false)

    fun resetShowCase() {
        // チュートリアルを表示できるように設定をリセットする
        pref.setPref(PREF_KEY_SHOWCASE_MAINACTIVITY_FINISHED, false)
        MaterialShowcaseView.resetSingleUse(context, SHOWCASE_ID_MAINACTIVITY)

        pref.setPref(PREF_KEY_SHOWCASE_FOODLISTFRAGMENT_FINISHED, false)
        MaterialShowcaseView.resetSingleUse(context, SHOWCASE_ID_FOODLISTFRAGMENT)

        Toast.makeText(context, context.getString(R.string.tutorial_reset_toast_text), Toast.LENGTH_SHORT).show()
    }

    fun setPrefShowcaseMainactivityFinished(finished: Boolean) {
        pref.setPref(PREF_KEY_SHOWCASE_MAINACTIVITY_FINISHED, finished)
    }

    fun setPrefShowcaseFoodListFragmentFinished(finished: Boolean) {
        pref.setPref(PREF_KEY_SHOWCASE_FOODLISTFRAGMENT_FINISHED, finished)
    }

    companion object {

        private val TAG = ShowcaseHelper::class.java.simpleName

        const val SHOWCASE_DELAY: Long = 500

        const val SHOWCASE_ID_MAINACTIVITY = "SHOWCASE_ID_MAINACTIVITY"
        const val SHOWCASE_ID_FOODLISTFRAGMENT = "SHOWCASE_ID_FOODLISTFRAGMENT"

        const val PREF_KEY_SHOWCASE_MAINACTIVITY_FINISHED = "PREF_KEY_MAINACTIVITY_FINISHED"
        const val PREF_KEY_SHOWCASE_FOODLISTFRAGMENT_FINISHED = "PREF_KEY_FOODLISTFRAGMENT_FINISHED"

        const val EVENT_SHOWCASE_MAINACTIVITY_FINISHED = "EVENT_SHOWCASE_MAINACTIVITY_FINISHED"
    }
}
