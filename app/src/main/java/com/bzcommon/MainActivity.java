package com.bzcommon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bzcommon.utils.AESUtil;
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
//        String finalPath2 = BZAssetsFileManager.getFinalPath(this, "lookup.png");
//        BZLogUtil.d(TAG, finalPath2);
        String encrypt = AESUtil.encrypt("Hello World!", "179cc14b51a22c64c81cdcc319f34e8a");
        BZLogUtil.d(TAG, "encrypt="+encrypt);
        String decrypt = AESUtil.decrypt(encrypt, "179cc14b51a22c64c81cdcc319f34e8a");
        BZLogUtil.d(TAG, "decrypt="+decrypt);
    }

    public void GLImageActivity(View view) {
        startActivity(new Intent(this, GLImageActivity.class));
    }
}
