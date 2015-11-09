package com.android.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.ImageView;

public class MyImageView extends ImageView {

    public MyImageView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
