package com.fom.videoplayer.enums;

public enum AspectRatio {

    sixteen_nine("16:9"),
    nine_sixteen("9:16"),
    one_one("1:1");

    String ratio;

    AspectRatio(String ratio) {
        this.ratio = ratio;
    }

    public String getRatio() {
        return ratio;
    }
}
