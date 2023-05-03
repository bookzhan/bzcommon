package com.bzcommon;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bzcommon.utils.BZLogUtil;
import com.bzcommon.utils.BZSpUtils;
import com.bzcommon.widget.TaskProcessingDialog;


public class MainActivity extends AppCompatActivity {

    private final static String TAG = "bz_MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BZSpUtils.init(getApplicationContext());
    }

    public void start(View view) {
        TaskProcessingDialog taskProcessingDialog = new TaskProcessingDialog(this);
        taskProcessingDialog.setProgress(0.3658f);
        taskProcessingDialog.setMassage("水波纹背景是一种常见的UI效果，可以让按钮、文本框等控件在被点击时产生水波纹效果，增强用户体验");
        taskProcessingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                BZLogUtil.d(TAG, "onCancel");
            }
        });
        taskProcessingDialog.show();
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
