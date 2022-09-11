package com.bzcommon.utils;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


/**
 * Created by admin on 2016/2/17.
 * 动态权限请求的相关工具类
 */
public class BZPermissionUtil {
    private static final String TAG = "PermissionUtil";
    public static final int CODE_REQ_PERMISSION = 1100;//权限请求
    public static final int CODE_REQ_AUDIO_PERMISSION = 601;
    public static final int CODE_REQ_CAMERA_PERMISSION = 602;

    /**
     * 权限请求
     *
     * @param activity
     * @return
     */
    public static void requestPermission(AppCompatActivity activity, String[] permissionArr, int requestCode) {
        if (permissionArr != null) {
            ActivityCompat.requestPermissions(activity, permissionArr, requestCode);
        }

    }

    public static void requestPermission(AppCompatActivity activity, String permissionArr, int requestCode) {
        if (permissionArr != null) {
            ActivityCompat.requestPermissions(activity, new String[]{permissionArr}, requestCode);
        }
    }

    public static void requestPermissionIFNot(AppCompatActivity activity, String permissionArr, int requestCode) {
        if (permissionArr != null && !isPermissionGranted(activity, permissionArr)) {
            requestPermission(activity, permissionArr, requestCode);
        }
    }

    /**
     * 判断是否拥有该权限
     */
    public static boolean isPermissionGranted(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断是否拥有全部权限
     */
    public static boolean isPermissionGranted(Context context, String[] permissions) {
        if (null == context || null == permissions || permissions.length <= 0) {
            return false;
        }
        boolean permissionGranted = true;
        for (String permission : permissions) {
            permissionGranted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
            if (!permissionGranted) {
                break;
            }
        }
        return permissionGranted;
    }
}
