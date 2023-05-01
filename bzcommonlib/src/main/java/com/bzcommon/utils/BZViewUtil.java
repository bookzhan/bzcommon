package com.bzcommon.utils;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by bookzhan on 2023−05-01 10:53.
 * description:
 */
public class BZViewUtil {

    public static <T> T findChildViewByClass(ViewGroup viewGroup, Class<T> clazz) {
        if (viewGroup == null) {
            return null;
        }
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (clazz.isInstance(child)) {
                return clazz.cast(child);
            } else if (child instanceof ViewGroup) {
                T result = findChildViewByClass((ViewGroup) child, clazz);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    public static <T> T findParentViewByClass(View view, Class<T> clazz) {
        if (view == null) {
            return null;
        }
        ViewGroup viewGroup = (ViewGroup) view.getParent();
        while (viewGroup != null) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                if (clazz.isInstance(child)) {
                    return clazz.cast(child);
                } else if (child instanceof ViewGroup) {
                    T result = findParentViewByClass(child, clazz);
                    if (result != null) {
                        return result;
                    }
                }
            }
            viewGroup = (ViewGroup) viewGroup.getParent();
        }
        return null;
    }
}
