package com.bzcommon;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bzcommon.utils.BZAssetsFileManager;
import com.bzcommon.utils.BZSpUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BZSpUtils.init(getApplicationContext());
    }

    public void start(View view) {
        BZAssetsFileManager.getFinalPath(this, "model/pd_2_00_pts5.dat");
    }
}
