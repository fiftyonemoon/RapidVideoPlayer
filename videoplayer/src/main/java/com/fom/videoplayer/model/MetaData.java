package com.fom.videoplayer.model;

public class MetaData {

    private String name;
    private String uri;
    private String resolution;
    private long size;
    private long duration;
    private int width;
    private int height;

    public MetaData() {
    }

    public MetaData(String name, String uri, int width, int height, String resolution, long size, long duration) {
        this.name = name;
        this.uri = uri;
        this.width = width;
        this.height = height;
        this.resolution = resolution;
        this.size = size;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }
}
