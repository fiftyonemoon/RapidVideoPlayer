package com.fom.videoplayer.ui;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import com.fom.videoplayer.model.MetaData;

import java.io.File;
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

    public static float getAbsFloatValueForBrightness(int value) {
        return (float) (1.0d - (Math.log((double) 100 - value) / Math.log(100.0d)));
    }

    public static MetaData getVideoMetaData(Context context, Uri mediaUri) {

        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(context, mediaUri);

            String uri = mediaUri.toString();
            String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String millis = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            String w = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            String h = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            String resolution = w + "x" + h;

            File file = new File(mediaUri.getPath());
            long size = file.length();
            long duration = Long.parseLong(millis);
            int width = Integer.parseInt(w);
            int height = Integer.parseInt(h);

            return new MetaData(title, uri, width, height, resolution, size, duration);

        } catch (Exception e) {
            return new MetaData();
        }
    }
}
