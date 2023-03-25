package com.bzcommon;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bzcommon.widget.gesturedetectors.MultiTouchListener;

public class ViewDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_demo);
        View image_view = findViewById(R.id.image_view);
        MultiTouchListener multiTouchListener = new MultiTouchListener();
        multiTouchListener.isRotateEnabled = false;
        multiTouchListener.minimumScale = 0.5f;
        multiTouchListener.maximumScale = 100f;
        image_view.setOnTouchListener(multiTouchListener);
    }
}