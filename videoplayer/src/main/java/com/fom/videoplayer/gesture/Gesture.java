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

        void onTopToBottomSwipeRight(int value);

        void onTopToBottomSwipeLeft(int value);

        void onBottomToTopSwipeRight(int value);

        void onBottomToTopSwipeLeft(int value);
    }

    /**
     * Detector constructor.
     */
    public static Detector<Object> detector() {
        return new Detector<>();
    }

    /**
     * A class to detect touch movement.
     */
    public static class Detector<detector> extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener {

        private GestureListener listener;
        final int minD = 50;
        float dx, dy, mx, my, nx, ny;
        private long millis;
        private boolean isDoubleTapConfirmed;
        private boolean isMoving;
        private boolean isHorizontalSwipe;
        private boolean isVerticalSwipe;
        private boolean isDisable;

        public Detector<detector> view(View view) {
            view.setOnTouchListener(this);
            return this;
        }

        public void listen(GestureListener listener) {
            this.listener = listener;
        }

        public void setDisable(boolean isDisable) {
            this.isDisable = isDisable;
        }

        public boolean isDisable() {
            return isDisable;
        }

        private void onDoubleTap() {
            if (listener != null) listener.onDoubleTap();
        }

        private void onSingleTap() {
            if (listener != null) listener.onSingleTap();
        }

        private void onLeftToRightSwipe(int value) {
            if (listener != null) listener.onLeftToRightSwipe(value);
        }

        private void onRightToLeftSwipe(int value) {
            if (listener != null) listener.onRightToLeftSwipe(value);
        }

        private void onTopToBottomSwipeRight(int value) {
            if (listener != null) listener.onTopToBottomSwipeRight(value);
        }

        private void onTopToBottomSwipeLeft(int value) {
            if (listener != null) listener.onTopToBottomSwipeLeft(value);
        }

        private void onBottomToTopSwipeRight(int value) {
            if (listener != null) listener.onBottomToTopSwipeRight(value);
        }

        private void onBottomToTopSwipeLeft(int value) {
            if (listener != null) listener.onBottomToTopSwipeLeft(value);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction() & MotionEvent.ACTION_MASK) {

                case MotionEvent.ACTION_DOWN:

                    if (isDisable) break; // if gesture disable return

                    if (isDoubleTap(millis)) {
                        isDoubleTapConfirmed = true;
                    }

                    millis = System.currentTimeMillis();

                    // get x and y position on touch
                    dx = event.getX();
                    dy = event.getY();

                    break;

                case MotionEvent.ACTION_MOVE:

                    if (isDisable) break; // if gesture disable return

                    //get x and y position on move
                    mx = event.getX();
                    my = event.getY();

                    float swipeX = dx - mx;
                    float swipeY = dy - my;

                    if (Math.abs(swipeX) > Math.abs(swipeY)) { //horizontal swipe

                        // if vertical swipe is active avoid horizontal swipe
                        if (isVerticalSwipe) break;

                        isMoving = Math.abs(swipeX) > minD; // if swipe value more than default min distance

                        if (isMoving) { // if horizontal swipe conditions valid

                            isHorizontalSwipe = true;

                            // break when current position (swipeX) is same as previous position (nx),
                            // in simple terms user stop moving but still keep in touch
                            if (nx == swipeX) break;

                            nx = swipeX;  // store current position as new X value

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

                        // if horizontal swipe is active avoid vertical swipe
                        if (isHorizontalSwipe) break;

                        isMoving = Math.abs(swipeY) > 0; // if swipe value more than 0

                        if (isMoving) { // if vertical swipe conditions valid

                            isVerticalSwipe = true; // set vertical swipe enable

                            // break when current position (swipeY) is same as previous position (ny),
                            // in simple terms user stop moving but still keep in touch
                            if (ny == swipeY) break;

                            ny = swipeY; // store current position as new Y value

                            // get device width
                            float width = v.getContext().getResources().getDisplayMetrics().widthPixels;

                            if (swipeY < 0) { // top to bottom swipe

                                // if dx value is more than half of total screen width it means
                                // user touch right hand side of the screen, vice-versa left hand side
                                // if dx less than total screen width
                                if (dx > (width / 2))
                                    onTopToBottomSwipeRight((int) swipeY);
                                else
                                    onTopToBottomSwipeLeft((int) swipeY);
                                return true;
                            }
                            if (swipeY > 0) { //bottom to top swipe

                                if (dx > (width / 2))
                                    onBottomToTopSwipeRight((int) swipeY);
                                else
                                    onBottomToTopSwipeLeft((int) swipeY);
                                return true;
                            }

                        } else {
                            return false;
                        }
                    }
                    break;

                case MotionEvent.ACTION_UP:

                    // if gesture is disable and user tap on screen
                    if (isDisable) {

                        onSingleTap(); // detect single tap to unlock screen

                        break;
                    }

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
