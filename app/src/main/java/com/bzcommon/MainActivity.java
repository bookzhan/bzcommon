package com.bzcommon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bzcommon.utils.BZDeviceUtils;
import com.bzcommon.utils.BZLogUtil;
import com.bzcommon.utils.BZSpUtils;


public class MainActivity extends AppCompatActivity {

    private final static String TAG = "bz_MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BZSpUtils.init(getApplicationContext());
//        BZPermissionUtil.requestCommonPermission(this);
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
//        Bitmap bitmap = BZBitmapUtil.loadBitmap(this, Environment.getExternalStorageDirectory().getAbsolutePath() + "/bzmedia/test_11.jpg");
//        BZLogUtil.d(TAG, "bitmap=" + bitmap);

//        try {
//            ExifInterface exifInterface = new ExifInterface(Environment.getExternalStorageDirectory().getAbsolutePath() + "/bzmedia/test_11.jpg");
//            exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, "6");
//            exifInterface.saveAttributes();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test_11);
//        String path = BZBitmapUtil.saveBitmapToExternalStorage(this, bitmap);

        BZLogUtil.d(TAG, "ScreenWidth=" + BZDeviceUtils.getScreenWidth(this));
        BZLogUtil.d(TAG, "ScreenHeight=" + BZDeviceUtils.getScreenHeight(this));
    }

    public void GLImageActivity(View view) {
        startActivity(new Intent(this, GLImageActivity.class));
    }
}
