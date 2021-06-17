package com.fom.videoplayer.ui;

import android.content.Context;
import android.preference.PreferenceManager;

public class Preference {

    public static void setBrightness(Context context, int value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt("", value).apply();
    }
}
