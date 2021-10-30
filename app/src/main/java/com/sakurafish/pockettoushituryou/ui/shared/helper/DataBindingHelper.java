package com.sakurafish.pockettoushituryou.ui.shared.helper;

import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;

import com.sakurafish.pockettoushituryou.R;

public class DataBindingHelper {

    /**
     * item_foodlist.xmlの食品名と糖質量のテキストカラーを設定
     *
     * @param view
     * @param colorResId
     */
    @BindingAdapter("foodTextColor")
    public static void setFoodTextColor(TextView view, @ColorRes int colorResId) {
        view.setTextColor(ContextCompat.getColor(view.getContext(), colorResId));
    }

    @BindingAdapter("goneUnless")
    public static void goneUnless(View view, boolean visible) {
        if (visible) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    @BindingAdapter("favImageColor")
    public static void setFavImageColor(View view, boolean isFavorite) {
        if (isFavorite) {
            ((ImageView) view).setColorFilter(ContextCompat.getColor(view.getContext(), R.color.dark_red));
        } else {
            ((ImageView) view).setColorFilter(ContextCompat.getColor(view.getContext(), R.color.grey600));
        }
    }

    @BindingAdapter("webViewUrl")
    public static void loadUrl(WebView webView, String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        webView.loadUrl(url);
    }

    @BindingAdapter("webViewClient")
    public static void setWebViewClient(WebView webView, WebViewClient client) {
        webView.setWebViewClient(client);
    }
}
