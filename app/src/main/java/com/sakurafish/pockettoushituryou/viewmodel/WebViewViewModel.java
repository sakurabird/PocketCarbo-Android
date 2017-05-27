package com.sakurafish.pockettoushituryou.viewmodel;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.URISyntaxException;

import javax.inject.Inject;

public final class WebViewViewModel extends BaseObservable implements ViewModel {

    private final Context context;

    private String url;

    private final WebViewClient webViewClient;

    @Inject
    WebViewViewModel(Context context) {
        this.context = context;
        webViewClient = new MyWebViewClient();
    }

    @Override
    public void destroy() {
        // Nothing to do
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(@NonNull String url) {
        this.url = url;
    }

    public WebViewClient getWebViewClient() {
        return webViewClient;
    }

    private class MyWebViewClient extends WebViewClient {
        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
            if (TextUtils.isEmpty(url)) {
                return false;
            }

            if (url.startsWith("tel:")) {
                final Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                context.startActivity(intent);
                return true;
            }
            if (url.startsWith("mailto:")) {
                try {
                    final Intent intent = Intent.parseUri(url, 0);
                    context.startActivity(intent);
                    return true;
                } catch (URISyntaxException e) {
                    // Intent schemeが不正.
                } catch (ActivityNotFoundException e) {
                    // 対象アプリがインストールされていない.
                }
                return true;
            }
            if (url.startsWith("intent:")) {
                try {
                    final Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    context.startActivity(intent);
                    return true;
                } catch (URISyntaxException e) {
                    // Intent schemeが不正.
                } catch (ActivityNotFoundException e) {
                    // 対象アプリがインストールされていない.
                }
                return true;
            }
            return false;
        }

        @Override
        @TargetApi(Build.VERSION_CODES.N)
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return shouldOverrideUrlLoading(view, request.getUrl().toString());
        }
    }
}
