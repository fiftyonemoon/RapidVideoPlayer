package com.fom.videoplayer.gesture;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created on 16th June 2021.
 * A class to detect video player gesture.
 *
 * @author hardkgosai.
 * @since 1.0.0
 */
public class Gesture {

    /**
     * Gesture listeners.
     */
    public interface GestureListener {
        void onDoubleTap();

        void onSingleTap();

        void onLeftToRightSwipe(int value);

        void onRightToLeftSwipe(int value);

        void onTopToBottomSwipe(int value);

        void onBottomToTopSwipe(int value);
    }

    /**
     * Detector constructor.
     */
    public static Detector<Object> detect() {
        return new Detector<>();
    }

    /**
     * A class to detect touch movement.
     */
    public static class Detector<detector> extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener {

        private GestureListener listener;
        final int minD = 50;
        float dx, dy, ux, uy;
        private long millis;
        private boolean isDoubleTapConfirmed;
        private boolean isMoving;
        private boolean isHorizontalSwipe;
        private boolean isVerticalSwipe;

        public Detector<detector> view(View view) {
            view.setOnTouchListener(this);
            return this;
        }

        public void listen(GestureListener listener) {
            this.listener = listener;
        }

        public void onDoubleTap() {
            if (listener != null) listener.onDoubleTap();
        }

        public void onSingleTap() {
            if (listener != null) listener.onSingleTap();
        }

        public void onLeftToRightSwipe(int value) {
            if (listener != null) listener.onLeftToRightSwipe(value);
        }

        public void onRightToLeftSwipe(int value) {
            if (listener != null) listener.onRightToLeftSwipe(value);
        }

        public void onTopToBottomSwipe(int value) {
            if (listener != null) listener.onTopToBottomSwipe(value);
        }

        public void onBottomToTopSwipe(int value) {
            if (listener != null) listener.onBottomToTopSwipe(value);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction() & MotionEvent.ACTION_MASK) {

                case MotionEvent.ACTION_DOWN:

                    if (isDoubleTap(millis)) {
                        isDoubleTapConfirmed = true;
                    }

                    millis = System.currentTimeMillis();

                    dx = event.getX();
                    dy = event.getY();

                    break;

                case MotionEvent.ACTION_MOVE:

                    ux = event.getX();
                    uy = event.getY();

                    float swipeX = dx - ux;
                    float swipeY = dy - uy;

                    //horizontal swipe
                    if (Math.abs(swipeX) > Math.abs(swipeY)) {

                        if (isVerticalSwipe) break;

                        isMoving = Math.abs(swipeX) > minD;

                        if (isMoving) {

                            isHorizontalSwipe = true;

                            if (swipeX < 0) { // left to right swipe

                                onLeftToRightSwipe((int) swipeX);
                                return true;
                            }
                            if (swipeX > 0) { // right to left swipe

                                onRightToLeftSwipe((int) swipeX);
                                return true;
                            }

                        } else {
                            return false; // return false if swipe not detect
                        }

                    } else { // vertical swipe

                        if (isHorizontalSwipe) break;

                        isMoving = Math.abs(swipeY) > minD;

                        if (isMoving) {

                            isVerticalSwipe = true;

                            if (swipeY < 0) { // top to bottom swipe

                                onTopToBottomSwipe((int) swipeY);
                                return true;
                            }
                            if (swipeY > 0) { //bottom to top swipe

                                onBottomToTopSwipe((int) swipeY);
                                return true;
                            }

                        } else {
                            return false;
                        }
                    }

                    break;

                case MotionEvent.ACTION_UP:

                    if (isMoving) {

                        isMoving = false;
                        isHorizontalSwipe = false;
                        isVerticalSwipe = false;

                        break; // if user is swiping then return
                    }

                    if (isDoubleTapConfirmed) { // double tap

                        handler.removeCallbacks(runnable); // remove handler if double tap

                        millis = 0;
                        isDoubleTapConfirmed = false;
                        onDoubleTap();

                    } else
                        handler.postDelayed(runnable, 250); // single tap

                    break;
            }
            return true;
        }

        private boolean isDoubleTap(long millis) {
            return System.currentTimeMillis() - millis < 1000;
        }

        private final Handler handler = new Handler(Looper.myLooper());
        private final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                millis = 0;
                onSingleTap();
            }
        };
    }

}
