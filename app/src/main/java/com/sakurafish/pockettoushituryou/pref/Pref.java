package com.sakurafish.pockettoushituryou.pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Pref {

    private final Context context;

    @Inject
    public Pref(Context context) {
        this.context = context;
    }

    public SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setPref(final String key, final String value) {
        final SharedPreferences pref = getSharedPreferences();
        final SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getPrefString(final String key) {
        final SharedPreferences pref = getSharedPreferences();
        return pref.getString(key, null);
    }

    public void setPref(final String key, boolean value) {
        final SharedPreferences pref = getSharedPreferences();
        final SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean getPrefBool(final String key, final boolean defaultBool) {
        final SharedPreferences pref = getSharedPreferences();
        return pref.getBoolean(key, defaultBool);
    }

    public void setPref(final String key, final int value) {
        final SharedPreferences pref = getSharedPreferences();
        final SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public int getPrefInt(final String key) {
        final SharedPreferences pref = getSharedPreferences();
        return pref.getInt(key, 0);
    }

    public long getPrefLong(final String key, final long defaultValue) {
        final SharedPreferences pref = getSharedPreferences();
        return pref.getLong(key, defaultValue);
    }

    public void setPrefLong(final String key, final long value) {
        final SharedPreferences pref = getSharedPreferences();
        final SharedPreferences.Editor editor = pref.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public SharedPreferences.Editor getEditor() {
        final SharedPreferences pref = getSharedPreferences();
        return pref.edit();
    }
}
