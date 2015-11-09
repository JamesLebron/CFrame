package com.android.base;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.android.volley.RequestQueue;
import com.android.http.volley.cache.CFrameVolley;
import com.android.util.Logs;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.internal.Supplier;
import com.facebook.common.memory.MemoryTrimmable;
import com.facebook.common.memory.MemoryTrimmableRegistry;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * @author cxd
 */
public abstract class BaseApplication extends MultiDexApplication {
    public Map<Object, Object> cache;
    public static BaseApplication app;
    private DoTask doTask;



    @Override
    public void onCreate() {
        super.onCreate();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Logs.isShowLog = true;
        Logs.i("AppContextControll.onCreate() === > ", "......");
        app = this;
//        Thread.setDefaultUncaughtExceptionHandler(this);
//        init();
    }

    public void init() {
        try {
            doTask = new DoTask();
            app.cache = new HashMap<>();

            RequestQueue queue = BaseVolleyHttpClient.newRequestQueue(app, null);
            app.cache.put("RequestQueue", queue);

            BaseVolleyHttpClient.init(queue);

            app.initFresco();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void addTask(BaseTask task) {
        doTask.addTask(task);
    }

    private void initFresco() {
        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder()
                .setBaseDirectoryPath(getCacheDir())
                .setBaseDirectoryName("FrescoCache")
                .setMaxCacheSize(100 * ByteConstants.MB)
                .build();
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setMainDiskCacheConfig(diskCacheConfig)
                        // Bitmap缓存存储Bitmap对象，这些Bitmap对象可以立刻用来显示或者用于后处理；5.0以下位于ashmem，5.0及以上位于JavaHeap
                .setBitmapMemoryCacheParamsSupplier(mBitmapMemoryCacheParamsSupplier)
                        // 未解码图片的内存缓存，这个缓存存储的是原始压缩格式的图片
                .setEncodedMemoryCacheParamsSupplier(mEncodedMemoryCacheParamsSupplier)
                .setMemoryTrimmableRegistry(mMemoryTrimmableRegistry)
                .build();
        Fresco.initialize(this, config);
    }

    @Override
    public void onLowMemory() {
        try {
            CFrameVolley.free();
            System.gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onLowMemory();
    }


    /**
     * 未解码图片内存缓存
     */
    private Supplier<MemoryCacheParams> mEncodedMemoryCacheParamsSupplier = new Supplier<MemoryCacheParams>() {
        // We want memory cache to be bound only by its memory consumption
        private static final int MAX_CACHE_ENTRIES = Integer.MAX_VALUE;
        private static final int MAX_EVICTION_QUEUE_ENTRIES = Integer.MAX_VALUE;

        @Override
        public MemoryCacheParams get() {
            final int maxCacheSize = getMaxCacheSize();
            Logs.e("mEncodedMemoryCacheParamsSupplier: ", "maxCacheSize: " + maxCacheSize / ByteConstants.MB);
            final int maxCacheEntrySize = maxCacheSize / 8;
            return new MemoryCacheParams(
                    maxCacheSize,
                    MAX_CACHE_ENTRIES,
                    maxCacheSize,
                    MAX_EVICTION_QUEUE_ENTRIES,
                    maxCacheEntrySize);
        }

        private int getMaxCacheSize() {
            final int maxMemory = (int) Math.min(Runtime.getRuntime().maxMemory(), Integer.MAX_VALUE);
            if (maxMemory < 16 * ByteConstants.MB) {
                return ByteConstants.MB;
            } else if (maxMemory < 32 * ByteConstants.MB) {
                return 2 * ByteConstants.MB;
            } else {
                return 4 * ByteConstants.MB;
            }
        }
    };

    private MemoryTrimmableRegistry mMemoryTrimmableRegistry = new MemoryTrimmableRegistry() {

        @Override
        public void registerMemoryTrimmable(MemoryTrimmable memoryTrimmable) {
            int k = 0;
        }

        @Override
        public void unregisterMemoryTrimmable(MemoryTrimmable memoryTrimmable) {
            int k = 0;
        }

    };
    /**
     * 图片内存缓存
     */
    private Supplier<MemoryCacheParams> mBitmapMemoryCacheParamsSupplier = new Supplier<MemoryCacheParams>() {

        private static final int MAX_CACHE_ENTRIES = 256;
        private static final int MAX_EVICTION_QUEUE_SIZE = Integer.MAX_VALUE;
        private static final int MAX_EVICTION_QUEUE_ENTRIES = Integer.MAX_VALUE;
        private static final int MAX_CACHE_ENTRY_SIZE = Integer.MAX_VALUE;

        @Override
        public MemoryCacheParams get() {
            int maxSize = getMaxCacheSize();
            Logs.e("mBitmapMemoryCacheParamsSupplier: ", "maxSize = " + maxSize / ByteConstants.MB);
            return new MemoryCacheParams(
                    maxSize,
                    MAX_CACHE_ENTRIES,
                    MAX_EVICTION_QUEUE_SIZE,
                    MAX_EVICTION_QUEUE_ENTRIES,
                    MAX_CACHE_ENTRY_SIZE);
        }

        private int getMaxCacheSize() {
            final int maxMemory = Math.min(((ActivityManager) getSystemService(ACTIVITY_SERVICE)).getMemoryClass() * ByteConstants.MB, Integer.MAX_VALUE);
            if (maxMemory < 32 * ByteConstants.MB) {
                return 4 * ByteConstants.MB;
            } else if (maxMemory < 64 * ByteConstants.MB) {
                return 6 * ByteConstants.MB;
            } else {
                // We don't want to use more ashmem on Gingerbread for now, since it doesn't respond well to
                // native memory pressure (doesn't throw exceptions, crashes app, crashes phone)
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD) {
                    return 8 * ByteConstants.MB;
                } else {
                    return maxMemory / 4;
                }
            }
        }

    };


    private static SharedPreferences mSharedPreferences;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     * Preference文件读取
     */
    public static void save(String key, String value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getString(String key) {
        return mSharedPreferences.getString(key, null);
    }

    public static void save(String key, boolean value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void save(String key, long value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static void save(String key, int value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void save(String key, Set<String> values) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putStringSet(key, values);
        editor.commit();
    }

    public static int getInt(String key) {
        return mSharedPreferences.getInt(key, -1);
    }

    public static int getInt(String key, int def) {
        return mSharedPreferences.getInt(key, def);
    }

    public static long getLong(String key) {
        return mSharedPreferences.getLong(key, -1);
    }

    public static boolean getBoolean(String key) {
        return mSharedPreferences.getBoolean(key, false);
    }

    public static boolean getBoolean(String key, boolean def) {
        return mSharedPreferences.getBoolean(key, def);
    }

    public static Set<String> getSet(String key) {
        return mSharedPreferences.getStringSet(key, new HashSet<String>());
    }

    public static boolean remove(String key) {
        return mSharedPreferences.edit().remove(key).commit();
    }

    public static boolean contains(String key) {
        return mSharedPreferences.contains(key);
    }


}
