package com.bzcommon.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

/**
 * Created by bookzhan on 2023−04-16 19:09.
 * description:对公共文件的读写类封装
 */
public class BZMediaStoreUtil {
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
        String name = "IMG_" + System.currentTimeMillis() + ".png";
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            boolean hasPermission = BZPermissionUtil.requestPermissionIfNot(
                    (AppCompatActivity) context,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    BZPermissionUtil.CODE_REQ_PERMISSION
            );
            if (!hasPermission) {
                return null;
            }
            String imagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Environment.DIRECTORY_PICTURES + "/" + name;
            BZBitmapUtil.saveBitmapToFile(bitmap, imagePath);
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(new File(imagePath));
            intent.setData(uri);
            context.sendBroadcast(intent);
            return imagePath;
        }
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, name);
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            Uri insertUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            OutputStream outputStream = context.getContentResolver().openOutputStream(insertUri);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
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
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
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
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(new File(videoPath));
            intent.setData(uri);
            context.sendBroadcast(intent);
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

    /**
     * @param path 最好是内容提供者的地址, 文件地址也可
     * @return 返回一个能直接读取的文件地址
     */
    @SuppressLint("Range")
    public static String getDirectlyReadPath(Context context, String path) {
        if (null == context || TextUtils.isEmpty(path)) {
            BZLogUtil.e(TAG, "null == context || TextUtils.isEmpty(contentUri)");
            return null;
        }
        if (path.startsWith("/")) {
            File realFile = new File(path);
            if (realFile.exists() && realFile.canRead()) {
                BZLogUtil.d(TAG, "A file path is a real address that can be read directly:" + path);
                return path;
            }
            BZLogUtil.e(TAG, "The file cannot be read:" + path);
            return null;
        }
        //Android 10 api29即使有读取权限也不能访问，需要copy
        try {
            Uri uri = Uri.parse(path);
            String[] projection = {MediaStore.Files.FileColumns.DISPLAY_NAME, MediaStore.Files.FileColumns.DATA};
            Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
            cursor.moveToFirst();
            String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME));
            String realPath = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
            cursor.close();
            if (TextUtils.isEmpty(realPath)) {
                BZLogUtil.e(TAG, "TextUtils.isEmpty(realPath)");
                return null;
            }
            File realFile = new File(realPath);
            if (realFile.exists() && realFile.canRead()) {
                BZLogUtil.d(TAG, "file can directly read path=" + realPath);
                return realPath;
            }
            String[] split = fileName.split("\\.");
            String displayName = BZMD5Util.md5(path) + "." + split[split.length - 1];
            BZLogUtil.d(TAG, "displayName from query=" + displayName);
            String finalPath = context.getExternalCacheDir().getAbsolutePath() + "/" + displayName;
            File file = new File(finalPath);
            if (file.exists() && file.canRead() && file.length() > 0) {
                BZLogUtil.d(TAG, "file can’t directly read get catch file path=" + finalPath);
                return finalPath;
            }
            BZFileUtils.fileCopy(context.getContentResolver().openInputStream(uri), finalPath);
            BZLogUtil.d(TAG, "file can’t directly read copy path=" + finalPath);
            return finalPath;
        } catch (Throwable throwable) {
            BZLogUtil.e(TAG, throwable);
        }
        return null;
    }
}
