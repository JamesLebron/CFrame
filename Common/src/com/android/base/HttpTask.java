package com.android.base;

import java.util.Map;

/**
 * Created by exception on 15/10/30.
 */
public class HttpTask extends BaseTask {

    public String url;//网络请求的url
    public DownloadEntity downloadEntity;//下载文件的实体配置
    public UpLoadEntity upLoadEntity;//上传文件的实体配置

    public static HTTPMODEL httpmodel = HTTPMODEL.JSON; //http 请求的返回值模型，可以是Json/string 等
    private static HttpTask task;
    private boolean isComplate = true;


    public HttpTask(int id, String method, Class deserializationClass, TaskListener listener) {
        super(id, method, HttpService.class, deserializationClass, listener);
    }

    public HttpTask(int id, String method, Class deserializationClass, TaskListener listener, String url) {
        super(id, method, HttpService.class, deserializationClass, listener);
        this.url = url;
    }

    public HttpTask(int id, String method, Class deserializationClass, TaskListener listener, String url, Map<Object, Object> params) {
        super(id, method, HttpService.class, deserializationClass, listener);
        this.url = url;
        this.params.putAll(params);
    }


    public static HttpTask optTask(int id, String method, Class deserializationClass, TaskListener listener, String url) {
        if (task != null && task.isComplate) {
            task.init(id, method, HttpService.class, deserializationClass, listener);
            task.url = url;
            task.params.clear();
        } else
            task = new HttpTask(id, method, deserializationClass, listener, url);
        return task;
    }




    @Override
    public void init(int id, String method, Class invokeClass, Class deserializationClass, TaskListener listener) {
        super.init(id, method, invokeClass, deserializationClass, listener);
        this.invokeObj = HttpService.getInstance();
        if (!HttpService.httpGet.equals(method) || !HttpService.httpPost.equals(method))
            needSyncTask = true;
        else needSyncTask = false;
    }

    public static void setHttpmodel(HTTPMODEL httpmodel) {
        HttpTask.httpmodel = httpmodel;
    }

    public void setIsComplate(boolean isComplate) {
        this.isComplate = isComplate;
    }

    public interface Method {
        int DEPRECATED_GET_OR_POST = -1;
        int GET = 0;
        int POST = 1;
        int PUT = 2;
        int DELETE = 3;
        int HEAD = 4;
        int OPTIONS = 5;
        int TRACE = 6;
        int PATCH = 7;
        int DOWNLOAD_FILE = 8;
        int UPLOAD_FILE = 9;
    }
}
