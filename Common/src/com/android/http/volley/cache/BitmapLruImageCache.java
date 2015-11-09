package com.android.http.volley.cache;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader;


/**
 * Created by xudong on 2015/3/18.
 */
public class BitmapLruImageCache extends LruCache<String, Bitmap> implements ImageLoader.ImageCache {
    private final String TAG = this.getClass().getSimpleName();

    public BitmapLruImageCache(int maxSize) {
        super(maxSize);
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight();
    }

    @Override
    public Bitmap getBitmap(String url) {
//        Logs.w(TAG, "Retrieved item from Mem Cache");
        return get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
//        Logs.w(TAG, "Added item to Mem Cache");
        put(url, bitmap);
    }
}
