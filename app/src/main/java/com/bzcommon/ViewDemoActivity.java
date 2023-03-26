package com.bzcommon;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class ViewDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_demo);
//        View image_view = findViewById(R.id.image_view);
//        MultiTouchListener multiTouchListener = new MultiTouchListener();
//        multiTouchListener.isRotateEnabled = false;
//        multiTouchListener.minimumScale = 0.5f;
//        multiTouchListener.maximumScale = 100f;
//        image_view.setOnTouchListener(multiTouchListener);
//        image_view.setOnTouchListener(new ViewGestureHelper());
    }
}