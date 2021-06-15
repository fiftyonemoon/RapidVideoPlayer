package com.fom.videoplayer;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.fom.videoplayer.databinding.ActivityVideoPlayerBinding;

public class VideoPlayer extends AppCompatActivity {

    ActivityVideoPlayerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.ivExtra.setOnClickListener(this::onExtraClick);
    }

    public void onExtraClick(View view) {
        binding.contentPanel.setVisibility(View.VISIBLE);
    }

}
