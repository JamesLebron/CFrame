package com.android.base;


import com.android.http.base.CustomMultipartEntity;
import com.android.http.base.DownLoadListener;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by exception on 15/10/30.
 */
public class BaseTask {
    public Map<Object, Object> params = new HashMap<>();
    public TaskListener listener;
    public int id;
    public String method;

    public Class invokeClass;//调用类型
    public Class deserializationClass;//反序列化类型

    public boolean needSyncTask = false;
    public Object invokeObj;//调用者


    public BaseTask(int id, String method, Class invokeClass, Class deserializationClass, TaskListener listener) {
        init(id, method, invokeClass, deserializationClass, listener);
    }

    public BaseTask(int id, String method, Class invokeClass, TaskListener listener) {
        init(id, method, invokeClass, null, listener);
    }

    public void init(int id, String method, Class invokeClass, Class deserializationClass, TaskListener listener) {
        this.id = id;
        this.method = method;
        this.listener = listener;
        this.invokeClass = invokeClass;
        this.deserializationClass = deserializationClass;
    }


    public void addParam(Object key, Object value) {
        this.params.put(key, value);
    }

    public void removeParam(Object key) {
        if (params.containsKey(key))
            params.remove(key);
    }

    /**
     * 文件下载实体类
     */
    public static class DownloadEntity {
        public String fileName, path;
        public DownLoadListener downLoadListener;
    }

    /**
     * 文件上传实体类
     */
    public static class UpLoadEntity {
        public List<FileNameViluePar> fileNameViluePars;
        public CustomMultipartEntity.ProgressListener progressListener;
    }

    /**
     * 文件上传表单类型
     */
    public static class FileNameViluePar {
        public String name;
        public File par;

        public FileNameViluePar(String name, File par) {
            this.name = name;
            this.par = par;
        }
    }


    public enum HTTPMODEL {
        String,
        JSON,
        JSONArray,
        Bitmap,
        XML
    }
}
