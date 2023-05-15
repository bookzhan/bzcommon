package com.bzcommon.widget.gesturedetectors;

import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import com.bzcommon.utils.BZLogUtil;

/**
 * Created by bookzhan on 2023−05-15 21:47.
 * description:
 */
public class OnClickGestureDetector {
    private int mMaxClickDistance = 50;
    private float mDownX, mDownY;
    private float mViewDownX, mViewDownY;
    private boolean mExceededMaximumDistance = false;
    private boolean mMultiTouch = false;
    private final @NonNull OnClickActionListener mOnClickActionListener;

    public OnClickGestureDetector(@NonNull OnClickActionListener onClickActionListener) {
        mOnClickActionListener = onClickActionListener;
    }

    public void setMaxClickDistance(int maxClickDistance) {
        mMaxClickDistance = maxClickDistance;
    }

    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                mViewDownX = view.getX();
                mViewDownY = view.getY();
                mExceededMaximumDistance = false;
                mMultiTouch = false;
                mOnClickActionListener.onActionDown(view, event);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mMultiTouch = true;
                break;
            case MotionEvent.ACTION_MOVE:
                float touchDistance = distance(mDownX, mDownY, event.getX(), event.getY());
                float viewDistance = distance(mViewDownX, mViewDownY, view.getX(), view.getY());
                BZLogUtil.d(this, "touchDistance=" + touchDistance + " viewDistance=" + viewDistance);
                if (touchDistance > mMaxClickDistance || viewDistance > mMaxClickDistance) {
                    mExceededMaximumDistance = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!mMultiTouch && !mExceededMaximumDistance) {
                    mOnClickActionListener.onClick(view);
                }
                mOnClickActionListener.onActionUp(view, event);
                break;
        }
        return true;
    }

    private static float distance(float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public interface OnClickActionListener {
        void onActionDown(View view, MotionEvent event);

        void onClick(View view);

        void onActionUp(View view, MotionEvent event);
    }
}
