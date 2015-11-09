package com.android.util;

import android.util.Log;

public class Logs {
    public static boolean isShowLog = true;

    public static void i(String tag, String msg) {
        if (isShowLog) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (isShowLog) {
            Log.w(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (isShowLog) {
            Log.d(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (isShowLog) {
            Log.e(tag, msg);
        }
    }

}
