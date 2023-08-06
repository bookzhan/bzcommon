package com.bzcommon.asyn;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.bzcommon.utils.BZLogUtil;

import java.lang.ref.SoftReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by bookzhan on 2023−08-06 06:24.
 * description:
 */
public class Asyn<T> implements DefaultLifecycleObserver {
    private final static String TAG = "Asyn";
    private final static ExecutorService mNetWorkExecutor = Executors.newFixedThreadPool(3);
    private final static ExecutorService mAsynExecutor = Executors.newFixedThreadPool(3);
    private final static Handler mMainHandler = new Handler(Looper.getMainLooper());

    private @Nullable Success<T> mSuccess;
    private @Nullable Exception mException;
    private final @NonNull Job<T> mJob;
    private @Nullable Future<?> mSubmit;
    private @Nullable SoftReference<Fragment> mFragmentSoftReference;
    private @Nullable SoftReference<AppCompatActivity> mAppCompatActivitySoftReference;
    private volatile boolean mIsDestroy = false;

    private Asyn(@Nullable AppCompatActivity activity, @NonNull Job<T> job) {
        mJob = job;
        if (null != activity) {
            mAppCompatActivitySoftReference = new SoftReference<>(activity);
            activity.getLifecycle().addObserver(this);
        }
    }

    private Asyn(@Nullable Fragment fragment, @NonNull Job<T> job) {
        mJob = job;
        if (null != fragment) {
            mFragmentSoftReference = new SoftReference<>(fragment);
            fragment.getLifecycle().addObserver(this);
        }
    }

    private Asyn(@NonNull Job<T> job) {
        mJob = job;
    }

    @NonNull
    public static <T> Asyn<T> on(@Nullable AppCompatActivity activity, @NonNull Job<T> job) {
        return new Asyn<>(activity, job);
    }

    @NonNull
    public static <T> Asyn<T> on(@Nullable Fragment fragment, @NonNull Job<T> job) {
        return new Asyn<>(fragment, job);
    }

    @NonNull
    public static <T> Asyn<T> on(@NonNull Job<T> job) {
        return new Asyn<>(job);
    }

    public Asyn<T> onSuccess(Success<T> success) {
        mSuccess = success;
        return this;
    }

    public Asyn<T> onException(Exception exception) {
        mException = exception;
        return this;
    }

    public void fireNetWork() {
        mSubmit = mNetWorkExecutor.submit(this::runTask);
    }

    public void fireAsynWork() {
        mSubmit = mAsynExecutor.submit(this::runTask);
    }

    @MainThread
    private void removeObserver() {
        if (null != mAppCompatActivitySoftReference) {
            AppCompatActivity appCompatActivity = mAppCompatActivitySoftReference.get();
            if (null != appCompatActivity) {
                appCompatActivity.getLifecycle().removeObserver(this);
            }
        }
        if (null != mFragmentSoftReference) {
            Fragment fragmentTemp = mFragmentSoftReference.get();
            if (null != fragmentTemp) {
                fragmentTemp.getLifecycle().removeObserver(this);
            }
        }
        BZLogUtil.d(TAG, "removeObserver");
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        mIsDestroy = true;
        removeObserver();
        cancelTask();
    }

    private void cancelTask() {
        if (null != mSubmit && !mSubmit.isDone()) {
            try {
                mSubmit.cancel(true);
            } catch (Throwable throwable) {
                BZLogUtil.e(TAG, throwable);
            }
        }
        mSubmit = null;
    }

    private boolean isAvailable() {
        if (mIsDestroy) {
            return false;
        }
        if (null != mAppCompatActivitySoftReference) {
            AppCompatActivity appCompatActivity = mAppCompatActivitySoftReference.get();
            return null != appCompatActivity && !appCompatActivity.isDestroyed() && !appCompatActivity.isFinishing();
        }
        if (null != mFragmentSoftReference) {
            Fragment fragment = mFragmentSoftReference.get();
            return null != fragment && !fragment.isDetached();
        }
        return true;
    }

    private void runTask() {
        try {
            T result = mJob.run();
            if (null != mSuccess) {
                mMainHandler.post(() -> {
                    if (null == mSuccess) {
                        return;
                    }
                    if (isAvailable()) {
                        mSuccess.onSuccess(result);
                    } else {
                        BZLogUtil.w(TAG, "runTask,isDestroy not post");
                    }
                });
            }
        } catch (Throwable throwable) {
            //cancelTask导致的异常,内部处理
            if (!isAvailable() && throwable instanceof RuntimeException && throwable.getCause() instanceof InterruptedException) {
                BZLogUtil.w(TAG, "runTask catch,throwable instanceof InterruptedException and isDestroy,not call back");
                return;
            }
            if (null != mException) {
                mMainHandler.post(() -> {
                    if (null == mException) {
                        return;
                    }
                    if (isAvailable()) {
                        mException.onException(throwable);
                    } else {
                        BZLogUtil.w(TAG, "runTask catch,isDestroy not post");
                    }
                });
            }
        } finally {
            mMainHandler.post(this::removeObserver);
        }
    }
}
