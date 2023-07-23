package com.bzcommon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bzcommon.utils.BZAssetsFileManager;
import com.bzcommon.utils.BZLogUtil;
import com.bzcommon.utils.BZSpUtils;
import com.bzcommon.utils.BZToastUtil;


public class MainActivity extends AppCompatActivity {

    private final static String TAG = "bz_MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BZSpUtils.init(getApplicationContext());
        BZToastUtil.init(getApplicationContext());
    }

    public void start(View view) {
//        startActivity(new Intent(this, GestureDetectorTestActivity.class));
//        Intent intent = new Intent(this, WebViewActivity.class);
//        intent.putExtra(WebViewActivity.KEY_URL, "https://arrowmark.com/apps/collagemaker2/policy.html");
//        startActivity(intent);
//        BZToastUtil.showToast("BZToastUtil test " + System.currentTimeMillis());

        String finalPath = BZAssetsFileManager.getFinalPath(this, "model/pd_2_00_pts5.dat");
        BZLogUtil.d(TAG, "finalPath=" + finalPath);
        BZAssetsFileManager.copyAllFile(this);
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
