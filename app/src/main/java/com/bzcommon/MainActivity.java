package com.bzcommon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bzcommon.utils.BZSpUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BZSpUtils.init(getApplicationContext());
    }
}
