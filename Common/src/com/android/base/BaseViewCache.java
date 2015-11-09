package com.android.base;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by exception on 15/10/23.
 */
public class BaseViewCache {
    private Map<Object, View> viewMap = new HashMap<>();
    private View layout;

    public BaseViewCache(View layout) {
        this.layout = layout;
    }


    public void setLayout(View layout) {
        this.layout = layout;
    }

    public void addView(int id) {
        View t = layout.findViewById(id);
        viewMap.put(id, t);
    }

    public void addView(View layout, int id) {
        View v = layout.findViewById(id);
        viewMap.put(id, v);
    }

    public <T extends View> T getViewById(int id) {
        if (!viewMap.containsKey(id))
            addView(id);
        return ((T) viewMap.get(id));
    }

    public <T extends View> T getViewById(View view, int id) {
        T v;
        if (viewMap.containsKey(id)) {
            v = ((T) viewMap.get(id));

        } else {
            v = ((T) view.findViewById(id));
        }
        return v;
    }

    public <T extends TextView> void setText(int id, String value) {
        if (viewMap.containsKey(id)) {
            T t = getViewById(id);
            t.setText(value);
        }
    }

    public void setImageOrBackgroundRes(int id, int drawableId) {
        View v = getViewById(id);
        if (v instanceof ImageView) {
            ((ImageView) v).setImageResource(drawableId);
        } else {
            v.setBackgroundResource(drawableId);
        }
    }

    public <T extends ImageView> void setImageBitmap(int id, Bitmap bitmap) {
        T v = getViewById(id);
        v.setImageBitmap(bitmap);
    }

    public void setViewBackgroundDrawable(int id, Drawable drawable) {
        getViewById(id).setBackgroundDrawable(drawable);
    }


    public <T extends SimpleDraweeView> void setImageUri(int id, String uri) {
        T t = getViewById(id);
        t.setImageURI(Uri.parse(uri));
    }


    public void setOnclickListener(int id, View.OnClickListener listener) {
        if (viewMap.containsKey(id)) {
            View t = getViewById(id);
            t.setOnClickListener(listener);
        } else {
            getViewById(id).setOnClickListener(listener);
        }
    }

}
