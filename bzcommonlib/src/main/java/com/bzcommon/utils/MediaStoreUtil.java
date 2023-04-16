package com.bzcommon.utils;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.OutputStream;

/**
 * Created by bookzhan on 2023−04-16 19:09.
 * description:对公共文件的读写类封装
 */
public class MediaStoreUtil {
    private static final String TAG = "bz_MediaStoreUtil";

    /**
     * 新老版本均兼容
     *
     * @return file uri or path
     */
    public static String saveBitmapToSdcard(Context context, Bitmap bitmap) {
        if (!(context instanceof AppCompatActivity) || null == bitmap || bitmap.isRecycled()) {
            return null;
        }
        //无论如何低版本都需要申请权限
        //低版本直接copy,否则有兼容问题
        String name = "IMG_" + System.currentTimeMillis() + ".jpg";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            boolean hasPermission = BZPermissionUtil.requestPermissionIfNot(
                    (AppCompatActivity) context,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    BZPermissionUtil.CODE_REQ_PERMISSION
            );
            if (!hasPermission) {
                return null;
            }
            String imagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Environment.DIRECTORY_PICTURES + "/" + name;
            BZBitmapUtil.saveBitmapToSDcard(bitmap, imagePath);
            return imagePath;
        }
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, name);
            contentValues.put(MediaStore.Video.Media.MIME_TYPE, "image/jpeg");
            Uri insertUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            OutputStream outputStream = context.getContentResolver().openOutputStream(insertUri);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();
            return insertUri.toString();
        } catch (Throwable e) {
            BZLogUtil.e(TAG, e);
        }
        return null;
    }

    /**
     * @param path app私有目录
     * @return file uri or path
     */
    @SuppressWarnings("all")
    public static String saveVideoToSdcard(Context context, String path) {
        if (!(context instanceof AppCompatActivity) || null == path) {
            return null;
        }
        //无论如何低版本都需要申请权限
        //低版本直接copy,否则有兼容问题
        String name = "VID_" + System.currentTimeMillis() + ".mp4";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            boolean hasPermission = BZPermissionUtil.requestPermissionIfNot(
                    (AppCompatActivity) context,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    BZPermissionUtil.CODE_REQ_PERMISSION
            );
            if (!hasPermission) {
                return null;
            }
            String videoPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Environment.DIRECTORY_MOVIES + "/" + name;
            BZFileUtils.fileCopy(path, videoPath);
            return videoPath;
        }
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, name);
            contentValues.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
            Uri insertUri = context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
            OutputStream outputStream = context.getContentResolver().openOutputStream(insertUri);
            BZFileUtils.fileCopy(new FileInputStream(path), outputStream);
            return insertUri.toString();
        } catch (Throwable e) {
            BZLogUtil.e(TAG, e);
        }
        return null;
    }

    /**
     * 防止加载本地图片OOM
     *
     * @param path 本地地址,可以是absolutePath,也可以是Uri Content path
     */
    public static Bitmap loadBitmap(Context context, String path) {
        return BZBitmapUtil.loadBitmap(context, path);
    }
}
