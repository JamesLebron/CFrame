package com.android.util;

/**
 * Created by xudong on 2015/4/7.
 */
import android.view.View;
import android.view.animation.Animation;

import com.android.animation.SwapViews;

public final class DisplayNextView implements Animation.AnimationListener {
    private boolean mCurrentView;
    View image1;
    View image2;
    SwapViews swapViews;

    public void setmCurrentView(boolean mCurrentView) {
        this.mCurrentView = mCurrentView;
    }

    public DisplayNextView(boolean currentView, View image1, View image2) {
        mCurrentView = currentView;
        this.image1 = image1;
        this.image2 = image2;
        swapViews = new SwapViews(currentView,image1,image2);
    }

    public void onAnimationStart(Animation animation) {
    }

    public void onAnimationEnd(Animation animation) {
        swapViews.setmIsFirstView(mCurrentView);
//        image1.clearAnimation();
//        image1.st
        image1.post(swapViews);
    }

    public void onAnimationRepeat(Animation animation) {
    }
}