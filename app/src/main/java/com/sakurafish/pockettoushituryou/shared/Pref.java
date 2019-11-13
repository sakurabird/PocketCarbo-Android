package com.sakurafish.pockettoushituryou.shared;

import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Pref {
    private SharedPreferences preferences;

    @Inject
    public Pref(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public void setPref(final String key, final String value) {
        final SharedPreferences.Editor editor = getEditor();
        getEditor().putString(key, value);
        editor.commit();
    }

    public String getPrefString(final String key) {
        return this.preferences.getString(key, null);
    }

    public void setPref(final String key, boolean value) {
        final SharedPreferences.Editor editor = getEditor();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean getPrefBool(final String key, final boolean defaultBool) {
        return this.preferences.getBoolean(key, defaultBool);
    }

    public void setPref(final String key, final int value) {
        final SharedPreferences.Editor editor = getEditor();
        editor.putInt(key, value);
        editor.commit();
    }

    public int getPrefInt(final String key) {
        return this.preferences.getInt(key, 0);
    }

    public long getPrefLong(final String key, final long defaultValue) {
        return this.preferences.getLong(key, defaultValue);
    }

    public void setPrefLong(final String key, final long value) {
        final SharedPreferences.Editor editor = getEditor();
        editor.putLong(key, value);
        editor.commit();
    }

    public SharedPreferences.Editor getEditor() {
        return this.preferences.edit();
    }
}
