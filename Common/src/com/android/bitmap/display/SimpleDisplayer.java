/**
 *@author oopp1990
 */
package com.android.bitmap.display;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.android.bitmap.core.BitmapDisplayConfig;

public class SimpleDisplayer implements Displayer {

    public void loadCompletedisplay(View imageView, Bitmap bitmap, BitmapDisplayConfig config) {
        switch (config.getAnimationType()) {
            case BitmapDisplayConfig.AnimationType.fadeIn:
                fadeInDisplay(imageView, bitmap);
                break;
            case BitmapDisplayConfig.AnimationType.userDefined:
                animationDisplay(imageView, bitmap, config.getAnimation());
                break;
            default:
                break;
        }
    }


    public void loadFailDisplay(View imageView, Bitmap bitmap) {
        if (imageView instanceof ImageView) {
            ((ImageView) imageView).setImageBitmap(bitmap);
        } else {
            imageView.setBackgroundDrawable(new BitmapDrawable(bitmap));
        }
    }


    private void fadeInDisplay(View imageView, Bitmap bitmap) {
        final TransitionDrawable td =
                new TransitionDrawable(new Drawable[]{
                        new ColorDrawable(android.R.color.transparent),
                        new BitmapDrawable(imageView.getResources(), bitmap)
                });
        if (imageView instanceof ImageView) {
            ((ImageView) imageView).setImageDrawable(td);
        } else {
            imageView.setBackgroundDrawable(td);
        }
        td.startTransition(300);
    }


    private void animationDisplay(View imageView, Bitmap bitmap, Animation animation) {
        animation.setStartTime(AnimationUtils.currentAnimationTimeMillis());
        if (imageView instanceof ImageView) {
            ((ImageView) imageView).setImageBitmap(bitmap);
        } else {
            imageView.setBackgroundDrawable(new BitmapDrawable(bitmap));
        }
        imageView.startAnimation(animation);
    }

}
