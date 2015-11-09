package com.android.util;

import android.app.Activity;
import android.util.DisplayMetrics;

public class PositionUtil {
    public static String getPosition(Activity ac) {
        DisplayMetrics metric = new DisplayMetrics();
        ac.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels; // 宽度（PX） int height =
        int height = metric.heightPixels;// metric.heightPixels; // 高度（PX）
//		System.err.println( "width:"+ width +"\n  height:"+ height);
        return width + "*" + height;
    }

    public static int getWidth(Activity ac) {
        DisplayMetrics metric = new DisplayMetrics();
        ac.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels; // 宽度（PX） int height =
        return width;
    }

    public static int getHeight(Activity ac) {
        DisplayMetrics metric = new DisplayMetrics();
        ac.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int height = metric.heightPixels;// metric.heightPixels; // 高度（PX）
        return height;
    }
}
