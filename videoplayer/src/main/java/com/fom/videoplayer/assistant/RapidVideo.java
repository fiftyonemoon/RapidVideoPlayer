package com.fom.videoplayer.assistant;

public class RapidVideo {

    public static VideoBuilder<Object> videoBuilder() {
        return new VideoBuilder<>();
    }

    public static VideoHandler<Object> videoHandler() {
        return VideoHandler.videoHandler();
    }
}
