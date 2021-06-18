package com.fom.videoplayer;

import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.fom.videoplayer.assistant.RapidVideo;
import com.fom.videoplayer.constant.Constant;
import com.fom.videoplayer.databinding.ActivityVideoPlayerBinding;
import com.fom.videoplayer.gesture.Gesture;

public class RapidVideoPlayer extends AppCompatActivity {

    private ActivityVideoPlayerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        binding = ActivityVideoPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initClicks();
        initVideoView();
        initializeGesture();
    }

    private void initClicks() {
        binding.ivBack.setOnClickListener(this::onBackClick);
        binding.ivExtra.setOnClickListener(this::onExtraClick);
        binding.ivMore.setOnClickListener(this::onMoreClick);
        binding.ivLock.setOnClickListener(this::onLockClick);
        binding.ivPrev.setOnClickListener(this::onPrevClick);
        binding.ivPlayPause.setOnClickListener(this::onPlayPauseClick);
        binding.ivNext.setOnClickListener(this::onNextClick);
        binding.ivRatio.setOnClickListener(this::onRatioClick);

        if (binding.ivBackward != null)
            binding.ivBackward.setOnClickListener(this::onBackwardClick);
        if (binding.ivForward != null)
            binding.ivForward.setOnClickListener(this::onForwardClick);
    }

    private void initVideoView() {
        String path = getIntent().getStringExtra(Constant.intent_extra_path);
        Uri uri = path == null ? getIntent().getParcelableExtra(Constant.intent_extra_uri) : Uri.parse(path);

        RapidVideo.videoHandler()
                .with(this)
                .window(getWindow())
                .setBinding(binding)
                .setVideoUri(uri)
                .init();
    }

    private void initializeGesture() {
        Gesture.detect().view(binding.gesturePanel).listen(listener);
    }

    private void onBackClick(View view) {
        onBackPressed();
    }

    public void onExtraClick(View view) {
        binding.contentPanel.setVisibility(binding.contentPanel.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    private void onMoreClick(View view) {
    }

    private void onLockClick(View view) {
    }

    private void onPrevClick(View view) {
    }

    private void onPlayPauseClick(View view) {
        if (binding.videoView.isPlaying()) {
            RapidVideo.videoHandler().pause();
        } else RapidVideo.videoHandler().start();
    }

    private void onNextClick(View view) {
    }

    private void onRatioClick(View view) {
    }

    private void onBackwardClick(View view) {
        int mSec = binding.videoView.getCurrentPosition() - (10 * 1000);
        RapidVideo.videoHandler().seekTo(mSec);
    }

    private void onForwardClick(View view) {
        int mSec = binding.videoView.getCurrentPosition() + (10 * 1000);
        RapidVideo.videoHandler().seekTo(mSec);
    }

    private final Gesture.GestureListener listener = new Gesture.GestureListener() {

        @Override
        public void onDoubleTap() {
            System.out.println("double tap");
        }

        @Override
        public void onSingleTap() {
            if (binding.contentPanel.getVisibility() == View.VISIBLE) {
                binding.contentPanel.setVisibility(View.GONE);
                return;
            }
            binding.topPanel.setVisibility(binding.topPanel.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            binding.bottomPanel.setVisibility(binding.bottomPanel.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        }

        @Override
        public void onLeftToRightSwipe(int value) {
            int mSec = binding.videoView.getCurrentPosition() - value; // value variable is negative, do minus so it's become positive variable
            RapidVideo.videoHandler().seekTo(mSec);
        }

        @Override
        public void onRightToLeftSwipe(int value) {
            int mSec = binding.videoView.getCurrentPosition() - value;
            RapidVideo.videoHandler().seekTo(mSec);
        }

        @Override
        public void onTopToBottomSwipeRight(int value) {

            int maxVolume = RapidVideo.videoHandler().getMaxVolume();
            int percentage = RapidVideo.videoHandler().getVolumeInPercentage();

            if (percentage >= 0 && percentage <= 100) {
                int volume = (percentage * maxVolume) / 100;
                RapidVideo.videoHandler().setVolume(volume, false);
            }
        }

        @Override
        public void onTopToBottomSwipeLeft(int value) {

            int brightness = RapidVideo.videoHandler().getBrightness();

            WindowManager.LayoutParams layout = getWindow().getAttributes();

            if (brightness >= 0 && brightness <= 100) {
                layout.screenBrightness = (float) (1.0d - (Math.log((double) 100 - brightness) / Math.log(100.0d)));
                RapidVideo.videoHandler().setBrightness(false);
            }

            getWindow().setAttributes(layout);
        }

        @Override
        public void onBottomToTopSwipeRight(int value) {

            int maxVolume = RapidVideo.videoHandler().getMaxVolume();
            int percentage = RapidVideo.videoHandler().getVolumeInPercentage();

            if (percentage >= 0 && percentage <= 100) {
                int volume = (percentage * maxVolume) / 100;
                RapidVideo.videoHandler().setVolume(volume, true);
            }
        }

        @Override
        public void onBottomToTopSwipeLeft(int value) {

            int brightness = RapidVideo.videoHandler().getBrightness();

            WindowManager.LayoutParams layout = getWindow().getAttributes();

            if (brightness >= 0 && brightness <= 100) {
                layout.screenBrightness = (float) (1.0d - (Math.log((double) 100 - brightness) / Math.log(100.0d)));
                System.out.println(brightness + "-" + layout.screenBrightness);
                RapidVideo.videoHandler().setBrightness(true);
            }

            getWindow().setAttributes(layout);
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                RapidVideo.videoHandler().updateStreamVolume(true);
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                RapidVideo.videoHandler().updateStreamVolume(false);
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    protected void onStop() {
        RapidVideo.videoHandler().stop();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
