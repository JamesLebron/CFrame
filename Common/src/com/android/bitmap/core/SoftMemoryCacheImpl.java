/**
 * @author oopp1990
 */
package com.android.bitmap.core;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.graphics.Bitmap;

public class SoftMemoryCacheImpl implements IMemoryCache {

    private final HashMap<String, SoftReference<Bitmap>> mMemoryCache;

    public SoftMemoryCacheImpl(int size) {

        mMemoryCache = new HashMap<String, SoftReference<Bitmap>>();
    }

    @Override
    public void put(String key, Bitmap bitmap) {
        mMemoryCache.put(key, new SoftReference<Bitmap>(bitmap));
    }

    @Override
    public Bitmap get(String key) {
        SoftReference<Bitmap> memBitmap = mMemoryCache.get(key);
        if (memBitmap != null) {
            return memBitmap.get();
        }
        return null;
    }

    @Override
    public void evictAll() {
        mMemoryCache.clear();
    }

    @Override
    public void remove(String key) {
        mMemoryCache.remove(key);
    }

}
