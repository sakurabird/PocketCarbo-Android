package com.sakurafish.pockettoushituryou.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class HelpViewModel @Inject constructor() : ViewModel() {

    private var _url = MutableLiveData<String>().apply {
        value = "file:///android_asset/www/help.html"
    }
    val url: LiveData<String> = _url
}