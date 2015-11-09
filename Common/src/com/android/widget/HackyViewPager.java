package com.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 作者：lr on 2015/7/29 13:47
 */
public class HackyViewPager extends VerticalViewPager {
    public HackyViewPager(Context context) {
        super(context);
    }

    public HackyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

       private boolean mIsDisallowIntercept = false;

       @Override
       public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
           // keep the info about if the innerViews do requestDisallowInterceptTouchEvent
           mIsDisallowIntercept = disallowIntercept;
           super.requestDisallowInterceptTouchEvent(disallowIntercept);
       }

       @Override
       public boolean dispatchTouchEvent(MotionEvent ev) {
           // the incorrect array size will only happen in the multi-touch scenario.
//           if (ev.getPointerCount() > 1 && mIsDisallowIntercept) {
//               requestDisallowInterceptTouchEvent(false);
//               boolean handled = super.dispatchTouchEvent(ev);
//               requestDisallowInterceptTouchEvent(true);
//               return handled;
//           } else {
//               return super.dispatchTouchEvent(ev);
//           }
           return super.dispatchTouchEvent(ev);
       }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
