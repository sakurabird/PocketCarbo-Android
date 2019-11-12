package com.sakurafish.pockettoushituryou.viewmodel

import android.annotation.TargetApi
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.BaseObservable
import java.net.URISyntaxException
import javax.inject.Inject

class WebViewViewModel @Inject
internal constructor(private val context: Context) : BaseObservable(), ViewModel {

    var url: String? = null

    val webViewClient: WebViewClient

    init {
        webViewClient = MyWebViewClient()
    }

    override fun destroy() {
        // Nothing to do
    }

    private inner class MyWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            if (TextUtils.isEmpty(url)) {
                return false
            }

            if (url.startsWith("tel:")) {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse(url))
                context.applicationContext.startActivity(intent)
                return true
            }
            if (url.startsWith("mailto:")) {
                try {
                    val intent = Intent.parseUri(url, 0)
                    context.applicationContext.startActivity(intent)
                    return true
                } catch (e: URISyntaxException) {
                    // Intent schemeが不正.
                } catch (e: ActivityNotFoundException) {
                    // 対象アプリがインストールされていない.
                }

                return true
            }
            if (url.startsWith("intent:")) {
                try {
                    val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                    context.applicationContext.startActivity(intent)
                    return true
                } catch (e: URISyntaxException) {
                    // Intent schemeが不正.
                } catch (e: ActivityNotFoundException) {
                    // 対象アプリがインストールされていない.
                }

                return true
            }
            return false
        }

        @TargetApi(Build.VERSION_CODES.N)
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            return shouldOverrideUrlLoading(view, request.url.toString())
        }
    }
}
