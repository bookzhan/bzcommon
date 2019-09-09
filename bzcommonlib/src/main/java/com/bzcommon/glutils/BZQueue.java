package com.bzcommon.glutils;


import java.util.ArrayList;

/**
 * Created by zhandalin on 2019-08-01 19:44.
 * 说明:
 */
public class BZQueue<T> {
    private static final String TAG = "bz_RecordInfoQueue";
    private ArrayList<T> arrayList = new ArrayList<>();

    public synchronized void add(T object) {
        if (null == object) {
            return;
        }
        arrayList.add(object);
    }

    public synchronized int size() {
        return arrayList.size();
    }

    public synchronized void remove(int index) {
        if (index >= arrayList.size()) {
            return;
        }
        arrayList.remove(index);
    }

    public synchronized void remove(T object) {
        if (null == object) {
            return;
        }
        arrayList.remove(object);
    }

    public synchronized T getFront() {
        if (arrayList.isEmpty()) {
            return null;
        }
        return arrayList.get(0);
    }

    public synchronized T getBack() {
        if (arrayList.isEmpty()) {
            return null;
        }
        return arrayList.get(arrayList.size() - 1);
    }

    public synchronized T get(int index) {
        if (arrayList.isEmpty()) {
            return null;
        }
        return arrayList.get(index);
    }

    public synchronized void clear() {
        arrayList.clear();
    }

    public synchronized void release() {
        if (arrayList.isEmpty()) {
            return;
        }
        arrayList.clear();
    }
}
