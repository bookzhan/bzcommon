package com.bzcommon.utils;

/**
 * Created by bookzhan on 2023−01-25 17:49.
 * description:
 */
public class BZCommonUtil {
    public static boolean floatIsEqual(float a, float b) {
        return Math.abs(a - b) < 0.00001;
    }
}
