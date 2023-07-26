package com.bzcommon.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import org.json.JSONArray;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by zhandalin on 2018-12-28 16:32.
 * 说明:缓存资产目录下的文件
 * 会自动读取assets/auto_copy_files.json 文件,配置格式为["path0","path1"]
 * 不配置会默认全部copy
 * 策略是每个版本会强制更新一次
 */
public class BZAssetsFileManager {
    private static final String TAG = "bz_AssetsFileManager";
    private static final String OUT_DIR_NAME = "assets";
    private static volatile boolean mHasInit = false;

    public static String getFinalPath(Context context, String path) {
        if (null == context || BZStringUtils.isEmpty(path)) {
            return path;
        }
        if (path.startsWith("/")) {
            return path;
        }
        //处理资产目录下的文件
        try {
            String fileDirPath = context.getFilesDir().getAbsolutePath();
            String finalPath = fileDirPath + "/" + OUT_DIR_NAME + "/" + path;
            if (BZFileUtils.fileIsEnable(finalPath)) {
                return finalPath;
            }
            BZLogUtil.d(TAG, "getFinalPath copyFile path=" + path);
            copyFile(context.getAssets(), path, finalPath);
            return finalPath;
        } catch (Throwable e) {
            BZLogUtil.e(TAG, e);
        }
        return path;
    }

    public static void init(@NonNull Context context, int currentVersionCode) {
        if (mHasInit) {
            return;
        }
        mHasInit = true;
        new Thread(() -> {
            copyAssetsFileImp(context, currentVersionCode);
        }).start();
    }

    private static void copyAssetsFileImp(@NonNull Context context, int currentVersionCode) {
        BZSpUtils.init(context);
        final String key = "lastCopyAssetsFileVersionCode";
        int lastCopyAssetsFileVersionCode = BZSpUtils.getInt(key, 0);
        boolean forceUpdate = currentVersionCode > lastCopyAssetsFileVersionCode;
        AssetManager assetManager = context.getAssets();
        String json = BZFileUtils.readAssetsFile(context, "auto_copy_files.json");
        String outPath = context.getFilesDir().getAbsolutePath() + "/" + OUT_DIR_NAME;
        if (TextUtils.isEmpty(json)) {
            copyAssets(assetManager, "", outPath, forceUpdate);
        } else {
            try {
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    String path = jsonArray.getString(i);
                    copyAssets(assetManager, path, outPath, forceUpdate);
                }
            } catch (Throwable e) {
                BZLogUtil.e(TAG, e);
            }
        }
        BZSpUtils.put(key, currentVersionCode);
        BZLogUtil.d(TAG, "copyAssetsFileByConfig forceUpdate=" + forceUpdate + " lastCopyAssetsFileVersionCode=" + lastCopyAssetsFileVersionCode + " currentVersionCode=" + currentVersionCode);
    }


    private static void copyAssets(AssetManager assetManager, String assetsPath, String destinationPath, boolean forceUpdate) {
        try {
            if (!isDirectory(assetManager, assetsPath)) {
                String newDestinationPath = destinationPath + File.separator + assetsPath;
                if (!BZFileUtils.fileIsEnable(newDestinationPath) || forceUpdate) {
                    BZLogUtil.d(TAG, "copyFile sourcePath=" + assetsPath + " destinationPath=" + newDestinationPath);
                    copyFile(assetManager, assetsPath, newDestinationPath);
                }
                return;
            }
            String[] assetsList = assetManager.list(assetsPath);
            if (assetsList != null && assetsList.length > 0) {
                for (String asset : assetsList) {
                    String sourcePath = asset;
                    if (!TextUtils.isEmpty(assetsPath)) {
                        sourcePath = assetsPath + File.separator + asset;
                    }
                    String newDestinationPath = destinationPath + File.separator + sourcePath;
                    if (isDirectory(assetManager, sourcePath)) {
                        // 递归复制子目录的文件
                        copyAssets(assetManager, sourcePath, newDestinationPath, forceUpdate);
                    } else {
                        if (!BZFileUtils.fileIsEnable(newDestinationPath) || forceUpdate) {
                            // 复制文件
                            BZLogUtil.d(TAG, "copyFile sourcePath=" + sourcePath + " destinationPath=" + newDestinationPath);
                            copyFile(assetManager, sourcePath, newDestinationPath);
                        }
                    }
                }
            }
        } catch (Throwable e) {
            BZLogUtil.e(TAG, e);
        }
    }

    private static boolean isDirectory(AssetManager assetManager, String path) {
        try {
            String[] assetsList = assetManager.list(path);
            return assetsList != null && assetsList.length > 0;
        } catch (Throwable e) {
            BZLogUtil.e(TAG, e);
        }
        return false;
    }

    private static synchronized void copyFile(AssetManager assetManager, String sourcePath, String destinationPath) {
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            BZFileUtils.createNewFile(destinationPath);
            inputStream = assetManager.open(sourcePath);
            outputStream = new FileOutputStream(destinationPath);
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
        } catch (Throwable e) {
            BZLogUtil.e(TAG, e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Throwable e) {
                BZLogUtil.e(TAG, e);
            }
        }
    }
}
