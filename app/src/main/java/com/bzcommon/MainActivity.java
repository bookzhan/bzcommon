package com.bzcommon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bzcommon.utils.BZFileUtils;
import com.bzcommon.utils.BZLogUtil;
import com.bzcommon.utils.BZSpUtils;


public class MainActivity extends AppCompatActivity {

    private final static String TAG = "bz_MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BZSpUtils.init(getApplicationContext());
    }

    public void start(View view) {
//        String finalPath = BZAssetsFileManager.getFinalPath(this, "model/pd_2_00_pts5.dat");
//        BZLogUtil.d(TAG, finalPath);
//
//        String finalPath2 = BZAssetsFileManager.getFinalPath(this, "lookup.png");
//        BZLogUtil.d(TAG, finalPath2);
        String readAssetsFile = BZFileUtils.readAssetsFile(this, "test.txt");
        BZLogUtil.d(TAG, "readAssetsFile=" + readAssetsFile);
    }

    public void GLImageActivity(View view) {
        startActivity(new Intent(this, GLImageActivity.class));
    }

    public void ViewDemoActivity(View view) {
        startActivity(new Intent(this, ViewDemoActivity.class));
    }

    public void FileReadWriteTestActivity(View view) {
        startActivity(new Intent(this, FileReadWriteTestActivity.class));
    }
}
