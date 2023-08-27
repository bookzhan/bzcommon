package com.google.android.material.bottomsheet;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;

import com.bzcommon.utils.BZLogUtil;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

/**
 * Created by bookzhan on 2023−08-26 17:36.
 * description:
 */
public class ViewPagerBottomSheetBehavior<V extends View> extends BottomSheetBehavior<V> {

    public ViewPagerBottomSheetBehavior() {
        super();
    }

    public ViewPagerBottomSheetBehavior(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Nullable
    @VisibleForTesting
    @Override
    View findScrollingChild(View view) {
        if (view.getVisibility() != View.VISIBLE) {
            return null;
        }
        if (ViewCompat.isNestedScrollingEnabled(view)) {
            return view;
        }
        if (view instanceof ViewPager) {
            ViewPager viewPager = (ViewPager) view;
            View currentViewPagerChild = getCurrentView(viewPager);
            if (currentViewPagerChild == null) {
                return null;
            }
            return findScrollingChild(currentViewPagerChild);
        } else if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0, count = group.getChildCount(); i < count; i++) {
                View scrollingChild = findScrollingChild(group.getChildAt(i));
                if (scrollingChild != null) {
                    return scrollingChild;
                }
            }
        }
        return null;
    }

    public static View getCurrentView(ViewPager viewPager) {
        int currentItem = viewPager.getCurrentItem();
        for (int i = 0; i < viewPager.getChildCount(); ++i) {
            View child = viewPager.getChildAt(i);
            ViewPager.LayoutParams layoutParams = (ViewPager.LayoutParams) child.getLayoutParams();
            Class<?> myClass = layoutParams.getClass();
            try {
                Field privateField = myClass.getDeclaredField("position");
                privateField.setAccessible(true);
                int position = (int) privateField.get(layoutParams);
                if (!layoutParams.isDecor && currentItem == position) {
                    return child;
                }
            } catch (Throwable e) {
                BZLogUtil.e(e);
            }
        }
        return null;
    }

    public void updateScrollingChild() {
        if (viewRef == null) {
            return;
        }
        final View scrollingChild = findScrollingChild(viewRef.get());
        nestedScrollingChildRef = new WeakReference<>(scrollingChild);
    }

}
