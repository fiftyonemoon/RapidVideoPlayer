package com.fom.videoplayer.ui;

import android.content.Context;
import android.preference.PreferenceManager;

import com.fom.videoplayer.constant.Constant;

public class Preference {

    public static void setBrightness(Context context, int value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(Constant.preference_brightness, value).apply();
    }

    public static int getBrightness(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(Constant.preference_brightness, 100); // default value is 100 for full brightness
    }

    public static void setVolume(Context context, int value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putFloat(Constant.preference_brightness, value).apply();
    }

    public static int getVolume(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(Constant.preference_volume, 100); // default value is 100 for full volume
    }

    public void removeBrightness(Context context){
        PreferenceManager.getDefaultSharedPreferences(context).edit().remove(Constant.preference_brightness).apply();
    }

    public void removeVolume(Context context){
        PreferenceManager.getDefaultSharedPreferences(context).edit().remove(Constant.preference_volume).apply();
    }
}
