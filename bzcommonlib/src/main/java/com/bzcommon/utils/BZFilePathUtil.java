package com.bzcommon.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by bookzhan on 2020-07-10 22:37.
 * description:
 */
public class BZFilePathUtil {
    private static String WORK_DIR_NAME = "bzmedia";

    public static void setWorkDirName(String workDirName) {
        WORK_DIR_NAME = workDirName;
    }

    public static String getWorkDir() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + WORK_DIR_NAME;
    }

    public static String getAVideoPath(Context context) {
        return context.getExternalFilesDir(Environment.DIRECTORY_MOVIES).getAbsolutePath() + "/VID_" + System.nanoTime() + ".mp4";
    }

    public static String getAAudioPath(Context context) {
        return context.getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath() + "/audio_" + System.nanoTime() + ".m4a";
    }

    public static String getAImagePath(Context context) {
        return context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/IMG_" + System.nanoTime() + ".jpg";
    }
}
