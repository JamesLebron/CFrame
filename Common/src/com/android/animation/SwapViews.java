package com.android.animation;

/**
 * Created by xudong on 2015/4/7.
 */

import android.view.View;
import android.view.animation.DecelerateInterpolator;

public final class SwapViews implements Runnable {
    private boolean mIsFirstView;
    View image1;
    View image2;
    private Flip3dAnimation normal;
    private Flip3dAnimation flip;
    public void setmIsFirstView(boolean mIsFirstView) {
        this.mIsFirstView = mIsFirstView;
    }

    public SwapViews(boolean isFirstView, View image1, View image2) {
        mIsFirstView = isFirstView;
        this.image1 = image1;
        this.image2 = image2;
        normal = new Flip3dAnimation(-90, 0, 0, 0);
        flip = new Flip3dAnimation(90, 0, 0, 0);
    }

    public void run() {
        final float centerX = image1.getWidth() / 2.0f;
        final float centerY = image1.getHeight() / 2.0f;

        final Flip3dAnimation rotation;
        if (mIsFirstView) {
            image1.setVisibility(View.GONE);
            image2.setVisibility(View.VISIBLE);
            image2.requestFocus();

            rotation = normal;
            rotation.setStartAndOff(-90,0,centerX,centerY);
        } else {
            image2.setVisibility(View.GONE);
            image1.setVisibility(View.VISIBLE);
            image1.requestFocus();

            rotation = flip;
            rotation.setStartAndOff(90,0,centerX,centerY);
        }
//        rotation.cancel();
        rotation.reset();
        rotation.setDuration(200);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new DecelerateInterpolator());

        if (mIsFirstView) {
            image2.startAnimation(rotation);
        } else {
            image1.startAnimation(rotation);
        }
    }
}
