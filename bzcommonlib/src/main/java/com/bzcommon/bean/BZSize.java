package com.bzcommon.bean;

/**
 * Created by bookzhan on 2023−01-23 16:55.
 * description:
 */
public class BZSize {
    private int mWidth;
    private int mHeight;

    public BZSize(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    public int getWidth() {
        return mWidth;
    }

    public void setWidth(int width) {
        mWidth = width;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int height) {
        mHeight = height;
    }
}
