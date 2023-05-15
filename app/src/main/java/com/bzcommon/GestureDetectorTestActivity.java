package com.bzcommon;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bzcommon.utils.BZLogUtil;
import com.bzcommon.widget.gesturedetectors.OnClickGestureDetector;

public class GestureDetectorTestActivity extends AppCompatActivity {

    private OnClickGestureDetector mOnClickGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_detector_test);
        mOnClickGestureDetector = new OnClickGestureDetector(new OnClickGestureDetector.OnClickActionListener() {
            @Override
            public void onActionDown(View view, MotionEvent event) {

            }

            @Override
            public void onClick(View view) {
                BZLogUtil.d(GestureDetectorTestActivity.this, "onClick");
            }

            @Override
            public void onActionUp(View view, MotionEvent event) {

            }
        });
        View view = findViewById(R.id.view);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mOnClickGestureDetector.onTouch(v, event);
            }
        });
    }
}