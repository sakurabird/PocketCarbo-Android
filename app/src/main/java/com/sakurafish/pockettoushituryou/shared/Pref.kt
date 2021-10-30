package com.sakurafish.pockettoushituryou.shared

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Pref @Inject constructor(private val preferences: SharedPreferences) {

    fun setPref(key: String?, value: String?) {
        val editor = editor
        editor.putString(key, value)
        editor.commit()
    }

    fun getPrefString(key: String?): String? {
        return preferences.getString(key, null)
    }

    fun setPref(key: String?, value: Boolean) {
        val editor = editor
        editor.putBoolean(key, value)
        editor.commit()
    }

    fun getPrefBool(key: String?, defaultBool: Boolean): Boolean {
        return preferences.getBoolean(key, defaultBool)
    }

    fun setPref(key: String?, value: Int) {
        val editor = editor
        editor.putInt(key, value)
        editor.commit()
    }

    fun getPrefInt(key: String?): Int {
        return preferences.getInt(key, 0)
    }

    fun getPrefLong(key: String?, defaultValue: Long): Long {
        return preferences.getLong(key, defaultValue)
    }

    fun setPrefLong(key: String?, value: Long) {
        val editor = editor
        editor.putLong(key, value)
        editor.commit()
    }

    val editor: SharedPreferences.Editor
        get() = preferences.edit()
}