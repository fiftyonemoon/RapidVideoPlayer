package com.fom.videoplayer.assistant;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;

import com.fom.videoplayer.R;
import com.fom.videoplayer.databinding.ActivityVideoPlayerBinding;
import com.fom.videoplayer.enums.AspectRatio;
import com.fom.videoplayer.model.MetaData;
import com.fom.videoplayer.ui.Preference;
import com.fom.videoplayer.ui.UI;

import androidx.constraintlayout.widget.ConstraintLayout;

public class VideoHandler<handler> {

    private Activity activity;
    private AudioManager audioManager;
    private static VideoHandler<Object> objectVideoHandler;
    private ActivityVideoPlayerBinding binding;
    private MetaData metaData;
    private Uri uri;
    private AspectRatio ratio;
    private int volume = 100;
    private int brightness = 100;

    public static VideoHandler<Object> videoHandler() {
        return objectVideoHandler == null ? objectVideoHandler = new VideoHandler<>() : objectVideoHandler;
    }

    public VideoHandler<handler> with(Activity activity) {
        this.activity = activity;
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
        audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        metaData = UI.getVideoMetaData(activity, uri);

        updateAspectRatio();
        updateBrightness(getBrightness());
        updateBrightnessPercentage();
        updateVolumePercentage();

        binding.videoView.setVideoURI(uri);
        binding.videoView.setOnPreparedListener(preparedListener);
        binding.seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
    }

    private final MediaPlayer.OnPreparedListener preparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mp.setOnSeekCompleteListener(seekCompleteListener);
            binding.seekBar.setMax(binding.videoView.getDuration());
            updateDuration();
            start();
        }
    };

    private final MediaPlayer.OnSeekCompleteListener seekCompleteListener = new MediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(MediaPlayer mp) {
            if (binding.videoView.isPlaying())
                start(); // after seek complete start video at new position if video is in playing mode
            else updateDuration(); // after seek complete update duration if video is in pause mode
        }
    };

    private final SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
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
            // nothing to do
        }
    };

    public void start() {
        if (!binding.videoView.isPlaying()) {
            binding.videoView.start();
            seekHandler.postDelayed(seekRunnable, 100);
            updatePlayPauseButton();
        }
    }

    public void pause() {
        if (binding.videoView.isPlaying()) {
            binding.videoView.pause();
            seekHandler.removeCallbacks(seekRunnable);
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

    public void setBrightness(float brightness, boolean isIncrease) {

        updateBrightness(brightness);
        updateDisplayMessage(false);

        Preference.setBrightness(activity, this.brightness);

        if (isIncrease && this.brightness != 100) this.brightness++;
        else if (!isIncrease && this.brightness != 0) this.brightness--;
    }

    public void setAspectRatio(AspectRatio ratio) {
        this.ratio = ratio;
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.videoView.getLayoutParams();
        params.dimensionRatio = ratio.getRatio();
        binding.videoView.setLayoutParams(params);
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

    public int getBrightnessInPercentage() {
        return brightness;
    }

    public int getBrightness() {
        return Preference.getBrightness(activity);
    }

    public AspectRatio getAspectRatio() {
        return ratio;
    }

    public void updateAudioManagerStreamVolume(boolean isVolumeRise) {
        audioManager.adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                isVolumeRise ? AudioManager.ADJUST_RAISE : AudioManager.ADJUST_LOWER,
                AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI
        );
        updateVolumePercentage();
    }

    private void updateAspectRatio() {
        AspectRatio ratio = metaData.getWidth() == metaData.getHeight() ? AspectRatio.one_one :
                metaData.getWidth() > metaData.getHeight() ? AspectRatio.sixteen_nine : AspectRatio.nine_sixteen;
        setAspectRatio(ratio);
    }

    public void updateBrightness(float brightness) {

        brightness = brightness < 0 ? 0 : Math.min(brightness, 1);

        WindowManager.LayoutParams layoutParams = activity.getWindow().getAttributes();
        layoutParams.screenBrightness = brightness;
        activity.getWindow().setAttributes(layoutParams);
    }

    public void updateVolumePercentage() {
        volume = (getVolume() * 100) / getMaxVolume();
    }

    public void updateBrightnessPercentage() {
        this.brightness = getBrightness();
        float brightness = UI.getAbsFloatValueForBrightness(this.brightness);
        updateBrightness(brightness);
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
        String percentage = (isVolume ? getVolumeInPercentage() : getBrightnessInPercentage()) + "%";

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

    public void hideUnlockView() {
        unlockHandler.removeCallbacks(unlockRunnable);
        unlockHandler.postDelayed(unlockRunnable, 1000);
    }

    private final Handler unlockHandler = new Handler(Looper.myLooper());
    private final Runnable unlockRunnable = new Runnable() {
        @Override
        public void run() {
            binding.ivUnlock.setVisibility(View.GONE);
        }
    };

    private final Handler messageHandler = new Handler(Looper.myLooper());
    private final Runnable messageRunnable = new Runnable() {
        @Override
        public void run() {
            binding.tvPercentage.setVisibility(View.GONE);
        }
    };

    private final Handler seekHandler = new Handler();
    private final Runnable seekRunnable = new Runnable() {
        @Override
        public void run() {

            if (binding.videoView.isPlaying() && binding.videoView.getCurrentPosition() <= binding.videoView.getDuration()) {
                updateSeekBar();
                updateDuration();
                seekHandler.postDelayed(seekRunnable, 100);
            } else {
                // after video complete do things
                updatePlayPauseButton();
            }
        }
    };

}
