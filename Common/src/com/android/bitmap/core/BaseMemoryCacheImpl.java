/**
 * @author oopp1990
 */
package com.android.bitmap.core;

import android.graphics.Bitmap;

import com.android.util.Utils;

public class BaseMemoryCacheImpl implements IMemoryCache {

    private final LruMemoryCache<String, Bitmap> mMemoryCache;

    public BaseMemoryCacheImpl(int size) {
        mMemoryCache = new LruMemoryCache<String, Bitmap>(size) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return Utils.getBitmapSize(bitmap);
            }
        };
    }

    @Override
    public void put(String key, Bitmap bitmap) {
        mMemoryCache.put(key, bitmap);
    }

    @Override
    public Bitmap get(String key) {
        return mMemoryCache.get(key);
    }

    @Override
    public void evictAll() {
        mMemoryCache.evictAll();
    }

    @Override
    public void remove(String key) {
        mMemoryCache.remove(key);
    }


}
