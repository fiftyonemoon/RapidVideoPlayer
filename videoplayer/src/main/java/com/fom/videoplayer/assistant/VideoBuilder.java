package com.fom.videoplayer.assistant;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.fom.videoplayer.RapidVideoPlayer;
import com.fom.videoplayer.constant.Constant;

public class VideoBuilder<builder> {

    private Uri uri;
    private String path;

    public VideoBuilder<builder> setVideoUri(Uri uri) {
        this.uri = uri;
        return this;
    }

    public VideoBuilder<builder> setVideoUri(String path) {
        this.path = path;
        return this;
    }

    public void start(Context context) {
        Intent intent = new Intent(context, RapidVideoPlayer.class);
        intent.putExtra(Constant.intent_extra_uri, uri);
        intent.putExtra(Constant.intent_extra_path, path);
        context.startActivity(intent);
    }
}
