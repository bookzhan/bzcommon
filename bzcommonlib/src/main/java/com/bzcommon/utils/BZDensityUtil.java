package com.bzcommon.utils;

import android.content.Context;

public class BZDensityUtil {
    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     */
    public static float px2dip(Context context, float pxValue) {
        if (null == context) {
            return pxValue;
        }
        float scale = context.getResources().getDisplayMetrics().density;
        if (scale <= 0) {
            scale = 1;
        }
        return pxValue / scale;
    }

    public static float dip2px(Context context, float dipValue) {
        if (null == context) {
            return dipValue;
        }
        float scale = context.getResources().getDisplayMetrics().density;
        if (scale <= 0) {
            scale = 1;
        }
        return dipValue * scale;
    }

    public static float px2sp(Context context, float pxValue) {
        if (null == context) {
            return pxValue;
        }
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        if (fontScale <= 0) {
            fontScale = 1;
        }
        return pxValue / fontScale;
    }


    public static float sp2px(Context context, float spValue) {
        if (null == context) {
            return spValue;
        }
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        if (fontScale <= 0) {
            fontScale = 1;
        }
        return spValue * fontScale;
    }

    public static int getScreenWidth(Context context) {
        if (null == context) {
            return -1;
        }
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {
        if (null == context) {
            return -1;
        }
        return context.getResources().getDisplayMetrics().heightPixels;
    }
}