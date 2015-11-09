package com.android.util;

import android.util.Log;

public class Logger {

    private static String t() {
        return "[" + Thread.currentThread().getName() + "] ";
    }

    public static Boolean IS_OPEN = true;

    public static void v(String tag, String msg) {
        if (IS_OPEN)
            Log.v(tag, t() + msg);
    }

    public static void i(String tag, String msg) {
        if (IS_OPEN)
            Log.i(tag, t() + msg);
    }

    public static void d(String tag, String msg) {
        if (IS_OPEN) {
            if (msg.length() > 3500) {
                Log.d(tag, msg.substring(0, 3500));
                d(tag, msg.substring(3500));
            } else {
                Log.d(tag, t() + msg);
            }
        }
    }

    public static void e(String tag, String msg) {
        if (IS_OPEN)
            Log.e(tag, t() + msg);
    }

    public static void w(String tag, String msg) {
        if (IS_OPEN)
            Log.w(tag, t() + msg);
    }
}
