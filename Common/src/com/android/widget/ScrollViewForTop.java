package com.android.widget;

/**
 * Created by User on 2015/7/9.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;


/**
 * Created by lr on 2015/6/17.
 */
public class ScrollViewForTop extends ScrollView {

    public interface OnScroll {
        public void onScrollChanged(ScrollViewForTop scrollView, int x, int y, int oldx, int oldy);
    }

    private OnScroll onScroll;

    public ScrollViewForTop(Context context) {
        super(context);
    }

    public ScrollViewForTop(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollViewForTop(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
    }

    public void setOnScroll(OnScroll onScroll) {
        this.onScroll = onScroll;
    }

    private float x;
    private int mEvents;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                x = event.getRawY();
                mEvents = 0;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_POINTER_UP:
                mEvents = -1;
                break;
            case MotionEvent.ACTION_MOVE: {
                if ((getChildAt(0).getMeasuredHeight() <= (getScrollY() + getHeight())) && mEvents == 0) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                    //获得 VerticalViewPager 的实例
                    ((View) getParent()).onTouchEvent(event);
                } else
                    mEvents++;
            }
            break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }

        try {
            return super.dispatchTouchEvent(event);
        } catch (Exception ex) {
            ex.printStackTrace();
            return true;
        }

    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (onScroll != null) {
            onScroll.onScrollChanged(this, l, t, oldl, oldt);
        }
    }
}

