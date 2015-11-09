package com.android.http.base;

public interface DownLoadListener {
    void onstart();

    void onSeekChange(int seek);//进度  百分比

    void onComplate();
}
