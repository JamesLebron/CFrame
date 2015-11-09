package com.android.util;

public interface DownLoadListener {
    void onstart();

    void onSeekChange(int seek);//进度  百分比

    void onComplate();
}
