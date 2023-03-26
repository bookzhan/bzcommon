package com.bzcommon;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

/**
 * Created by bookzhan on 2023−03-26 09:40.
 * description:
 */
public class GestureView extends FrameLayout {
    public GestureView(Context context) {
        this(context, null);
    }

    public GestureView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GestureView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private enum Mode {
        NONE, DRAG, ZOOM, ROTATE
    }

    private Mode mode = Mode.NONE;
    private PointF start = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f;
    private float oldRotation = 0f;
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                start.set(event.getRawX(), event.getRawY());
                mode = Mode.DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = getDistance(event);
                oldRotation = getRotation(event);
                savedMatrix.set(matrix);
                getMidPoint(mid, event);
                mode = Mode.ZOOM;
//                if (oldRotation > 10f) {
//                    mode = Mode.ROTATE;
//                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == Mode.DRAG) {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getRawX() - start.x, event.getRawY() - start.y);
                } else if (mode == Mode.ZOOM) {
                    float newDist = getDistance(event);
                    float scale = newDist / oldDist;
                    getMidPoint(mid, event);
                    float newRotation = getRotation(event);
                    float angle = newRotation - oldRotation;
                    matrix.set(savedMatrix);
                    matrix.postScale(scale, scale, mid.x, mid.y);
                    matrix.postRotate(angle, mid.x, mid.y);
                    setPivotX(mid.x);
                    setPivotY(mid.y);
                } else if (mode == Mode.ROTATE) {

                    matrix.set(savedMatrix);

                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode = Mode.NONE;
                break;
        }
        setMatrix(matrix);
        return true;
    }

    private float getDistance(MotionEvent event) {
        int[] location = new int[2];
        getLocationOnScreen(location);
        float pointer0RawX = event.getX(0) * getScaleX() + location[0];
        float pointer0RawY = event.getY(0) * getScaleY() + location[1];
        float pointer1RawX = event.getX(1) * getScaleX() + location[0];
        float pointer1RawY = event.getY(1) * getScaleY() + location[1];

        float x = pointer0RawX - pointer1RawX;
        float y = pointer0RawY - pointer1RawY;
        return (float) Math.sqrt(x * x + y * y);
    }

    private void getMidPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1) + getX();
        float y = event.getY(0) + event.getY(1) + getY();
        point.set(x / 2, y / 2);
    }

    private float getRotation(MotionEvent event) {
        double deltaX = event.getX(1) - event.getX(0);
        double deltaY = event.getY(1) - event.getY(0);
        double radians = Math.atan2(deltaY, deltaX);
        return (float) Math.toDegrees(radians);
    }

    private void setMatrix(Matrix matrix) {
        float[] values = new float[9];
        matrix.getValues(values);
        float scaleX = values[Matrix.MSCALE_X];
        float skewY = values[Matrix.MSKEW_Y];
        float transX = values[Matrix.MTRANS_X];
        float skewX = values[Matrix.MSKEW_X];
        float scaleY = values[Matrix.MSCALE_Y];
        float transY = values[Matrix.MTRANS_Y];
        float persp0 = values[Matrix.MPERSP_0];
        float persp1 = values[Matrix.MPERSP_1];
        float persp2 = values[Matrix.MPERSP_2];

//        setScaleX(scaleX);
//        setScaleY(scaleY);
        setTranslationX(transX);
        setTranslationY(transY);
        // Set rotation
        float angle = (float) Math.atan2(values[Matrix.MSKEW_Y], values[Matrix.MSCALE_Y]) * (180 / (float) Math.PI);
        setRotation(angle % 360);
    }
}
