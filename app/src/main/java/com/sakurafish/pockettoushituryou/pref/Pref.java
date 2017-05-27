package com.sakurafish.pockettoushituryou.pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Pref {

    private Pref() {
    }

    public static SharedPreferences getSharedPreferences(final Context con) {
        return PreferenceManager.getDefaultSharedPreferences(con);
    }

    public static void setPref(final Context con, final String key, final String value) {
        final SharedPreferences pref = getSharedPreferences(con);
        final SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getPrefString(final Context con, final String key) {
        final SharedPreferences pref = getSharedPreferences(con);
        return pref.getString(key, null);
    }

    public static void setPref(final Context con, final String key, boolean value) {
        final SharedPreferences pref = getSharedPreferences(con);
        final SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean getPrefBool(final Context con, final String key, final boolean defaultBool) {
        final SharedPreferences pref = getSharedPreferences(con);
        return pref.getBoolean(key, defaultBool);
    }

    public static void setPref(final Context con, final String key, final int value) {
        final SharedPreferences pref = getSharedPreferences(con);
        final SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static int getPrefInt(final Context con, final String key) {
        final SharedPreferences pref = getSharedPreferences(con);
        return pref.getInt(key, 0);
    }

    public static long getPrefLong(final Context con, final String key, final long defaultValue) {
        final SharedPreferences pref = getSharedPreferences(con);
        return pref.getLong(key, defaultValue);
    }

    public static void setPrefLong(final Context con, final String key, final long value) {
        final SharedPreferences pref = getSharedPreferences(con);
        final SharedPreferences.Editor editor = pref.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static SharedPreferences.Editor getEditor(final Context con) {
        final SharedPreferences pref = getSharedPreferences(con);
        return pref.edit();
    }
}
