package com.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by lr on 2015/6/17.
 */
public class ScrollViewForBottom extends RelativeLayout {
    private float mLastMotionY = 0;
    private int mEvents;

    public ScrollViewForBottom(Context context) {
        super(context);
    }

    public ScrollViewForBottom(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * @param direction -1 页面从上往下走。
     * @return
     */
    public boolean canScrollVertical(int direction) {
        final int offset = computeVerticalScrollOffset();
        final int range = computeVerticalScrollRange() - computeVerticalScrollExtent();
        if (range == 0) return false;
        else return (direction < 0) ? (offset > 0) : (offset < range - 1);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = event.getY();
                getParent().requestDisallowInterceptTouchEvent(true);
                mEvents = 0;
                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_POINTER_DOWN:
                mEvents = -1;
                break;
            case MotionEvent.ACTION_MOVE: {
                float direction = mLastMotionY - event.getY();
                mLastMotionY = event.getY();
                if ((getScrollY() == 0 && direction < 0) && mEvents == 0) {
                    //获得VerticalViewPager的实例
                    getParent().requestDisallowInterceptTouchEvent(false);
                    ((View) getParent().getParent().getParent()).onTouchEvent(event);
                } else {
                    mEvents++;
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
            }
            break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return super.dispatchTouchEvent(event);
    }
}
