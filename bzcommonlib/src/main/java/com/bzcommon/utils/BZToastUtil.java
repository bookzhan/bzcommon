package com.bzcommon.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
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
    private static Toast mToast;

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
            handler.post(() -> makeToast(content, duration));
        } else {
            makeToast(content, duration);
        }
    }

    private static void makeToast(String content, int duration) {
        //防止短时间重复调用
        if (TextUtils.isEmpty(content) || null == context || null != mToast) {
            return;
        }
        mToast = Toast.makeText(context, content, duration);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            mToast.addCallback(new Toast.Callback() {
                @Override
                public void onToastShown() {
                    super.onToastShown();
                }

                @Override
                public void onToastHidden() {
                    super.onToastHidden();
                    mToast = null;
                }
            });
        } else {
            handler.postDelayed(() -> mToast = null, 2000);
        }
        setContextCompat(mToast.getView(), context);
        mToast.show();
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
