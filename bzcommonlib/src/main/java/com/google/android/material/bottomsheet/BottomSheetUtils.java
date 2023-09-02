package com.google.android.material.bottomsheet;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;

/**
 * Created by bookzhan on 2023−08-26 20:07.
 * description:
 */
public class BottomSheetUtils {
    public BottomSheetUtils() {
    }

    public static void setupViewPager(ViewPager viewPager) {
        View bottomSheetParent = findBottomSheetParent(viewPager);
        if (bottomSheetParent != null) {
            viewPager.addOnPageChangeListener(new BottomSheetViewPagerListener(bottomSheetParent));
        }
    }

    private static View findBottomSheetParent(View view) {
        ViewParent parent;
        for (View current = view; current != null; current = parent != null && parent instanceof View ? (View) parent : null) {
            ViewGroup.LayoutParams params = current.getLayoutParams();
            if (params instanceof CoordinatorLayout.LayoutParams && ((CoordinatorLayout.LayoutParams) params).getBehavior() instanceof ViewPagerBottomSheetBehavior) {
                return current;
            }
            parent = current.getParent();
        }
        return null;
    }

    private static class BottomSheetViewPagerListener extends ViewPager.SimpleOnPageChangeListener {
        private final ViewPagerBottomSheetBehavior<View> behavior;
        private final View mBottomSheetParent;

        private BottomSheetViewPagerListener(View bottomSheetParent) {
            this.behavior = (ViewPagerBottomSheetBehavior<View>) BottomSheetBehavior.from(bottomSheetParent);
            mBottomSheetParent = bottomSheetParent;
        }

        public void onPageSelected(int position) {
            mBottomSheetParent.post(behavior::updateScrollingChild);
        }
    }
}
