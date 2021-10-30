package com.sakurafish.pockettoushituryou.ui.help

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HelpViewModel @Inject constructor() : ViewModel() {

    private var _url = MutableLiveData<String>().apply {
        value = "file:///android_asset/www/help.html"
    }
    val url: LiveData<String> = _url
}