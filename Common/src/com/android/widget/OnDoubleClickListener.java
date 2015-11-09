package com.android.widget;

import android.view.MotionEvent;
import android.view.View;

public abstract class OnDoubleClickListener implements View.OnTouchListener {
    int count = 0;
    long firClick = 0;
    long secClick = 0;

    @Override
    public boolean onTouch(View v, MotionEvent event) {


        if (MotionEvent.ACTION_DOWN == event.getAction()) {
            count++;
            if (count == 1) {
                firClick = System.currentTimeMillis();

            } else if (count >= 2) {
                secClick = System.currentTimeMillis();
                if (secClick - firClick < 1000) {
                    // 双击事件
                    onDoubleClick(v);
                }
                count = 0;
                firClick = 0;
                secClick = 0;

            }
        }
        return true;
    }

    public abstract void onDoubleClick(View v);

}
