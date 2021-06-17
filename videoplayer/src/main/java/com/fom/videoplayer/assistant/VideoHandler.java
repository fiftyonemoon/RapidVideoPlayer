package com.fom.videoplayer.assistant;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.widget.SeekBar;

import com.fom.videoplayer.R;
import com.fom.videoplayer.databinding.ActivityVideoPlayerBinding;
import com.fom.videoplayer.ui.UI;

public class VideoHandler<handler> implements MediaPlayer.OnPreparedListener, SeekBar.OnSeekBarChangeListener, MediaPlayer.OnSeekCompleteListener {

    private MediaPlayer mediaPlayer;
    private static VideoHandler<Object> objectVideoHandler;
    private ActivityVideoPlayerBinding binding;
    private Uri uri;
    private float volume = 15f;

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
        mediaPlayer = mp;
        mediaPlayer.setOnSeekCompleteListener(this);
        binding.seekBar.setMax(binding.videoView.getDuration());
        setVolume(volume);
        updateDuration();
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
        updateDuration();
    }

    public void seekTo(int mSec) {

        binding.videoView.seekTo(mSec);

        if (!binding.videoView.isPlaying()) { // call when user seek video in pause mode
            updateSeekBar();
            updateDuration();
        }
    }

    public void setVolume(float volume) {
        this.volume = volume;
        mediaPlayer.setVolume(volume, volume);
    }

    public float getVolume(){
        return volume;
    }

    private void updatePlayPauseButton() {
        binding.ivPlayPause.setImageResource(binding.videoView.isPlaying() ? R.drawable.ic_pause : R.drawable.ic_play);
    }

    private void updateDuration() {
        binding.tvDuration.setText(UI.getTimeInMinSecFormat(binding.videoView.getDuration()));
        binding.tvCount.setText(UI.getTimeInMinSecFormat(binding.videoView.getCurrentPosition()));
    }

    private void updateSeekBar() {
        binding.seekBar.setProgress(binding.videoView.getCurrentPosition());
    }

    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {

            if (binding.videoView.isPlaying() && binding.videoView.getCurrentPosition() <= binding.videoView.getDuration()) {
                updateSeekBar();
                updateDuration();
                handler.postDelayed(runnable, 100);
            } else {
                // after video complete do things
                updatePlayPauseButton();
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
        pause(); // pause the video on user start to seek
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //nothing to do
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        if (binding.videoView.isPlaying())
            start(); // after seek complete start video at new position if video is in playing mode
        else updateDuration(); // after seek complete update duration if video is in pause mode
    }
}
