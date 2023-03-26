package com.bzcommon;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.Nullable;

import com.bzcommon.utils.BZLogUtil;

/**
 * Created by bookzhan on 2023−03-26 09:19.
 * description:
 */
public class MultiTouchView extends View {
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;
    private RotateGestureDetector mRotateGestureDetector;

    private float mLastX, mLastY;
    private float mScaleFactor = 1f;
    private float mRotationDegrees = 0f;

    public MultiTouchView(Context context) {
        this(context, null);
    }

    public MultiTouchView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultiTouchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                float dx = e2.getRawX() - mLastX;
                float dy = e2.getRawY() - mLastY;
                // 将滑动距离应用到 View 上
                setTranslationX(getTranslationX() + dx);
                setTranslationY(getTranslationY() + dy);
                BZLogUtil.d("dx=" + dx + " dy=" + dy + " distanceX=" + distanceX + " distanceY=" + distanceY);
                BZLogUtil.d("e1=" + e1.toString());
                BZLogUtil.d("e2=" + e2.toString());
                // 记录上一次的位置
                mLastX = e2.getRawX();
                mLastY = e2.getRawY();
                return true;
            }
        });
        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                BZLogUtil.d("ScaleFactor="+detector.getScaleFactor());
                mScaleFactor *= detector.getScaleFactor();
                // 限制缩放范围在0.1倍到10倍之间
                mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));
                // 缩放 View
//                setScaleX(mScaleFactor);
//                setScaleY(mScaleFactor);
                return true;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {

            }
        });
        mRotateGestureDetector = new RotateGestureDetector(context, new RotateGestureDetector.OnRotateGestureListener() {
            @Override
            public boolean onRotate(RotateGestureDetector detector) {
                mRotationDegrees -= detector.getRotationDegreesDelta();
                // 旋转 View
//                setRotation(mRotationDegrees);
                return true;
            }

            @Override
            public boolean onRotateBegin(RotateGestureDetector detector) {
                return true;
            }

            @Override
            public void onRotateEnd(RotateGestureDetector detector) {

            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        mScaleGestureDetector.onTouchEvent(event);
        mRotateGestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 记录下按下时的位置
                mLastX = (int) event.getRawX();
                mLastY = (int) event.getRawY();
                break;
        }
        return true;
    }
}
