package com.android.widget;

import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by lr on 2015/6/17.
 */
public class VerticalViewPagerAdapter extends PagerAdapter {
    private List<View> mViews;

    public VerticalViewPagerAdapter(List<View> views) {
        this.mViews = views;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public void destroyItem(ViewGroup container, int position,
                            Object object) {
        super.destroyItem(container, position, object);
        container.removeView(mViews.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mViews.get(position));
        return mViews.get(position);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
