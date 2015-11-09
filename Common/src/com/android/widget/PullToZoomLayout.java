package com.android.widget;

/**
 * Created by lr on 2015/5/23.
 */

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.base.BaseApplication;
import com.android.common.R;
import com.android.bitmap.CBitmap;
import com.android.bitmap.core.BitmapDisplayConfig;
import com.android.bitmap.display.Displayer;
import com.enrique.stackblur.StackBlurManager;

import java.lang.reflect.Field;

public class PullToZoomLayout extends PullBase {
    private RelativeLayout headerView;
    private int headerHeight;
    private int maxHeight;
    private int currentHeight;
    private float downY = 0.0f;
    private boolean zooming = false;
    private int minHeight;
    private StackBlurManager blurManager;
    private ImageView m_img1;
    private ImageView m_img2;
    private boolean isFirstPull = true;
    private int firstPullAlpha;
    private boolean isTop;
    private float first = 0;
    private boolean isUp;
    private boolean isFirst = false;

    public PullToZoomLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.pull); //
        int layout = a.getResourceId(R.styleable.pull_header, 0);
        maxHeight = a.getLayoutDimension(R.styleable.pull_maxHeaderHeight, 0);
        minHeight = a.getLayoutDimension(R.styleable.pull_minHeaderHeight, 0);
        a.recycle();
        if (layout == 0) {
            throw new RuntimeException("PullToZoomLayout haven't header view.");
        }
        if (maxHeight == 0) {
            throw new RuntimeException("PullToZoomLayout maxHeight must be set.");
        }
        headerView = (RelativeLayout) LayoutInflater.from(context).inflate(layout, null);
        addHeaderView(headerView, minHeight);
        headerHeight = getHeaderHeight();
        currentHeight = headerHeight;
        headerShowing = true;
    }

    public void loadingHeadImg(String url) {
        CBitmap bitmap = (CBitmap) BaseApplication.app.cache.get("cBitmap");
        bitmap.configDisplayer(new Displayer() {
            @Override
            public void loadCompletedisplay(View imageView, Bitmap bitmap, BitmapDisplayConfig config) {
                m_img1 = (ImageView) headerView.getChildAt(0);
                m_img2 = (ImageView) headerView.getChildAt(1);
                m_img1.setImageBitmap(bitmap);
                blurManager = new StackBlurManager(getBitMapCache(m_img1));//getBitMapCache
                setLayoutBlur(m_img2, 15);
            }

            @Override
            public void loadFailDisplay(View imageView, Bitmap bitmap) {
                //加载失败
            }
        });
        bitmap.display(headerView.getChildAt(0), url);
    }

    private Bitmap getBitMapCache(ImageView contentLayout) {
        contentLayout.setDrawingCacheEnabled(true);
        contentLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        contentLayout.layout(0, 0, contentLayout.getMeasuredWidth(),
                contentLayout.getMeasuredHeight());

        contentLayout.buildDrawingCache();
        Bitmap bitmap = contentLayout.getDrawingCache();
        return bitmap;
    }

    private int mEvents;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                first = event.getY();
                isTop = (measureStateBar() - ofterObtainLoaction()) == 0;
                mEvents = 0;
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_POINTER_UP:
                mEvents = -1;
                break;
            case MotionEvent.ACTION_MOVE:
                isUp = event.getY() - first > 5;
                if (mEvents == 0 && isTop && isUp) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    computeTravel(event, false);
                    downY = event.getY();
                } else if (mEvents == 0 && !isUp) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                computeTravel(event, true);
                break;
        }
        return true;
    }

    /**
     * 通过反射计算状态栏的高度
     */
    public int measureStateBar() {
        Class<?> c;
        Object obj;
        Field field;
        int x = 0, sbar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sbar;
    }

    /**
     * 时刻计算ListView当前处于的位置
     */
    private int ofterObtainLoaction() {
        final int[] location = new int[2];
        getLocationInWindow(location);
        int loactionY = location[1];
        return loactionY;
    }

    /**
     * 计算并调整header显示的高度
     *
     * @param ev
     * @param actionUp
     */
    private void computeTravel(MotionEvent ev, boolean actionUp) {
        float movingY = ev.getY();
        int travel = (int) (downY - movingY);
        boolean up = travel > 0;
        travel = Math.abs(travel);
        move(travel, up, actionUp);
    }

    public void move(int distance, boolean upwards, boolean release) {
        // illegal distance
        if (distance > 450) return;

        if (release) {
            // zooming
            if (headerView.getHeight() > headerHeight) {
                AnimUtil.collapse(headerView, headerHeight, m_img2);
                currentHeight = headerHeight;
            }
            zooming = false;
            return;
        } else {
            zooming = true;
            resizeHeader(distance, upwards);
        }
    }

    private void resizeHeader(int distance, boolean upwards) {
        distance = (int) (distance / 1.5f);
        // zoom out
        if (upwards && headerView.getHeight() > headerHeight) {
            int tmpHeight = currentHeight - distance;
            if (tmpHeight < headerHeight) {
                tmpHeight = headerHeight;
            }
            currentHeight = tmpHeight;
            resizeHeight(currentHeight);
        }
        if (!upwards && headerView.getHeight() >= headerHeight) {
            // zoom in
            currentHeight += distance;
            if (currentHeight > maxHeight) {
                currentHeight = maxHeight;
            }
            resizeHeight(currentHeight);
        }
    }

    private void resizeHeight(int resizeHeight) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) headerView.getLayoutParams();
        if (params == null) {
            params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, resizeHeight);
        } else {
            params.height = resizeHeight;
        }
        m_img2 = (ImageView) headerView.getChildAt(1);
        if (isFirstPull) {
            firstPullAlpha = (resizeHeight / (maxHeight / 255));
        }
        isFirstPull = false;
        int alpha = (resizeHeight / (maxHeight / 255)) - firstPullAlpha;
        if (alpha > 255)
            alpha = 255;
        else if (alpha < 0)
            alpha = 0;
        m_img2.setAlpha(255 - alpha);
        headerView.setLayoutParams(params);
    }

    private void setLayoutBlur(final ImageView view, final int radius) {
        if (blurManager != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Bitmap bitmap = blurManager.process(radius);
                    Activity activity = (Activity) getContext();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            view.setImageBitmap(bitmap);
                        }
                    });
                }
            }).start();
        }
    }

    protected boolean isHeaderZooming() {
        return zooming;
    }
}

