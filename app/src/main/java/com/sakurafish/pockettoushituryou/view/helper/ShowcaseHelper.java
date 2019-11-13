package com.sakurafish.pockettoushituryou.view.helper;

import android.content.Context;
import android.widget.Toast;

import com.sakurafish.pockettoushituryou.R;
import com.sakurafish.pockettoushituryou.shared.Pref;

import javax.inject.Inject;
import javax.inject.Singleton;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

/**
 * deano2390/MaterialShowcaseView を使用してチュートリアルを表示するに当たってのヘルパーメソッド
 */
@Singleton
public class ShowcaseHelper {

    private static final String TAG = ShowcaseHelper.class.getSimpleName();

    public static final int SHOWCASE_DELAY = 500;

    public static final String SHOWCASE_ID_MAINACTIVITY = "SHOWCASE_ID_MAINACTIVITY";
    public static final String SHOWCASE_ID_FOODLISTFRAGMENT = "SHOWCASE_ID_FOODLISTFRAGMENT";

    public static final String PREF_KEY_SHOWCASE_MAINACTIVITY_FINISHED = "PREF_KEY_MAINACTIVITY_FINISHED";
    public static final String PREF_KEY_SHOWCASE_FOODLISTFRAGMENT_FINISHED = "PREF_KEY_FOODLISTFRAGMENT_FINISHED";

    public final static String EVENT_SHOWCASE_MAINACTIVITY_FINISHED = "EVENT_SHOWCASE_MAINACTIVITY_FINISHED";


    private final Context context;
    private final Pref pref;

    @Inject
    public ShowcaseHelper(Context context, Pref pref) {
        this.context = context;
        this.pref = pref;
    }

    public void resetShowCase() {
        // チュートリアルを表示できるように設定をリセットする
        pref.setPref(PREF_KEY_SHOWCASE_MAINACTIVITY_FINISHED, false);
        MaterialShowcaseView.resetSingleUse(context, SHOWCASE_ID_MAINACTIVITY);

        pref.setPref(PREF_KEY_SHOWCASE_FOODLISTFRAGMENT_FINISHED, false);
        MaterialShowcaseView.resetSingleUse(context, SHOWCASE_ID_FOODLISTFRAGMENT);

        Toast.makeText(context, context.getString(R.string.tutorial_reset_toast_text), Toast.LENGTH_SHORT).show();
    }

    public void setPrefShowcaseMainactivityFinished(boolean finished) {
        pref.setPref(PREF_KEY_SHOWCASE_MAINACTIVITY_FINISHED, finished);
    }

    public boolean isShowcaseMainActivityFinished() {
        // true: 表示済みをあらわす
        return pref.getPrefBool(PREF_KEY_SHOWCASE_MAINACTIVITY_FINISHED, false);
    }

    public void setPrefShowcaseFoodListFragmentFinished(boolean finished) {
        pref.setPref(PREF_KEY_SHOWCASE_FOODLISTFRAGMENT_FINISHED, finished);
    }

    public boolean isShowcaseFoodListFragmentFinished() {
        // true: 表示済みをあらわす
        return pref.getPrefBool(PREF_KEY_SHOWCASE_FOODLISTFRAGMENT_FINISHED, false);
    }
}
