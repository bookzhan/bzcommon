package com.bzcommon.utils;

import android.content.Context;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Build;
import android.view.WindowManager;

import com.bzcommon.glutils.BZOpenGlUtils;

/**
 * 系统版本信息类
 */
public class BZDeviceUtils {

    private static Point mScreenSize = null;

    /**
     * 获得设备的固件版本号
     */
    public static String getReleaseVersion() {
        return BZStringUtils.makeSafe(Build.VERSION.RELEASE);
    }

    /**
     * 获得设备型号
     */
    public static String getDeviceModel() {
        return BZStringUtils.trim(Build.MODEL);
    }

    /**
     * 获取厂商信息
     */
    public static String getManufacturer() {
        return BZStringUtils.trim(Build.MANUFACTURER);
    }

    /**
     * 判断是否是平板电脑
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * 判断是否支持闪光灯
     */
    public static boolean isSupportCameraLedFlash(PackageManager pm) {
        if (pm != null) {
            FeatureInfo[] features = pm.getSystemAvailableFeatures();
            if (features != null) {
                for (FeatureInfo f : features) {
                    if (f != null && PackageManager.FEATURE_CAMERA_FLASH.equals(f.name)) //判断设备是否支持闪光灯
                        return true;
                }
            }
        }
        return false;
    }

    /**
     * 检测设备是否支持相机
     */
    public static boolean isSupportCameraHardware(Context context) {
        if (null == context) {
            return false;
        }
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        if (null == context) {
            return -1;
        }
        return getScreenRect(context).x;
    }

    private static Point getScreenRect(Context context) {
        if (null == mScreenSize) {
            mScreenSize = new Point();
        }
        if (mScreenSize.x <= 0 || mScreenSize.y <= 0) {
            ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRealSize(mScreenSize);
        }
        return mScreenSize;
    }

    public static int getScreenHeight(Context context) {
        if (null == context) {
            return -1;
        }
        return getScreenRect(context).y;
    }

    /**
     * @return 是否支持硬解
     */
    public static boolean hardDecoderEnable() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && BZOpenGlUtils.detectOpenGLES30();
    }

    /**
     * 检测是否支持H265硬解码
     *
     * @return 检测结果
     */
    public static boolean isH265DecoderSupport() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            MediaCodecInfo[] info = new MediaCodecList(0).getCodecInfos();
            for (MediaCodecInfo codecInfo : info) {
                String name = codecInfo.getName();
                if (null == name) continue;
                name = name.toLowerCase();
                if (name.contains("decoder") && name.contains("hevc")) {
                    return true;
                }
            }
        } else {
            int count = MediaCodecList.getCodecCount();
            for (int i = 0; i < count; i++) {
                MediaCodecInfo info = MediaCodecList.getCodecInfoAt(i);
                String name = info.getName();
                if (null == name) continue;
                name = name.toLowerCase();
                if (name.contains("decoder") && name.contains("hevc")) {
                    return true;
                }
            }
        }
        return false;
    }
}
