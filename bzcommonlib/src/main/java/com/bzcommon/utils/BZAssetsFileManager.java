package com.bzcommon.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zhandalin on 2018-12-28 16:32.
 * 说明:缓存资产目录下的文件
 */
public class BZAssetsFileManager {
    private static final String TAG = "bz_AssetsFileManager";
    private static final String OUT_DIR_NAME = "assets";

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
            if (new File(finalPath).exists()) {
                return finalPath;
            }
            BZFileUtils.createNewFile(finalPath);

            BZFileUtils.fileCopy(context.getAssets().open(path), finalPath);
            return finalPath;
        } catch (Throwable e) {
            BZLogUtil.e(TAG, e);
        }
        return path;
    }

    public static void copyAllFile(@NonNull Context context) {
        Context applicationContext = context.getApplicationContext();
        new Thread(() -> {
            try {
                long currentTimeMillis = System.currentTimeMillis();
                copyAssets(applicationContext, "", applicationContext.getFilesDir().getAbsolutePath() + "/" + OUT_DIR_NAME);
                BZLogUtil.d(TAG, "copyAllFile finish cost time=" + (System.currentTimeMillis() - currentTimeMillis));
            } catch (Throwable e) {
                BZLogUtil.e(TAG, e);
            }
        }).start();
    }

    public static void copyAssets(Context context, String assetsPath, String destinationPath) {
        AssetManager assetManager = context.getAssets();
        try {
            String[] assetsList = assetManager.list(assetsPath);
            if (assetsList != null && assetsList.length > 0) {
                for (String asset : assetsList) {
                    String sourcePath = asset;
                    if (!TextUtils.isEmpty(assetsPath)) {
                        sourcePath = assetsPath + File.separator + asset;
                    }
                    String newDestinationPath = destinationPath + File.separator + asset;
                    if (isDirectory(assetManager, sourcePath)) {
                        // 递归复制子目录的文件
                        copyAssets(context, sourcePath, newDestinationPath);
                    } else {
                        // 复制文件
                        BZLogUtil.d(TAG, "copyFile sourcePath=" + sourcePath);
                        BZLogUtil.d(TAG, "copyFile destinationPath=" + newDestinationPath);
                        copyFile(assetManager, sourcePath, newDestinationPath);
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

    private static void copyFile(AssetManager assetManager, String sourcePath, String destinationPath) {
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
