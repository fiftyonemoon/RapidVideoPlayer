package com.fom.videoplayer.assistant;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;

import com.fom.videoplayer.R;
import com.fom.videoplayer.databinding.ActivityVideoPlayerBinding;
import com.fom.videoplayer.ui.UI;

import androidx.annotation.RequiresApi;

public class VideoHandler<handler> implements MediaPlayer.OnPreparedListener, SeekBar.OnSeekBarChangeListener, MediaPlayer.OnSeekCompleteListener {

    private Context context;
    private Window window;
    private AudioManager audioManager;
    private static VideoHandler<Object> objectVideoHandler;
    private ActivityVideoPlayerBinding binding;
    private Uri uri;
    private int volume = 100;
    private int brightness = 100;

    public static VideoHandler<Object> videoHandler() {
        return objectVideoHandler == null ? objectVideoHandler = new VideoHandler<>() : objectVideoHandler;
    }

    public VideoHandler<handler> with(Context context) {
        this.context = context;
        return this;
    }

    public VideoHandler<handler> window(Window window) {
        this.window = window;
        return this;
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
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        updateVolumePercentage();
        updateBrightnessPercentage();
        binding.videoView.setVideoURI(uri);
        binding.videoView.setOnPreparedListener(this);
        binding.seekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.setOnSeekCompleteListener(this);
        binding.seekBar.setMax(binding.videoView.getDuration());
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
        if (binding.videoView.isPlaying()) {
            binding.videoView.pause();
            handler.removeCallbacks(runnable);
            updatePlayPauseButton();
        }
    }

    public void stop() {
        if (binding.videoView.isPlaying()) {
            pause();
            binding.videoView.stopPlayback();
        }
    }

    public void seekTo(int mSec) {

        binding.videoView.seekTo(mSec);

        if (!binding.videoView.isPlaying()) { // call when user seek video in pause mode
            updateSeekBar();
            updateDuration();
        }
    }

    public void setVolume(int volume, boolean isIncrease) {

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);

        updateDisplayMessage(true);

        if (isIncrease && this.volume != 100) this.volume++;
        else if (!isIncrease && this.volume != 0) this.volume--;
    }

    public void setBrightness(boolean isIncrease) {

        updateDisplayMessage(false);

        if (isIncrease && this.brightness != 100) this.brightness++;
        else if (!isIncrease && this.brightness != 0) this.brightness--;
    }

    public int getVolumeInPercentage() {
        return volume;
    }

    public int getVolume() {
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    public int getMaxVolume() {
        return audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public int getMinVolume() {
        return audioManager.getStreamMinVolume(AudioManager.STREAM_MUSIC);
    }

    public int getBrightness() {
        return brightness;
    }

    public void updateVolumePercentage() {
        volume = (getVolume() * 100) / getMaxVolume();
    }

    public void updateStreamVolume(boolean isVolumeRise) {
        audioManager.adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                isVolumeRise ? AudioManager.ADJUST_RAISE : AudioManager.ADJUST_LOWER,
                AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI
        );
        updateVolumePercentage();
    }

    public void updateBrightnessPercentage() {
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.screenBrightness = 1f;
        window.setAttributes(layoutParams);
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

    private void updateDisplayMessage(boolean isVolume) {
        String percentage = (isVolume ? getVolumeInPercentage() : brightness) + "%";

        binding.tvPercentage.setText(percentage);
        binding.tvPercentage.setVisibility(View.VISIBLE);

        binding.tvPercentage.setCompoundDrawablesWithIntrinsicBounds(0,
                isVolume ? volume == 0 ? R.drawable.ic_volume_mute :
                        volume > 0 && volume <= 33 ? R.drawable.ic_volume_low :
                                volume > 33 && volume <= 77 ? R.drawable.ic_volume_medium :
                                        volume > 77 && volume <= 100 ? R.drawable.ic_volume_high : 0 :
                        brightness >= 0 && brightness <= 33 ? R.drawable.ic_brightness_low :
                                brightness > 33 && brightness <= 77 ? R.drawable.ic_brightness_medium :
                                        brightness > 77 && brightness <= 100 ? R.drawable.ic_brightness_high : 0, 0, 0
        );

        messageHandler.removeCallbacks(messageRunnable);
        messageHandler.postDelayed(messageRunnable, 500);
    }

    private final Handler messageHandler = new Handler(Looper.myLooper());
    private final Runnable messageRunnable = new Runnable() {
        @Override
        public void run() {
            binding.tvPercentage.setVisibility(View.GONE);
        }
    };

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
