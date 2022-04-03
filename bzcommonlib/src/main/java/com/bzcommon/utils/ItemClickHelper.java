package com.bzcommon.utils;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by bookzhan on 2022-04-03 13:55.
 * description:
 */
public class ItemClickHelper extends RecyclerView.SimpleOnItemTouchListener {

    private final OnItemClickListener mOnItemClickListener;
    private RecyclerView mRv;
    private final GestureDetectorCompat mGestureDetectorCompat;

    public ItemClickHelper(Context context, OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
        mGestureDetectorCompat = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (null == mRv || null == mOnItemClickListener) {
                    return false;
                }
                View childView = mRv.findChildViewUnder(e.getX(), e.getY());
                if (null == childView) {
                    return false;
                }
                RecyclerView.ViewHolder childViewHolder = mRv.getChildViewHolder(childView);
                if (null == childViewHolder) {
                    return false;
                }
                mOnItemClickListener.onItemClick(childViewHolder);
                return true;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        mRv = rv;
        return true;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        super.onTouchEvent(rv, e);
        mGestureDetectorCompat.onTouchEvent(e);
    }

    public interface OnItemClickListener {
        void onItemClick(RecyclerView.ViewHolder viewHolder);
    }
}
