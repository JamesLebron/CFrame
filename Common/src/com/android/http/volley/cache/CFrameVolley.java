package com.android.http.volley.cache;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.http.AndroidHttpClient;
import android.os.Build;

import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;

import java.io.File;

/**
 * Created by xudong on 2015/3/18.
 */
public class CFrameVolley {
    private static CFrameCache cache;
    /**
     * Default on-disk cache directory.
     */
    private static final String DEFAULT_CACHE_DIR = "volley";

    public static RequestQueue newRequestQueue(Context context, HttpStack stack) {
        File cacheDir = new File(context.getCacheDir(), DEFAULT_CACHE_DIR);

        String userAgent = "volley/0";
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            userAgent = packageName + "/" + info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }

        if (stack == null) {
            if (Build.VERSION.SDK_INT >= 9) {
                stack = new HurlStack();
            } else {
                stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
            }
        }

        Network network = new BasicNetwork(stack);
        CFrameCache.CacheConfig cacheConfig = new CFrameCache.CacheConfig();
        cacheConfig.rootDirectory = cacheDir;
        cacheConfig.maxMerryCacheSize = (int) (Runtime.getRuntime().maxMemory()) / 12;//1/12内存作为缓存
        cacheConfig.maxDiskCacheSize = 20 * 1028 * 1024;//硬盘上  20M缓存
        cache = new CFrameCache(cacheConfig);
        RequestQueue queue = new RequestQueue(cache, network);
        queue.start();

        return queue;
    }

    /**
     * Creates a default instance of the worker pool and calls {@link com.android.volley.RequestQueue#start()} on it.
     *
     * @param context A {@link android.content.Context} to use for creating the cache dir.
     * @return A started {@link com.android.volley.RequestQueue} instance.
     */
    public static RequestQueue newRequestQueue(Context context) {
        return newRequestQueue(context, null);
    }

    public static void free() {
        if (cache != null)
            cache.clearMCache();
    }
}
