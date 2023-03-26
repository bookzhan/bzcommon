package com.bzcommon;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class MoveScaleRotateView extends FrameLayout {
    // 属性变量
    private float translationX; // 移动X
    private float translationY; // 移动Y
    private float scale = 1; // 伸缩比例
    private float rotation; // 旋转角度
    private float mPivotX = 0;
    private float mPivotY = 0;

    // 移动过程中临时变量
    private float actionX;
    private float actionY;
    private float spacing;
    private float degree;
    private int moveType; // 0=未选择，1=拖动，2=缩放

    public MoveScaleRotateView(Context context) {
        this(context, null);
    }

    public MoveScaleRotateView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MoveScaleRotateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClickable(true);
    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        getParent().requestDisallowInterceptTouchEvent(true);
//        return super.onInterceptTouchEvent(ev);
//    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mPivotX == 0) {
            mPivotX = w / 2;
            mPivotY = h / 2;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                moveType = 1;
                translationX = getTranslationX();
                translationY = getTranslationY();
                actionX = event.getRawX();
                actionY = event.getRawY();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                translationX = getTranslationX();
                translationY = getTranslationY();
                moveType = 2;
                spacing = getSpacing(event);
                degree = getDegree(event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (moveType == 1) {
                    translationX = translationX + event.getRawX() - actionX;
                    translationY = translationY + event.getRawY() - actionY;
                    setTranslationX(translationX);
                    setTranslationY(translationY);
                    actionX = event.getRawX();
                    actionY = event.getRawY();
                } else if (moveType == 2) {
                    translationX = translationX + event.getRawX() - actionX;
                    translationY = translationY + event.getRawY() - actionY;
                    setPivotX((event.getX(0) + event.getX(1)) / 2);
                    setPivotY((event.getY(0) + event.getY(1)) / 2);
                    setTranslationX(translationX);
                    setTranslationY(translationY);
                    scale = scale * getSpacing(event) / spacing;
                    setScaleX(scale);
                    setScaleY(scale);
                    rotation = rotation + getDegree(event) - degree;
                    if (rotation > 360) {
                        rotation = rotation - 360;
                    }
                    if (rotation < -360) {
                        rotation = rotation + 360;
                    }
                    setRotation(rotation);
                    actionX = event.getRawX();
                    actionY = event.getRawY();
                }
                break;
            case MotionEvent.ACTION_UP:
                moveType = 0;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                moveType = 1;
                break;
        }
        return super.onTouchEvent(event);
    }

    // 触碰两点间距离
    private float getSpacing(MotionEvent event) {
        //通过三角函数得到两点间的距离
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    // 取旋转角度
    private float getDegree(MotionEvent event) {
        //得到两个手指间的旋转角度
        double delta_x = event.getX(0) - event.getX(1);
        double delta_y = event.getY(0) - event.getY(1);
        double radians = Math.atan2(delta_y, delta_x);
//        return (float) Math.toDegrees(radians);
        return 0;
    }
}
