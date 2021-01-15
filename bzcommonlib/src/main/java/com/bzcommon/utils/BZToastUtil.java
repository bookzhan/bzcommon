package com.bzcommon.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;


/**
 * Created by zhandalin on 2021-01-15 15:59.
 * description:
 */
public class BZToastUtil {
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private static Handler handler = new Handler(Looper.getMainLooper());
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
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, content, duration).show();
                }
            });
        } else {
            Toast.makeText(context, content, duration).show();
        }
    }
}
