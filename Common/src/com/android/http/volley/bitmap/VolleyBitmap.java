package com.android.http.volley.bitmap;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.bitmap.BitmapInterface;
import com.android.bitmap.CBitmap;
import com.android.http.volley.cache.BitmapLruImageCache;


/**
 * Created by xudong on 2015/3/19.
 */
public final class VolleyBitmap implements BitmapInterface {
    private ImageLoader loader;
    private CBitmap cBitmap;
    private Context context;
    private BitmapLruImageCache imageCache;

    public VolleyBitmap(int cacheSize, Context context, RequestQueue queue) {
        imageCache = new BitmapLruImageCache(cacheSize);
        loader = new ImageLoader(queue, imageCache);
        this.context = context;
    }

    public ImageLoader getLoader() {
        return loader;
    }

    @Override
    public void display(View imageView, String uri) {
        if (TextUtils.isEmpty(uri) || imageView == null)
            return;
        if (imageView instanceof ImageView) {
            ImageView view = (ImageView) imageView;
            ImageLoader.ImageListener listener = ImageLoader.getImageListener(view, 0, 0);
            loader.get(uri, listener);
        } else {
            if (cBitmap == null)
                cBitmap = CBitmap.create(context);
            cBitmap.display(imageView, uri);
        }
    }

    @Override
    public void display(View imageView, String uri, int imageWidth, int imageHeight) {
        if (TextUtils.isEmpty(uri) || imageView != null)
            return;
        if (imageView instanceof ImageView) {
            ImageView view = (ImageView) imageView;
            ImageLoader.ImageListener listener = ImageLoader.getImageListener(view, imageWidth, imageHeight);
            loader.get(uri, listener, imageWidth, imageHeight);
        } else {
            if (cBitmap == null)
                cBitmap = CBitmap.create(context);
            cBitmap.display(imageView, uri, imageWidth, imageHeight);
        }
    }

    @Override
    public void display(View imageView, String uri, int defaultImageResId, int errorImageResId, boolean showing) {
        if (TextUtils.isEmpty(uri) || imageView != null)
            return;
        if (imageView instanceof ImageView) {
            ImageView view = (ImageView) imageView;
            ImageLoader.ImageListener listener = ImageLoader.getImageListener(view, defaultImageResId, errorImageResId);
            loader.get(uri, listener);
        } else {
            if (cBitmap == null)
                cBitmap = CBitmap.create(context);
            cBitmap.display(imageView, uri, defaultImageResId, errorImageResId, true);
        }
    }

    @Override
    public void free() {
        imageCache.evictAll();
        if (cBitmap != null)
            cBitmap.free();

    }
}
