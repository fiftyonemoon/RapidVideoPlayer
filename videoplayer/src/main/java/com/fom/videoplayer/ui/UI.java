package com.fom.videoplayer.ui;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class UI {

    /**
     * @return time in hh:mm:ss format.
     */
    public static String getTimeInFormat(int value) {
        return String.format(
                Locale.ENGLISH,
                "%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(value),
                TimeUnit.MILLISECONDS.toMinutes(value),
                TimeUnit.MILLISECONDS.toSeconds(value) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(value)));
    }

    /**
     * @return time in mm:ss format.
     */
    public static String getTimeInMinSecFormat(int value) {
        return String.format(
                Locale.ENGLISH,
                "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(value),
                TimeUnit.MILLISECONDS.toSeconds(value) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(value)));
    }

    public static float getAbsFloatValueForBrightness(int value){
        return (float) (1.0d - (Math.log((double) 100 - value) / Math.log(100.0d)));
    }
}
