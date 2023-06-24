package com.bzcommon.bean;

import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

/**
 * Created by bookzhan on 2023−06-24 19:38.
 * description:
 */
public class TimeSequenceLiveData<T> extends MutableLiveData<T> {
    private long mLastValueTime = -1;

    @Override
    public void postValue(T value) {
        mLastValueTime = SystemClock.uptimeMillis();
        super.postValue(value);

    }

    @Override
    public void setValue(T value) {
        mLastValueTime = SystemClock.uptimeMillis();
        super.setValue(value);
    }

    @Override
    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
        super.observe(owner, new Observer<T>() {
            private final long initTime = SystemClock.uptimeMillis();

            @Override
            public void onChanged(T t) {
                if (initTime < mLastValueTime) {
                    observer.onChanged(t);
                }
            }
        });
    }
}
