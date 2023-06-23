package com.bzcommon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bzcommon.activity.WebViewActivity;
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
//        startActivity(new Intent(this, GestureDetectorTestActivity.class));
        startActivity(new Intent(this, WebViewActivity.class));
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
