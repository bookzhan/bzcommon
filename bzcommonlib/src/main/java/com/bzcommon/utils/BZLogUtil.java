package com.bzcommon.utils;

public class BZLogUtil {
    public final static String TAG_PREFIX = "bz_";
    private static boolean isShowLog = true;

    public static void setShowLog(boolean isLog) {
        BZLogUtil.isShowLog = isLog;
    }

    public static boolean isShowLog() {
        return isShowLog;
    }

    public static void v(String tag, String msg) {
        if (isShowLog) {
            android.util.Log.v(tag, msg);
        }
    }

    public static void v(String msg) {
        if (isShowLog) {
            android.util.Log.v(TAG_PREFIX, msg);
        }

    }

    public static void d(String tag, String msg) {
        if (isShowLog) {
            android.util.Log.d(tag, msg);
        }
    }

    public static void d(String msg) {
        if (isShowLog) {
            android.util.Log.d(TAG_PREFIX, msg);
        }

    }

    public static void w(String tag, String msg) {
        if (isShowLog) {
            android.util.Log.w(tag, msg);
        }
    }

    public static void w(String msg) {
        if (isShowLog) {
            android.util.Log.w(TAG_PREFIX, msg);
        }

    }

    /**
     * Send a {@link #isShowLog} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void d(String tag, String msg, Throwable tr) {
        if (isShowLog) {
            android.util.Log.d(tag, msg, tr);
        }
    }

    public static void i(String tag, String msg) {
        if (isShowLog) {
            android.util.Log.i(tag, msg);
        }
    }

    /**
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void i(String tag, String msg, Throwable tr) {
        if (isShowLog) {
            android.util.Log.i(tag, msg, tr);
        }

    }

    /**
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void e(String tag, String msg) {
        if (isShowLog) {
            android.util.Log.e(tag, msg);
        }
    }

    public static void e(String msg) {
        if (isShowLog) {
            android.util.Log.e(TAG_PREFIX, msg);
        }
    }

    /**
     * @param tag Used to identify the source of a log message. It usually
     *            identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void e(String tag, String msg, Throwable tr) {
        if (isShowLog) {
            android.util.Log.e(tag, msg, tr);
        }
    }

    public static void e(String msg, Throwable tr) {
        if (isShowLog) {
            android.util.Log.e(TAG_PREFIX, msg, tr);
        }
    }

    public static void e(Throwable tr) {
        if (isShowLog) {
            android.util.Log.e(TAG_PREFIX, tr.getMessage(), tr);
        }
    }
}
