package com.bzcommon;

import android.content.Context;
import android.view.MotionEvent;

/**
 * Created by bookzhan on 2023−03-26 09:19.
 * description:
 */
public class RotateGestureDetector {
    private OnRotateGestureListener mListener;
    private float mRotationDegrees;
    private float mFocusX;
    private float mFocusY;
    private float mLastAngle;

    public RotateGestureDetector(Context context, OnRotateGestureListener listener) {
        mListener = listener;
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                // 重置状态
                mRotationDegrees = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() != 2) {
                    break;
                }
                // 计算旋转角度
                float dx = event.getX(0) - event.getX(1);
                float dy = event.getY(0) - event.getY(1);
                float angle = (float) Math.toDegrees(Math.atan2(dy, dx));
                mRotationDegrees = angle - mLastAngle;
                mLastAngle = angle;
                // 计算旋转中心点
                mFocusX = (event.getX(0) + event.getX(1)) / 2;
                mFocusY = (event.getY(0) + event.getY(1)) / 2;
                // 回调监听器
                mListener.onRotate(this);
                break;
        }
        return true;
    }

    public float getRotationDegreesDelta() {
        return mLastAngle;
    }

    public interface OnRotateGestureListener {
        boolean onRotate(RotateGestureDetector detector);
        boolean onRotateBegin(RotateGestureDetector detector);
        void onRotateEnd(RotateGestureDetector detector);
    }
}
