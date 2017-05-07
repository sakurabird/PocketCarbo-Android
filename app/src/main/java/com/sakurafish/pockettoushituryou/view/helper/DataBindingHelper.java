package com.sakurafish.pockettoushituryou.view.helper;

import android.databinding.BindingAdapter;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

public class DataBindingHelper {

    /**
     * item_foodlist.xmlの食品名と糖質量のテキストカラーを設定
     * @param view
     * @param colorResId
     */
    @BindingAdapter("foodTextColor")
    public static void setFoodTextColor(TextView view, @ColorRes int colorResId) {
        view.setTextColor(ContextCompat.getColor(view.getContext(), colorResId));
    }
}
