package com.sakurafish.pockettoushituryou.viewmodel

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.net.URISyntaxException
import javax.inject.Inject

class WebViewViewModel @Inject constructor(private val context: Context) : ViewModel() {

    private val _initAction = MutableLiveData<Boolean>().apply {
        value = true
    }
    val initAction: LiveData<Boolean> = _initAction

    private val _url = MutableLiveData<String>()
    val url: LiveData<String> = _url

    private val _webViewClient = MutableLiveData<WebViewClient>().apply {
        value = MyWebViewClient()
    }
    val webViewClient: LiveData<WebViewClient> = _webViewClient

    fun setUrl(url: String) {
        _url.value = url
    }

    fun enableInitAction(enable: Boolean) {
        _initAction.value = enable
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
    }
}
