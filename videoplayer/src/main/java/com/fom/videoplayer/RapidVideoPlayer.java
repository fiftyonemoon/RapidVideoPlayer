package com.fom.videoplayer;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.fom.videoplayer.assistant.RapidVideo;
import com.fom.videoplayer.constant.Constant;
import com.fom.videoplayer.databinding.ActivityVideoPlayerBinding;
import com.fom.videoplayer.gesture.Gesture;

public class RapidVideoPlayer extends AppCompatActivity {

    public static final String TAG = RapidVideoPlayer.class.getName();
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

        RapidVideo.videoHandler().setBinding(binding).setVideoUri(uri).init();
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

            if (BuildConfig.DEBUG) Log.d(TAG, "onLeftToRightSwipe: " + value);

            int mSec = binding.videoView.getCurrentPosition() - (value);
            RapidVideo.videoHandler().seekTo(mSec);
        }

        @Override
        public void onRightToLeftSwipe(int value) {

            if (BuildConfig.DEBUG) Log.d(TAG, "onRightToLeftSwipe: " + value);

            int mSec = binding.videoView.getCurrentPosition() - value;
            RapidVideo.videoHandler().seekTo(mSec);
        }

        @Override
        public void onTopToBottomSwipeRight(int value) {

            float volume = RapidVideo.videoHandler().getVolume();

            if (volume > 0 && volume <= 15) {
                RapidVideo.videoHandler().setVolume(volume - 0.5f);
            }

            String text = "-" + volume;
            binding.tvPercentage.setText(text);
            binding.tvPercentage.setVisibility(View.VISIBLE);

            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, 500);
        }

        @Override
        public void onTopToBottomSwipeLeft(int value) {

            WindowManager.LayoutParams layout = getWindow().getAttributes();

            if (layout.screenBrightness == -1) {
                layout.screenBrightness = 0.7f;
            } else if (layout.screenBrightness > 0.1) {
                layout.screenBrightness = layout.screenBrightness - 0.1f;
            }

            getWindow().setAttributes(layout);
        }

        @Override
        public void onBottomToTopSwipeRight(int value) {

            float volume = RapidVideo.videoHandler().getVolume();

            if (volume >= 0 && volume < 15) {
                RapidVideo.videoHandler().setVolume(volume + 0.5f);
            }

            String text = "+" + volume;
            binding.tvPercentage.setText(text);
            binding.tvPercentage.setVisibility(View.VISIBLE);
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, 500);
        }

        @Override
        public void onBottomToTopSwipeLeft(int value) {

            WindowManager.LayoutParams layout = getWindow().getAttributes();

            if (layout.screenBrightness == -1) {
                layout.screenBrightness = 0.7f;
            } else if (layout.screenBrightness > 0 && layout.screenBrightness < 1) {
                layout.screenBrightness = layout.screenBrightness + 0.1f;
            }

            getWindow().setAttributes(layout);
        }
    };

    private Handler handler = new Handler(Looper.myLooper());
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            binding.tvPercentage.setVisibility(View.GONE);
        }
    };

    @Override
    protected void onStop() {
        RapidVideo.videoHandler().pause();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
