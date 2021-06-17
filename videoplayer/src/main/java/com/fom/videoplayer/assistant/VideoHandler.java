package com.fom.videoplayer.assistant;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.widget.SeekBar;

import com.fom.videoplayer.R;
import com.fom.videoplayer.databinding.ActivityVideoPlayerBinding;
import com.fom.videoplayer.ui.UI;

public class VideoHandler<handler> implements MediaPlayer.OnPreparedListener, SeekBar.OnSeekBarChangeListener {

    private static VideoHandler<Object> objectVideoHandler;
    private ActivityVideoPlayerBinding binding;
    private Uri uri;

    public static VideoHandler<Object> videoHandler() {
        return objectVideoHandler == null ? objectVideoHandler = new VideoHandler<>() : objectVideoHandler;
    }

    public VideoHandler<handler> setBinding(ActivityVideoPlayerBinding binding) {
        this.binding = binding;
        return this;
    }

    public VideoHandler<handler> setVideoUri(Uri uri) {
        this.uri = uri;
        return this;
    }

    public void init() {
        binding.videoView.setVideoURI(uri);
        binding.videoView.setOnPreparedListener(this);
        binding.seekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        updateDuration();
        binding.seekBar.setMax(binding.videoView.getDuration());
        start();
    }

    public void start() {
        if (!binding.videoView.isPlaying()) {
            binding.videoView.start();
            handler.postDelayed(runnable, 100);
            updatePlayPauseButton();
        }
    }

    public void pause() {
        binding.videoView.pause();
        handler.removeCallbacks(runnable);
        updatePlayPauseButton();
    }

    public void stop() {
        pause();
        binding.videoView.stopPlayback();
        binding.videoView.seekTo(0);
        binding.seekBar.setProgress(0);
        updateDuration();
    }

    public void seekTo(int mSec) {
        pause();
        binding.videoView.seekTo(mSec);
        start();
    }

    private void updatePlayPauseButton() {
        binding.ivPlayPause.setImageResource(binding.videoView.isPlaying() ? R.drawable.ic_pause : R.drawable.ic_play);
    }

    private void updateDuration() {
        binding.tvDuration.setText(UI.getTimeInMinSecFormat(binding.videoView.getDuration()));
        binding.tvCount.setText(UI.getTimeInMinSecFormat(binding.videoView.getCurrentPosition()));
    }

    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (binding.videoView.getCurrentPosition() <= binding.videoView.getDuration()) {
                binding.seekBar.setProgress(binding.videoView.getCurrentPosition());
                handler.postDelayed(runnable, 100);
                updateDuration();
            } else {
                stop();
            }
        }
    };

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser)
            binding.videoView.seekTo(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        pause();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        start();
    }
}
