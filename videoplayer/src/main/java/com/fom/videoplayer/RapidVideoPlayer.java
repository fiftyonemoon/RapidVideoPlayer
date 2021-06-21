package com.fom.videoplayer;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
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
import com.fom.videoplayer.enums.AspectRatio;
import com.fom.videoplayer.gesture.Gesture;
import com.fom.videoplayer.ui.UI;

public class RapidVideoPlayer extends AppCompatActivity {

    private ActivityVideoPlayerBinding binding;
    private Gesture.Detector<Object> gestureDetector;

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
        binding.ivUnlock.setOnClickListener(this::onLockClick);
        binding.ivPrev.setOnClickListener(this::onPrevClick);
        binding.ivPlayPause.setOnClickListener(this::onPlayPauseClick);
        binding.ivNext.setOnClickListener(this::onNextClick);
        binding.ivAspectRatio.setOnClickListener(this::onAspectRatioClick);
        binding.ivOrientation.setOnClickListener(this::onOrientationClick);

        if (binding.ivBackward != null)
            binding.ivBackward.setOnClickListener(this::onBackwardClick);
        if (binding.ivForward != null)
            binding.ivForward.setOnClickListener(this::onForwardClick);
    }

    private void initVideoView() {
        String path = getIntent().getStringExtra(Constant.intent_extra_path);
        Uri uri = path == null ? getIntent().getParcelableExtra(Constant.intent_extra_uri) : Uri.parse(path);

        RapidVideo.videoHandler().with(this).setBinding(binding).setVideoUri(uri).init();
    }

    private void initializeGesture() {
        gestureDetector = Gesture.detector();
        gestureDetector.view(binding.gesturePanel).listen(listener);
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
        gestureDetector.setDisable(!gestureDetector.isDisable()); // enable/disable gesture
        RapidVideo.videoHandler().updateContentViews(); // show/hide content views
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

    private void onAspectRatioClick(View view) {
        AspectRatio ratio = RapidVideo.videoHandler().getAspectRatio();
        switch (ratio) {
            case one_one:
                RapidVideo.videoHandler().setAspectRatio(AspectRatio.nine_sixteen);
                break;
            case nine_sixteen:
                RapidVideo.videoHandler().setAspectRatio(AspectRatio.sixteen_nine);
                break;
            case sixteen_nine:
                RapidVideo.videoHandler().setAspectRatio(AspectRatio.one_one);
                break;
        }
    }

    private void onBackwardClick(View view) {
        int mSec = binding.videoView.getCurrentPosition() - (10 * 1000);
        RapidVideo.videoHandler().seekTo(mSec);
    }

    private void onForwardClick(View view) {
        int mSec = binding.videoView.getCurrentPosition() + (10 * 1000);
        RapidVideo.videoHandler().seekTo(mSec);
    }

    private void onOrientationClick(View view) {
        int orientation = getResources().getConfiguration().orientation;
        setRequestedOrientation(orientation == Configuration.ORIENTATION_LANDSCAPE ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    /**
     * Gesture listener {@link Gesture} to listen video screen gestures.
     */
    private final Gesture.GestureListener listener = new Gesture.GestureListener() {

        /**
         * Double tap on video screen.
         */
        @Override
        public void onDoubleTap() {
            System.out.println("double tap");
        }

        /**
         * Single tap on video screen.
         * Show video view only, hide content views.
         */
        @Override
        public void onSingleTap() {

            if (gestureDetector.isDisable()) {

                // show unlock view
                binding.ivUnlock.setVisibility(View.VISIBLE);

                // after 1 sec hide unlock view
                RapidVideo.videoHandler().hideUnlockView();

                return;
            } else binding.ivUnlock.setVisibility(View.GONE);

            RapidVideo.videoHandler().updateContentViews();
        }

        /**
         * Swipe from left hand side to right hand side.
         *
         * @param value - negative argument, do minus ('-') so it's become positive argument
         *              and add into video current position to go forward 10 sec.
         */
        @Override
        public void onLeftToRightSwipe(int value) {
            int mSec = binding.videoView.getCurrentPosition() - value;
            RapidVideo.videoHandler().seekTo(mSec);
        }

        /**
         * Swipe from right hand side to left hand side.
         *
         * @param value - positive argument, do minus ('-') with video current position
         *              to go backward 10 sec.
         */
        @Override
        public void onRightToLeftSwipe(int value) {
            int mSec = binding.videoView.getCurrentPosition() - value;
            RapidVideo.videoHandler().seekTo(mSec);
        }

        /**
         * Swipe from top to bottom on right hand side screen.
         * Too decrease the volume.
         */
        @Override
        public void onTopToBottomSwipeRight(int value) {

            int maxVolume = RapidVideo.videoHandler().getMaxVolume();
            int percentage = RapidVideo.videoHandler().getVolumeInPercentage();

            if (percentage >= 0 && percentage <= 100) {
                int volume = (percentage * maxVolume) / 100;
                RapidVideo.videoHandler().setVolume(volume, false);
            }
        }

        /**
         * Swipe from top to bottom on left hand side screen.
         * Too decrease the brightness.
         */
        @Override
        public void onTopToBottomSwipeLeft(int value) {

            int percentage = RapidVideo.videoHandler().getBrightnessInPercentage();

            if (percentage >= 0 && percentage <= 100) {
                float brightness = UI.getAbsFloatValueForBrightness(percentage);
                RapidVideo.videoHandler().setBrightness(brightness, false);
            }
        }

        /**
         * Swipe from bottom to top on right hand side screen.
         * Too increase the volume.
         */
        @Override
        public void onBottomToTopSwipeRight(int value) {

            int maxVolume = RapidVideo.videoHandler().getMaxVolume();
            int percentage = RapidVideo.videoHandler().getVolumeInPercentage();

            if (percentage >= 0 && percentage <= 100) {
                int volume = (percentage * maxVolume) / 100;
                RapidVideo.videoHandler().setVolume(volume, true);
            }
        }

        /**
         * Swipe from bottom to top on left hand side screen.
         * Too increase the brightness.
         */
        @Override
        public void onBottomToTopSwipeLeft(int value) {

            int percentage = RapidVideo.videoHandler().getBrightnessInPercentage();

            if (percentage >= 0 && percentage <= 100) {
                float brightness = UI.getAbsFloatValueForBrightness(percentage);
                RapidVideo.videoHandler().setBrightness(brightness, true);
            }
        }
    };

    /**
     * To listen system volume when user adjust volume using phone volume button.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                RapidVideo.videoHandler().updateAudioManagerStreamVolume(true);
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                RapidVideo.videoHandler().updateAudioManagerStreamVolume(false);
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
        if (!gestureDetector.isDisable()) super.onBackPressed();
    }

}
