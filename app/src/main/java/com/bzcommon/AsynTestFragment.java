package com.bzcommon;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bzcommon.asyn.Asyn;
import com.bzcommon.asyn.Exception;
import com.bzcommon.asyn.Job;
import com.bzcommon.asyn.Success;
import com.bzcommon.utils.BZLogUtil;

/**
 * Created by bookzhan on 2023−08-06 08:53.
 * description:
 */
public class AsynTestFragment extends Fragment {
    private final static String TAG = "AsynTestFragment";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TextView textView = new TextView(container.getContext());
        textView.setText("AsynTestFragment");
        textView.setTextColor(Color.RED);
        textView.setTextSize(30);
        return textView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BZLogUtil.d(TAG, "start Thread=" + Thread.currentThread());
        Asyn.on(this, new Job<Boolean>() {
            @Override
            public Boolean run() {
                BZLogUtil.d(TAG, "run Thread=" + Thread.currentThread());
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return true;
            }
        }).onSuccess(new Success<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                BZLogUtil.d(TAG, "onSuccess Thread=" + Thread.currentThread() + " result=" + result);
            }
        }).onException(new Exception() {
            @Override
            public void onException(Throwable throwable) {
                BZLogUtil.d(TAG, "onException Thread=" + Thread.currentThread() + " throwable=" + throwable);
            }
        }).fireAsynWork();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BZLogUtil.d(TAG, "onDestroy");
    }
}
