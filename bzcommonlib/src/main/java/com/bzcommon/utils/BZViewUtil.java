package com.bzcommon.utils;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

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

    public static <T> List<T> findAllChildViewByClass(ViewGroup viewGroup, Class<T> clazz) {
        List<T> result = new ArrayList<>();
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (clazz.isInstance(child)) {
                result.add(clazz.cast(child));
            }
            if (child instanceof ViewGroup) {
                result.addAll(findAllChildViewByClass((ViewGroup) child, clazz));
            }
        }
        return result;
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
