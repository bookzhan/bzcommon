package com.bzcommon.widget.gesturedetectors;

import android.annotation.SuppressLint;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by bookzhan on 2023−03-26 11:34.
 * description:
 */
public class ViewGestureHelper implements View.OnTouchListener {
    private enum Mode {
        NONE, DRAG, ZOOM
    }

    private Mode mMode = Mode.NONE;
    private final PointF mStart = new PointF();
    private final PointF mMid = new PointF();
    private float mOldDist = 1f;
    private float mOldRotation = 0f;
    private final Matrix mMatrix = new Matrix();
    private final Matrix mSavedMatrix = new Matrix();
    private final float[] mValues = new float[9];
    private final int[] mLocation = new int[2];
    private float mMinScale = 1;
    private float mMaxScale = 10;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mSavedMatrix.set(mMatrix);
                mStart.set(event.getRawX(), event.getRawY());
                mMode = Mode.DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mOldDist = getDistance(view, event);
                mOldRotation = getRotation(event);
                mSavedMatrix.set(mMatrix);
                getMidPoint(view, mMid, event);
                mMode = Mode.ZOOM;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mMode == Mode.DRAG) {
                    mMatrix.set(mSavedMatrix);
                    mMatrix.postTranslate(event.getRawX() - mStart.x, event.getRawY() - mStart.y);
                } else if (mMode == Mode.ZOOM) {
                    float newDist = getDistance(view, event);
                    float scale = newDist / mOldDist;
                    getMidPoint(view, mMid, event);
                    mMatrix.set(mSavedMatrix);
                    mMatrix.postScale(scale, scale, mMid.x, mMid.y);
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                mMode = Mode.NONE;
                break;
        }
        setMatrix(view, mMatrix);
        return true;
    }

    private float getDistance(View view, MotionEvent event) {
        view.getLocationOnScreen(mLocation);
        float pointer0RawX = event.getX(0) * view.getScaleX() + mLocation[0];
        float pointer0RawY = event.getY(0) * view.getScaleY() + mLocation[1];
        float pointer1RawX = event.getX(1) * view.getScaleX() + mLocation[0];
        float pointer1RawY = event.getY(1) * view.getScaleY() + mLocation[1];

        float x = pointer0RawX - pointer1RawX;
        float y = pointer0RawY - pointer1RawY;
        return (float) Math.sqrt(x * x + y * y);
    }

    private void getMidPoint(View view, PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1) + view.getX();
        float y = event.getY(0) + event.getY(1) + view.getY();
        point.set(x / 2, y / 2);
    }

    private float getRotation(MotionEvent event) {
        double deltaX = event.getX(1) - event.getX(0);
        double deltaY = event.getY(1) - event.getY(0);
        double radians = Math.atan2(deltaY, deltaX);
        return (float) Math.toDegrees(radians);
    }

    private void setMatrix(View view, Matrix matrix) {
        matrix.getValues(mValues);
        float scaleX = mValues[Matrix.MSCALE_X];
        float transX = mValues[Matrix.MTRANS_X];
        float scaleY = mValues[Matrix.MSCALE_Y];
        float transY = mValues[Matrix.MTRANS_Y];

        view.setScaleX(Math.min(Math.max(mMinScale, scaleX), mMaxScale));
        view.setScaleY(Math.min(Math.max(mMinScale, scaleY), mMaxScale));
        view.setTranslationX(transX);
        view.setTranslationY(transY);
//        float angle = (float) Math.atan2(values[Matrix.MSKEW_Y], values[Matrix.MSCALE_Y]) * (180 / (float) Math.PI)*0.5f;
//        setRotation(angle % 360);
    }

    public float getMinScale() {
        return mMinScale;
    }

    public void setMinScale(float minScale) {
        this.mMinScale = minScale;
    }

    public float getMaxScale() {
        return mMaxScale;
    }

    public void setMaxScale(float maxScale) {
        this.mMaxScale = maxScale;
    }
}
