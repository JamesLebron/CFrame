package com.android.base;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.http.AndroidHttpClient;
import android.os.Build;

import com.android.http.base.CustomMultipartEntity;
import com.android.http.base.DownLoadListener;
import com.android.http.volley.cache.CFrameCache;
import com.android.util.Logs;
import com.android.volley.AuthFailureError;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by exception on 15/10/30.
 */
public class BaseVolleyHttpClient {
    private ObjectMapper mapper;
    private static BaseVolleyHttpClient volleyHttpClient;
    private RequestQueue queue;

    private static CFrameCache cache;
    /**
     * Default on-disk cache directory.
     */
    private static final String DEFAULT_CACHE_DIR = "volley";

    private BaseVolleyHttpClient(RequestQueue queue) {
        this.queue = queue;
    }

    public static BaseVolleyHttpClient init(RequestQueue queue) {
        if (volleyHttpClient == null)
            volleyHttpClient = new BaseVolleyHttpClient(queue);
        if (volleyHttpClient.mapper == null)
            volleyHttpClient.mapper = new ObjectMapper();
        return volleyHttpClient;
    }


    public static void httpGet(HttpTask task) {
        volleyHttpClient.http(Request.Method.GET, task);
    }

    public static void httpPost(HttpTask task) {
        volleyHttpClient.http(Request.Method.POST, task);
    }


    public void http(int type, final HttpTask task) {
        String url = task.url;
        if (url == null) {
            url = ((String) task.params.get("url"));
            task.params.remove(url);
        }
        if (HttpTask.httpmodel == BaseTask.HTTPMODEL.JSON) {
            httpJson(type, url, task);
        } else if (HttpTask.httpmodel == BaseTask.HTTPMODEL.String) {
            httpString(type, url, task);
        } else if (HttpTask.httpmodel == BaseTask.HTTPMODEL.Bitmap) {
            httpBitmap(url, task);
        } else if (HttpTask.httpmodel == BaseTask.HTTPMODEL.JSONArray) {
            httpJsonArray(url, task);
        }
    }

    /**
     * @param maxWidth  Integer
     * @param maxHeight Integer
     * @param config    Bitmap.Config
     * @param url
     * @param task      onSecuss callback return a bitmap
     */
    public void httpBitmap(String url, final HttpTask task) {
        int maxWidth = task.params.containsKey("maxWidth") ? ((Integer) task.params.get("maxWidth")) : 0;
        int maxHeight = task.params.containsKey("maxHeight") ? ((Integer) task.params.get("maxHeight")) : 0;
        Bitmap.Config config = task.params.containsKey("config") ? ((Bitmap.Config) task.params.get("config")) : null;
        ImageRequest request = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
                if (task.listener != null)
                    task.listener.onComplate(task, null, bitmap);
            }
        }, maxWidth, maxHeight, config, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (task.listener != null)
                    task.listener.onComplate(task, volleyError, null);
            }
        });
        queue.add(request);
    }

    /**
     * @param url
     * @param task return a list or a JSONArray
     */
    public void httpJsonArray(String url, final HttpTask task) {
        JsonArrayRequest request = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                try {
                    JavaType type1 = getCollectionType(ArrayList.class, task.deserializationClass);
                    ArrayList list = (ArrayList) mapper.readValue(jsonArray.toString(), type1);
                    if (task.listener != null)
                        task.listener.onComplate(task, null, list);
                } catch (Exception e) {
                    e.printStackTrace();
                    if (task.listener != null) {
                        task.listener.onComplate(task, null, jsonArray);
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (task.listener != null) {
                    task.listener.onComplate(task, volleyError, null);
                }
            }
        });
        queue.add(request);
    }

    /**
     * * 获取泛型的Collection Type
     * * @param collectionClass 泛型的Collection
     * * @param elementClasses 元素类
     * * @return JavaType Java类型
     * * @since 1.0
     */
    public JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

    /**
     * @param type http type {post ,put ...}
     * @param url
     * @param task return a class or JSONObject
     */
    public void httpJson(int type, String url, final HttpTask task) {
        JSONObject json = null;
        if (task.params.containsKey("json_object")) {
            String str = (String) task.params.get("json_object");
            try {
                json = new JSONObject(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            json = new JSONObject(task.params);


        JsonObjectRequest request = new JsonObjectRequest(type, url, json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Object obj = null;
                if (task.deserializationClass != null && !task.deserializationClass.equals(String.class)) {
                    try {
                        obj = mapper.readValue(response.toString(), task.deserializationClass);
//                        obj = JSON.parseObject(response,clazz);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    obj = response;
                }
                Logs.w("success", task.toString());
                if (task.listener != null) {
                    task.listener.onComplate(task, null, obj);
                }
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (task.listener != null)
                    task.listener.onComplate(task, error, null);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return super.getHeaders();
            }
        };
        queue.add(request);
    }

    /**
     * @param type
     * @param url
     * @param task
     */
    public void httpString(int type, String url, final HttpTask task) {
        StringBuffer params = new StringBuffer(url);
        params.append("?");
        if (task.params != null && task.params.size() > 0) {
            Iterator it = task.params.keySet().iterator();
            while (it.hasNext()) {
                Object key = it.next();
                Object value = task.params.get(key);
                params.append(key.toString());
                params.append("=");
                params.append(value.toString());
                if (it.hasNext())
                    params.append("&");
            }
        }


        final StringRequest request = new StringRequest(type, params.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                onSucessRet(task, s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (task.listener != null)
                    task.listener.onComplate(task, volleyError, null);
            }
        });

        queue.add(request);
    }

    private void onSucessRet(HttpTask task, String ret) {
        Object obj = null;
        if (task.deserializationClass != null && !task.deserializationClass.equals(String.class)) {
            try {
                obj = mapper.readValue(ret, task.deserializationClass);
//                        obj = JSON.parseObject(response,clazz);
                if (task.listener != null) {
                    task.listener.onComplate(task, null, obj);
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (task.listener != null)
                    task.listener.onComplate(task, e, null);
            }
        } else {
            if (task.listener != null) {
                task.listener.onComplate(task, null, ret);
            }
        }

    }


    /**
     * get a RequestQueue
     *
     * @param context
     * @param stack
     * @return
     */
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
     * <br></br>
     * <br>Download File ,</br>
     * <br>It used HttpUrlConnection  connect a stream to read bytes from server and write bytes to local file</br>
     *
     * @param url
     * @param fileName
     * @param path
     * @param listener
     * @throws Exception
     */
    public static synchronized void downLoad(String url, String fileName, String path, DownLoadListener listener) throws Exception {
        System.out.println("HttpUtil.downLoad() ====> " + "url : " + url + " \n fileName :" + fileName + " \n path:" + path);
        URLConnection conn = new URL(url).openConnection();
        int length = conn.getContentLength();
        InputStream in = conn.getInputStream();
        File pathDir = new File(path);
        if (!pathDir.exists()){
            pathDir.mkdir();
        }
        File f = null;
        if (fileName != null)
            f = new File(path + "/" + fileName);
        else
            f = new File((path + "/" + url.substring(url.lastIndexOf("/") + 1)));
        if (!f.exists()) {
            f.createNewFile();
        }
        FileOutputStream out = new FileOutputStream(f);

        byte[] buffer = new byte[4096];
        int count = 0;
        int seek = 0;
        int lastSeek = 0;
        if (listener != null) {
            listener.onstart();
        }
        while ((count = in.read(buffer)) > 0) {
            out.write(buffer, 0, count);
            if (seek == 0 && listener != null) {
                listener.onSeekChange(0);
            }
            if (listener != null) {
                seek += count;
                int a = (int) ((double) seek / length * 100);
                if (a > lastSeek) {
                    lastSeek = a;
                    listener.onSeekChange(a);
                }
            }
        }
        if (listener != null)
            listener.onComplate();
        in.close();
        out.close();
    }


    @SuppressWarnings("deprecation")
    public static synchronized String postFiles(String url, List<HttpTask.FileNameViluePar> parNameAndFiles, Map<Object, Object> params, CustomMultipartEntity.ProgressListener listener) throws Exception {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        CustomMultipartEntity multipartContent = new CustomMultipartEntity(listener);
        //add file pars
        for (HttpTask.FileNameViluePar parNameAndFile : parNameAndFiles) {
            multipartContent.addPart(parNameAndFile.name, new FileBody(parNameAndFile.par, ContentType.create("application/octet-stream", Consts.UTF_8)));
        }

        //add params to post
        Iterator<Object> pk = params.keySet().iterator();
        while (pk.hasNext()) {
            Object key = pk.next();
            Object par = params.get(key);
            multipartContent.addPart(key + "", new StringBody(par + "", ContentType.create("text/plain", Consts.UTF_8)));
        }
        listener.setContentLength(multipartContent.getContentLength());
        httpPost.setEntity(multipartContent);
        // 发起请求 并返回请求的响应
        HttpResponse response = httpClient.execute(httpPost);
        if (response.getStatusLine().getStatusCode() == 200) {
            HttpEntity resEntity = response.getEntity();
            if (resEntity != null) {
                String str = EntityUtils.toString(resEntity, "utf-8");
                return str;
            }
        }
        return null;
    }


}
