package com.android.widget;

import android.os.Build;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;

/**
 * Created by lr on 2015/6/08.
 */
public class AnimUtil {
    private static final boolean CURRENT_VERSION = Build.VERSION.SDK_INT > 15; // 判断当前版本
    static float alpha = 250; // 默认透明度

    // height change animation
    public static void collapse(final View v, final int minHeight, final ImageView view) {

        final int initialHeight = v.getMeasuredHeight();
        final int offset = initialHeight - minHeight; // 需要缩放的高度
        if (CURRENT_VERSION)
            alpha = view.getImageAlpha();
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.getLayoutParams().height = minHeight;
                    v.requestLayout();
                    view.setAlpha(255 * interpolatedTime);
                } else {
                    int upAlpha = (int) (alpha + (alpha * interpolatedTime));
                    if (upAlpha > 255) {
                        upAlpha = 255;
                    }
                    v.requestLayout();
                    view.setAlpha(upAlpha);
                    v.getLayoutParams().height = initialHeight - (int) (offset * interpolatedTime);
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }

        };
        a.setDuration((int) (minHeight / v.getContext().getResources().getDisplayMetrics().density));//
        v.startAnimation(a);
    }
}

