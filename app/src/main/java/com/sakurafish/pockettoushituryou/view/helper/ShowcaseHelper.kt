package com.sakurafish.pockettoushituryou.view.helper

import android.view.View
import androidx.appcompat.app.AlertDialog
import com.sakurafish.pockettoushituryou.R
import com.sakurafish.pockettoushituryou.databinding.ActivityMainBinding
import com.sakurafish.pockettoushituryou.databinding.FragmentFoodsBinding
import com.sakurafish.pockettoushituryou.shared.events.Events
import com.sakurafish.pockettoushituryou.shared.events.ShowcaseState
import com.sakurafish.pockettoushituryou.shared.Pref
import com.sakurafish.pockettoushituryou.view.activity.MainActivity
import com.sakurafish.pockettoushituryou.view.fragment.FoodsFragment
import kotlinx.coroutines.*
import uk.co.deanwild.materialshowcaseview.IShowcaseListener
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig
import javax.inject.Inject
import javax.inject.Singleton

/**
 * deano2390/MaterialShowcaseView を使用してチュートリアルを表示するに当たってのヘルパーメソッド
 */
@Singleton
class ShowcaseHelper @Inject constructor(
    private val pref: Pref,
    private val events: Events
) {
    // TODO CoroutineScopeはinjectする
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // true: 表示済みをあらわす
    val isShowcaseMainActivityFinished: Boolean
        get() = pref.getPrefBool(PREF_KEY_SHOWCASE_MAINACTIVITY_FINISHED, false)

    // true: 表示済みをあらわす
    val isShowcaseFoodListFragmentFinished: Boolean
        get() = pref.getPrefBool(PREF_KEY_SHOWCASE_FOODLISTFRAGMENT_FINISHED, false)

    fun setPrefShowcaseMainactivityFinished(finished: Boolean) {
        pref.setPref(PREF_KEY_SHOWCASE_MAINACTIVITY_FINISHED, finished)
    }

    fun setPrefShowcaseFoodListFragmentFinished(finished: Boolean) {
        pref.setPref(PREF_KEY_SHOWCASE_FOODLISTFRAGMENT_FINISHED, finished)
    }

    fun showTutorialOnce(activity: MainActivity, binding: ActivityMainBinding) {
        setPrefShowcaseFoodListFragmentFinished(false)
        val config = ShowcaseConfig()
        config.delay = SHOWCASE_DELAY

        val sequence = MaterialShowcaseSequence(activity, SHOWCASE_ID_MAINACTIVITY)
        sequence.setConfig(config)

        // Menu tutorial
        val drawerIcon = binding.toolbar.getChildAt(1)
        sequence.addSequenceItem(
                MaterialShowcaseView.Builder(activity)
                        .setTarget(drawerIcon)
                        .setContentText(activity.getString(R.string.tutorial_nav_text))
                        .setDismissText(activity.getString(android.R.string.ok))
                        .setDismissOnTouch(true)
                        .build()
        )

        // Search tutorial
        val searchIcon = activity.findViewById<View>(R.id.action_search)
        sequence.addSequenceItem(
                MaterialShowcaseView.Builder(activity)
                        .setTarget(searchIcon)
                        .setContentText(activity.getString(R.string.tutorial_search_text))
                        .setDismissText(activity.getString(android.R.string.ok))
                        .setDismissOnTouch(true)
                        .build()
        )

        // Tab tutorial
        sequence.addSequenceItem(
                MaterialShowcaseView.Builder(activity)
                        .setTarget(binding.tabLayout)
                        .setContentText(activity.getString(R.string.tutorial_tab_text))
                        .setDismissText(activity.getString(android.R.string.ok))
                        .withRectangleShape(true)
                        .setListener(object : IShowcaseListener {
                            override fun onShowcaseDisplayed(materialShowcaseView: MaterialShowcaseView) {
                            }

                            override fun onShowcaseDismissed(materialShowcaseView: MaterialShowcaseView) {
                                setPrefShowcaseMainactivityFinished(true)
                                scope.launch {
                                    events.setShowcaseState(ShowcaseState.PROCEEDED)
                                }
                            }
                        })
                        .setDismissOnTouch(true)
                        .build()
        )
        sequence.start()
    }

    fun showTutorialOnce(foodsFragment: FoodsFragment, binding: FragmentFoodsBinding, typeId: Int) {
        val mainActivity = foodsFragment.requireActivity()
        if (!isShowcaseMainActivityFinished
                || isShowcaseFoodListFragmentFinished
                || binding.recyclerView.getChildAt(0) == null)
            return
        if ((mainActivity as MainActivity).currentPagerPosition + 1 != typeId) return

        val config = ShowcaseConfig()
        config.delay = SHOWCASE_DELAY

        val sequence = MaterialShowcaseSequence(mainActivity, SHOWCASE_ID_FOODLISTFRAGMENT)
        sequence.setConfig(config)

        // Kind Spinner tutorial
        sequence.addSequenceItem(
                MaterialShowcaseView.Builder(mainActivity)
                        .setTarget(binding.kindSpinner)
                        .setContentText(mainActivity.getString(R.string.tutorial_kind_text))
                        .setDismissText(mainActivity.getString(android.R.string.ok))
                        .withRectangleShape()
                        .setDismissOnTouch(true)
                        .build()
        )

        sequence.addSequenceItem(
                MaterialShowcaseView.Builder(mainActivity)
                        .setTarget(binding.sortSpinner)
                        .setContentText(mainActivity.getString(R.string.tutorial_sort_text))
                        .setDismissText(mainActivity.getString(android.R.string.ok))
                        .withRectangleShape()
                        .setDismissOnTouch(true)
                        .build()
        )

        // List tutorial
        val view = binding.recyclerView.getChildAt(0)
        sequence.addSequenceItem(
                MaterialShowcaseView.Builder(mainActivity)
                        .setTarget(view)
                        .setContentText(mainActivity.getString(R.string.tutorial_food_list_text))
                        .setDismissText(mainActivity.getString(android.R.string.ok))
                        .withRectangleShape()
                        .setListener(object : IShowcaseListener {
                            override fun onShowcaseDisplayed(materialShowcaseView: MaterialShowcaseView) {
                            }

                            override fun onShowcaseDismissed(materialShowcaseView: MaterialShowcaseView) {
                                setPrefShowcaseFoodListFragmentFinished(true)

                                try {
                                    val layout = mainActivity.layoutInflater.inflate(R.layout.view_tutorial_finished, null)
                                    val alert = AlertDialog.Builder(foodsFragment.requireContext())
                                    alert.setView(layout)
                                    alert.setPositiveButton("OK") { _, _ ->
                                    }
                                    alert.show()
                                } catch (exception: Exception) {
                                    exception.printStackTrace()
                                }
                            }
                        })
                        .setDismissOnTouch(true)
                        .build()
        )
        sequence.start()
    }

    companion object {
        const val SHOWCASE_DELAY: Long = 500

        const val SHOWCASE_ID_MAINACTIVITY = "SHOWCASE_ID_MAINACTIVITY"
        const val SHOWCASE_ID_FOODLISTFRAGMENT = "SHOWCASE_ID_FOODLISTFRAGMENT"

        const val PREF_KEY_SHOWCASE_MAINACTIVITY_FINISHED = "PREF_KEY_MAINACTIVITY_FINISHED"
        const val PREF_KEY_SHOWCASE_FOODLISTFRAGMENT_FINISHED = "PREF_KEY_FOODLISTFRAGMENT_FINISHED"
    }
}
