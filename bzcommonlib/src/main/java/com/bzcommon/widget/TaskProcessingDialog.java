package com.bzcommon.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import com.luoye.bzcommonlib.R;

import java.util.Locale;

/**
 * Created by bookzhan on 2023−05-03 14:38.
 * description:
 */
public class TaskProcessingDialog extends Dialog {

    protected TextView mTvMsg;
    protected TextView mTvProgress;

    public TaskProcessingDialog(Context context) {
        this(context, R.style.task_processing_dialog);
    }

    public TaskProcessingDialog(Context context, int theme) {
        super(context, theme);
        setContentView(R.layout.layout_task_processing_dialog);
        findViewById(R.id.iv_delete).setOnClickListener(v -> cancel());
        mTvMsg = findViewById(R.id.tv_msg);
        mTvProgress = findViewById(R.id.tv_progress);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    public void setMassage(String massage) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            if (mTvMsg.getVisibility() != View.VISIBLE) {
                mTvMsg.setVisibility(View.VISIBLE);
            }
            mTvMsg.setText(massage);
        } else {
            mTvMsg.post(() -> {
                if (mTvMsg.getVisibility() != View.VISIBLE) {
                    mTvMsg.setVisibility(View.VISIBLE);
                }
                mTvMsg.setText(massage);
            });
        }
    }

    public void setProgress(float progress) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            mTvProgress.setText(String.format(Locale.ENGLISH, "%.2f%%", progress * 100));
        } else {
            mTvProgress.post(() -> mTvProgress.setText(String.format(Locale.ENGLISH, "%.2f%%", progress * 100)));
        }
    }

}
