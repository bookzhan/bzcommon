package com.bzcommon.asyn;

/**
 * Created by bookzhan on 2023−08-06 07:22.
 * description:
 */
public interface Success<T> {
    void onSuccess(T result);
}