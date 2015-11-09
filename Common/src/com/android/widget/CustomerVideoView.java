package com.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class CustomerVideoView extends VideoView {
    private int w, h;

    public CustomerVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CustomerVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomerVideoView(Context context) {
        super(context);
    }

    /*
     * 视频拉伸全屏显示
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // int width = getDefaultSize(getWidth(), widthMeasureSpec);
        // int height = getDefaultSize(getHeight(), heightMeasureSpec);
        //Log.w("onMeasure", getWidth() + "    " + getHeight());
        setMeasuredDimension(getW(), getH());
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

}
