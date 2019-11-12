package com.sakurafish.pockettoushituryou.viewmodel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sakurafish.pockettoushituryou.view.helper.ShowcaseHelper
import javax.inject.Inject

class HelpViewModel @Inject constructor(
        private val showcaseHelper: ShowcaseHelper
) : ViewModel() {

    private var _url = MutableLiveData<String>().apply {
        value = "file:///android_asset/www/help.html"
    }
    val url: LiveData<String> = _url

    fun onClickTurorialResetButton(@Suppress("UNUSED_PARAMETER") view: View) {
        showcaseHelper.resetShowCase()
    }
}
