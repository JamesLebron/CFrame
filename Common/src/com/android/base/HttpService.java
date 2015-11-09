package com.android.base;

import android.os.Bundle;

/**
 * Created by exception on 15/10/31.
 */
public class HttpService extends BaseService {
    private static HttpService httpService;

    public static final String httpGet = "httpGet";
    public static final String httpPost = "httpPost";
    public static final String httpDownloadFile = "httpDownloadFile";
    public static final String httpPostFiles = "httpPostFiles";

    public HttpService() {

    }

    public HttpService init(){
        return   getInstance();
    }

    public static HttpService getInstance() {
        if (httpService == null)
            httpService = new HttpService();
        return httpService;
    }

    public  static HttpService newInstance() {
        return getInstance();
    }

    public void httpGet(HttpTask task) {
        BaseVolleyHttpClient.httpGet(task);
    }

    public void httpPost(HttpTask task) {
        BaseVolleyHttpClient.httpPost(task);
    }


    public void httpDownloadFile(HttpTask task) throws Exception {
        if (task.downloadEntity != null && task.downloadEntity.downLoadListener != null) {
            BaseVolleyHttpClient.downLoad(task.url, task.downloadEntity.fileName, task.downloadEntity.path, task.downloadEntity.downLoadListener);
            task.listener.onComplate(task, null,"{\"ret\":\"success\"}");
        } else {
            throw new Exception("please set download entity or downLoadListener!");
        }
    }

    public void httpPostFiles(HttpTask task) throws Exception {
        if (task.upLoadEntity != null && task.upLoadEntity.progressListener != null) {
            BaseVolleyHttpClient.postFiles(task.url, task.upLoadEntity.fileNameViluePars, task.params, task.upLoadEntity.progressListener);
            task.listener.onComplate(task,null, "{\"ret\":\"success\"}");
        } else {
            throw new Exception("please set upload entity or progressListener");
        }
    }

}
