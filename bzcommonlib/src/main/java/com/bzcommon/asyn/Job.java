package com.bzcommon.asyn;

/**
 * Created by bookzhan on 2023−08-06 07:21.
 * description:
 */
public interface Job<T> {
    T run();
}