package com.android.http.volley.cache;


import android.support.v4.util.LruCache;

import com.android.volley.Cache;
import com.android.volley.toolbox.DiskBasedCache;


import java.io.File;

/**
 * Created by xudong on 2015/3/18.
 * 集成内存缓存
 * 集成文件缓存
 */
public class CFrameCache extends DiskBasedCache implements Cache {
    public final int maxMerryCache;
    private final LruCache<String, Entry> mMemoryCache;

    public CFrameCache(CacheConfig config) {
        super(config.rootDirectory, config.maxDiskCacheSize);
        this.maxMerryCache = config.maxMerryCacheSize;

        mMemoryCache = new LruCache<String, Entry>(config.maxMerryCacheSize);

    }

    @Override
    public Entry get(String key) {
        if (key == null)
            return super.get(key);
        Entry en = mMemoryCache.get(key);
        if (en != null)
            return en;
        else return super.get(key);
    }

    @Override
    public void put(String key, Entry entry) {
        mMemoryCache.put(key, entry);
        super.put(key, entry);
    }

    @Override
    public void initialize() {
        super.initialize();
    }

    @Override
    public void invalidate(String key, boolean fullExpire) {
        super.invalidate(key, fullExpire);
    }

    @Override
    public void remove(String key) {
        super.remove(key);
    }

    @Override
    public void clear() {
        super.clear();
    }

    public void clearMCache() {
        if (mMemoryCache != null)
            mMemoryCache.evictAll();
    }


    public static class CacheConfig {
        public static File rootDirectory;
//        public static boolean useMemerryCache = true;
//        public static boolean useDiskCache = true;

        public static int maxMerryCacheSize = 5 * 1024 * 1024;
        public static int maxDiskCacheSize = 50 * 1024 * 1024;
    }
}
