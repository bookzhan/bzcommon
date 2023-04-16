package com.bzcommon.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;


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
     */
    public static void requestPermission(AppCompatActivity activity, String[] permissionArr, int requestCode) {
        if (permissionArr != null) {
            ActivityCompat.requestPermissions(activity, permissionArr, requestCode);
        }
    }

    public static void requestPermission(AppCompatActivity activity, String permission, int requestCode) {
        if (permission != null) {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
        }
    }

    /**
     * @return false has no permission, true has all permissions
     */
    public static boolean requestPermissionIfNot(AppCompatActivity activity, String[] permissionArr, int requestCode) {
        if (null == activity || null == permissionArr || permissionArr.length <= 0) {
            return false;
        }
        ArrayList<String> arrayList = new ArrayList<>();
        for (String permission : permissionArr) {
            if (!isPermissionGranted(activity, permission)) {
                arrayList.add(permission);
            }
        }
        if (!arrayList.isEmpty()) {
            String[] strings = new String[arrayList.size()];
            arrayList.toArray(strings);
            requestPermission(activity, strings, requestCode);
            return false;
        }
        return true;
    }

    public static boolean requestPermissionIfNot(AppCompatActivity activity, String permission, int requestCode) {
        if (permission != null && !isPermissionGranted(activity, permission)) {
            requestPermission(activity, permission, requestCode);
            return false;
        }
        return true;
    }

    /**
     * 判断是否拥有该权限
     */
    public static boolean isPermissionGranted(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
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

    /**
     * @return false has no permission, true has all permissions
     */
    public static boolean requestMediaFileReadPermission(AppCompatActivity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return BZPermissionUtil.requestPermissionIfNot(
                    activity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.READ_MEDIA_AUDIO,
                            Manifest.permission.READ_MEDIA_VIDEO,
                            Manifest.permission.READ_MEDIA_IMAGES},
                    CODE_REQ_PERMISSION
            );
        } else {
            return BZPermissionUtil.requestPermissionIfNot(
                    activity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    CODE_REQ_PERMISSION
            );
        }
    }

    /**
     * @return false has no permission, true has all permissions
     */
    public static boolean requestFileReadWritePermission(AppCompatActivity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                activity.startActivityForResult(intent, CODE_REQ_PERMISSION);
                return false;
            }
            return true;
        } else {
            return BZPermissionUtil.requestPermissionIfNot(
                    activity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    CODE_REQ_PERMISSION
            );
        }
    }

    public static boolean requestCommonPermission(AppCompatActivity activity) {
        if (null == activity) {
            return false;
        }
        ArrayList<String> permissionList = new ArrayList<>();
        boolean isExternalStorageManager = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                activity.startActivityForResult(intent, CODE_REQ_PERMISSION);
                isExternalStorageManager = false;
            }
        } else {
            if (!BZPermissionUtil.isPermissionGranted(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (!BZPermissionUtil.isPermissionGranted(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        }
        if (!BZPermissionUtil.isPermissionGranted(activity, Manifest.permission.CAMERA)) {
            permissionList.add(Manifest.permission.CAMERA);
        }
        if (!BZPermissionUtil.isPermissionGranted(activity, Manifest.permission.RECORD_AUDIO)) {
            permissionList.add(Manifest.permission.RECORD_AUDIO);
        }
        String[] permissionStrings = new String[permissionList.size()];
        permissionList.toArray(permissionStrings);
        if (permissionList.size() > 0) {
            BZPermissionUtil.requestPermission(activity, permissionStrings, BZPermissionUtil.CODE_REQ_PERMISSION);
            return false;
        } else {
            BZLogUtil.d(TAG, "Have all permissions");
        }
        return isExternalStorageManager;
    }
}
