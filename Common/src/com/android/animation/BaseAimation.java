package com.android.animation;

import android.app.Activity;

import com.android.common.R;

/**
 * Created by xudong on 2015/4/20.
 */
public class BaseAimation {

    public static void backAnn(Activity activity) {
        activity.overridePendingTransition(R.anim.back_zoomin, R.anim.back_zoomout);
    }

    public static void nextAnn(Activity activity){
        activity.overridePendingTransition(R.anim.next_zoomin, R.anim.next_zoomout);
    }
}
