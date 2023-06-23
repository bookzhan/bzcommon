package com.bzcommon.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import java.util.List;

/**
 * Created by bookzhan on 2023−01-25 17:49.
 * description:
 */
public class BZCommonUtil {
    public static boolean floatIsEqual(float a, float b) {
        return floatIsEqual(a, b, 0.00001f);
    }

    public static boolean floatIsEqual(float a, float b, float precision) {
        return Math.abs(a - b) < precision;
    }

    /**
     * @return apk签名信息, 并MD5
     */
    public static String getSignatureInfo(Context context) {
        try {
            @SuppressLint("PackageManagerGetSignatures")
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature[] signatures = packageInfo.signatures;
            return BZMD5Util.md5(signatures[0].toCharsString());
        } catch (Exception e) {
            BZLogUtil.e(e);
        }
        return null;
    }

    public static boolean isMainProcess(Context context) {
        if (null == context) {
            return false;
        }
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = activityManager.getRunningAppProcesses();
        String packageName = context.getPackageName();
        boolean isMainProcess = false;
        for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
            if (processInfo.processName.equals(packageName) && processInfo.pid == android.os.Process.myPid()) {
                isMainProcess = true;
                break;
            }
        }
        return isMainProcess;
    }
}
