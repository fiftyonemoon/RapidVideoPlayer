package com.fom.videoplayer;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.fom.videoplayer.databinding.ActivityVideoPlayerBinding;
import com.fom.videoplayer.gesture.Gesture;
import com.fom.videoplayer.ui.UI;

public class VideoPlayer extends AppCompatActivity implements MediaPlayer.OnPreparedListener {

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

    private void initVideoView() {
        String path = "android.resource://" + getPackageName() + "/" + R.raw.sample;
        binding.videoView.setVideoURI(Uri.parse(path));
        binding.videoView.setOnPreparedListener(this);
    }

    private void initializeGesture() {
        Gesture.build().set(binding.gesturePanel).listen(listener);
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
            binding.videoView.pause();
            handler.removeCallbacks(runnable);
        } else {
            binding.videoView.start();
            handler.postDelayed(runnable, 100);
        }

        updatePlayPauseButton();
    }

    private void onNextClick(View view) {
    }

    private void onRatioClick(View view) {
    }

    private final Gesture.Builder.GestureListener listener = new Gesture.Builder.GestureListener() {

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
            System.out.println("left > right:" + value);
        }

        @Override
        public void onRightToLeftSwipe(int value) {
            System.out.println("right > left:" + value);
        }

        @Override
        public void onTopToBottomSwipe(int value) {
            System.out.println("top > bottom:" + value);
        }

        @Override
        public void onBottomToTopSwipe(int value) {
            System.out.println("bottom > top:" + value);
        }
    };

    @Override
    public void onPrepared(MediaPlayer mp) {
        binding.progressBar.setMax(binding.videoView.getDuration());
        updateDuration();
        startVideoPlayer();
    }

    private void updatePlayPauseButton() {
        binding.ivPlayPause.setImageResource(binding.videoView.isPlaying() ? R.drawable.ic_pause : R.drawable.ic_play);
    }

    private void updateDuration() {
        binding.tvDuration.setText(UI.getTimeInMinSecFormat(binding.videoView.getDuration()));
        binding.tvCount.setText(UI.getTimeInMinSecFormat(binding.videoView.getCurrentPosition()));
    }

    private void startVideoPlayer() {
        if (!binding.videoView.isPlaying()) {
            binding.videoView.start();
            handler.postDelayed(runnable, 100);
            updatePlayPauseButton();
        }
    }

    private void stopVideoPlayer() {
        if (binding.videoView.isPlaying()) {
            binding.videoView.pause();
            binding.videoView.stopPlayback();
            binding.videoView.seekTo(0);
            binding.progressBar.setProgress(0);
        }
        updatePlayPauseButton();
    }

    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (binding.videoView.getCurrentPosition() <= binding.videoView.getDuration()) {
                binding.progressBar.setProgress(binding.videoView.getCurrentPosition());
                handler.postDelayed(runnable, 100);
                updateDuration();
            } else {
                stopVideoPlayer();
            }
        }
    };

    @Override
    protected void onStop() {
        stopVideoPlayer();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
