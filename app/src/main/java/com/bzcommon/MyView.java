package com.bzcommon;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.view.MotionEventCompat;

/**
 * Created by bookzhan on 2023−03-26 19:30.
 * description:
 */
public class MyView extends View {

    private float mLastTouchX, mLastTouchY;
    private int mActivePointerId;

    private float mInitialDistance;
    private float mInitialScaleX, mInitialScaleY;

    private float mInitialAngle, mRotationAngle;

    public MyView(Context context) {
        super(context);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final float x = ev.getRawX();
                final float y = ev.getRawY();

                // Remember where we started (for dragging)
                mLastTouchX = x;
                mLastTouchY = y;
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                final float x = ev.getRawX(pointerIndex);
                final float y = ev.getRawY(pointerIndex);

                // Calculate the distance moved
                final float dx = x - mLastTouchX;
                final float dy = y - mLastTouchY;

                // Move the view
                setTranslationX(getTranslationX() + dx);
                setTranslationY(getTranslationY() + dy);

                mLastTouchX = x;
                mLastTouchY = y;
                break;
            }

            case MotionEvent.ACTION_UP: {
                mActivePointerId = MotionEvent.INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_CANCEL:
                mActivePointerId = MotionEvent.INVALID_POINTER_ID;
                break;


            case MotionEvent.ACTION_POINTER_UP: {
                final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);

                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = MotionEventCompat.getX(ev, newPointerIndex);
                    mLastTouchY = MotionEventCompat.getY(ev, newPointerIndex);
                    mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
                }
                break;
            }

            case MotionEvent.ACTION_POINTER_DOWN: {
                mInitialDistance = getDistance(ev);
                mInitialScaleX = getScaleX();
                mInitialScaleY = getScaleY();

                mInitialAngle = getAngle(ev);
                mRotationAngle = getRotation();

                break;
            }

            case MotionEvent.ACTION_MOVE | MotionEvent.ACTION_POINTER_DOWN: {
                final float distance = getDistance(ev);
                final float scaleFactor = distance / mInitialDistance;

                final float angle = getAngle(ev);
                final float rotation = mRotationAngle + angle - mInitialAngle;

                setScaleX(mInitialScaleX * scaleFactor);
                setScaleY(mInitialScaleY * scaleFactor);
                setRotation(rotation);

                break;
            }
        }

        return true;
    }

    private float getDistance(MotionEvent ev) {
        final float dx = MotionEventCompat.getX(ev, 1) - MotionEventCompat.getX(ev, 0);
        final float dy = MotionEventCompat.getY(ev, 1) - MotionEventCompat.getY(ev, 0);
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    private float getAngle(MotionEvent ev) {
        final float dx = MotionEventCompat.getX(ev, 1) - MotionEventCompat.getX(ev, 0);
        final float dy = MotionEventCompat.getY(ev, 1) - MotionEventCompat.getY(ev, 0);
        return (float) Math.toDegrees(Math.atan2(dy, dx));
    }
}