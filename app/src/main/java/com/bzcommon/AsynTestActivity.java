package com.bzcommon;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bzcommon.asyn.Asyn;
import com.bzcommon.asyn.Exception;
import com.bzcommon.asyn.Job;
import com.bzcommon.asyn.Success;
import com.bzcommon.utils.BZLogUtil;

public class AsynTestActivity extends AppCompatActivity {
    private final static String TAG = "AsynTestActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asyn_test);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_container, new AsynTestFragment()).commit();
    }

    public void testAsyn(View view) {
        BZLogUtil.d(TAG, "start Thread=" + Thread.currentThread());
        Asyn.on(this, new Job<Boolean>() {
            @Override
            public Boolean run() {
                BZLogUtil.d(TAG, "run Thread=" + Thread.currentThread());
                try {
                    Thread.sleep(5000);
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
}