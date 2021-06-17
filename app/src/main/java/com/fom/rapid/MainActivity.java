package com.fom.rapid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.fom.videoplayer.assistant.RapidVideo;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String path = "android.resource://" + getPackageName() + "/" + R.raw.sample2;
        RapidVideo.videoBuilder().setVideoUri(path).start(this);
        finish();

    }

}