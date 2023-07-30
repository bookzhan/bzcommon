package com.bzcommon.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by bookzhan on 2023−07-30 13:37.
 * description:
 */
public class StrokeItemDecoration extends RecyclerView.ItemDecoration {
    private final Paint mPaint;

    public StrokeItemDecoration(int borderSize, int color) {
        mPaint = new Paint();
        mPaint.setColor(color);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(borderSize);
    }

    @Override
    public void onDraw(@NonNull Canvas canvas, RecyclerView parent, @NonNull RecyclerView.State state) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            canvas.drawRect(child.getLeft(), child.getTop(), child.getRight(), child.getBottom(), mPaint);
        }
    }
}
