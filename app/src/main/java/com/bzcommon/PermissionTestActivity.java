package com.bzcommon;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bzcommon.utils.BZLogUtil;
import com.bzcommon.utils.BZPermissionUtil;

import java.io.File;

public class PermissionTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_test);
    }

    public void requestMediaFileReadPermission(View view) {
        BZPermissionUtil.requestMediaFileReadPermission(this);
    }

    public void FileReadTest(View view) {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/bzmedia/out.mp4");
        BZLogUtil.d(this, "file exists=" + file.exists() + " canRead=" + file.canRead() + " canWrite=" + file.canWrite() + " length=" + file.length());
    }
}