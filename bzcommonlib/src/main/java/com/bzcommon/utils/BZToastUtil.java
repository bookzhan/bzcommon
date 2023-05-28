package com.bzcommon.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import java.lang.reflect.Field;


/**
 * Created by zhandalin on 2021-01-15 15:59.
 * description:
 */
public class BZToastUtil {
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private static final Handler handler = new Handler(Looper.getMainLooper());
    private static final String TAG = "bz_BZToastUtil";

    public static void init(Context context) {
        BZToastUtil.context = context.getApplicationContext();
    }

    public static void showToast(final String content) {
        showToast(content, Toast.LENGTH_SHORT);
    }

    public static void showToast(final String content, final int duration) {
        if (null == context) {
            BZLogUtil.e(TAG, "showToast null==context");
            return;
        }
        if (Looper.myLooper() != Looper.getMainLooper()) {
            handler.post(() -> {
                Toast toast = Toast.makeText(context, content, duration);
                setContextCompat(toast.getView(), context);
                toast.show();
            });
        } else {
            Toast toast = Toast.makeText(context, content, duration);
            setContextCompat(toast.getView(), context);
            toast.show();
        }
    }

    private static void setContextCompat(View view, Context context) {
        if (null == view || null == context) {
            return;
        }
        if (Build.VERSION.SDK_INT == 25) {
            try {
                @SuppressLint("DiscouragedPrivateApi")
                Field field = View.class.getDeclaredField("mContext");
                field.setAccessible(true);
                field.set(view, context);
            } catch (Throwable throwable) {
                BZLogUtil.e(throwable);
            }
        }
    }
}
