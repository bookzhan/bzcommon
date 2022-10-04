package com.bzcommon;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bzcommon.utils.BZBitmapUtil;
import com.bzcommon.utils.BZLogUtil;
import com.bzcommon.utils.BZPermissionUtil;
import com.bzcommon.utils.BZSpUtils;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private final static String TAG = "bz_MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BZSpUtils.init(getApplicationContext());
        requestPermission();
    }

    public void start(View view) {
//        String finalPath = BZAssetsFileManager.getFinalPath(this, "model/pd_2_00_pts5.dat");
//        BZLogUtil.d(TAG, finalPath);
//
//        String json = BZFileUtils.readTextFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/CameraCalibrationInfo/camera_calibration_info.json");
//        BZLogUtil.d(TAG, json);
//
//        String assetsFile = BZFileUtils.readAssetsFile(this, "test.txt");
//        BZLogUtil.d(TAG, assetsFile);
//        boolean h265DecoderSupport = BZDeviceUtils.isH265DecoderSupport();
//        BZLogUtil.d(TAG, "h265DecoderSupport=" + h265DecoderSupport);
        Bitmap bitmap = BZBitmapUtil.loadBitmap(this, Environment.getExternalStorageDirectory().getAbsolutePath() + "/bzmedia/test_11.jpg");
        BZLogUtil.d(TAG, "bitmap=" + bitmap);

//        try {
//            ExifInterface exifInterface = new ExifInterface(Environment.getExternalStorageDirectory().getAbsolutePath() + "/bzmedia/test_11.jpg");
//            exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, "6");
//            exifInterface.saveAttributes();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    private boolean requestPermission() {
        ArrayList<String> permissionList = new ArrayList<>();
        if (!BZPermissionUtil.isPermissionGranted(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!BZPermissionUtil.isPermissionGranted(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!BZPermissionUtil.isPermissionGranted(this, Manifest.permission.CAMERA)) {
            permissionList.add(Manifest.permission.CAMERA);
        }
        if (!BZPermissionUtil.isPermissionGranted(this, Manifest.permission.RECORD_AUDIO)) {
            permissionList.add(Manifest.permission.RECORD_AUDIO);
        }

        String[] permissionStrings = new String[permissionList.size()];
        permissionList.toArray(permissionStrings);

        if (permissionList.size() > 0) {
            BZPermissionUtil.requestPermission(this, permissionStrings, BZPermissionUtil.CODE_REQ_PERMISSION);
            return false;
        } else {
            BZLogUtil.d(TAG, "Have all permissions");
            return true;
        }
    }

    public void GLImageActivity(View view) {
        startActivity(new Intent(this, GLImageActivity.class));
    }
}
