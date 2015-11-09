package com.android.bitmap;

import android.view.View;


/**
 * Created by xudong on 2015/3/19.
 */
public interface BitmapInterface {
    public void display(View imageView, String uri);

    public void display(View imageView, String uri, int imageWidth, int imageHeight);

    public void display(View view, final String uri,
                        int defaultImageResId, int errorImageResId, boolean showing);

    public void free();
}
