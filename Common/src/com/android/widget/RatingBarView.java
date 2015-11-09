package com.android.widget;

/**
 * Created by User on 2015/7/14.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.common.R;

import java.util.ArrayList;
import java.util.List;

public class RatingBarView extends LinearLayout {
    private List<ImageView> mStars = new ArrayList<ImageView>();
    private OnRatingListener onRatingListener;
    private Object bindObject;
    private float starImageSize;
    private int starCount;
    private float ratingCount;
    private Boolean isClickable;
    private Drawable starEmptyDrawable;
    private Drawable starFillDrawable;
    private Drawable starHalfDrawable;

    public void setStarFillDrawable(Drawable starFillDrawable) {
        this.starFillDrawable = starFillDrawable;
    }

    public void setStarEmptyDrawable(Drawable starEmptyDrawable) {
        this.starEmptyDrawable = starEmptyDrawable;
    }

    public void setStarHalfDrawable(Drawable starHalfDrawable) {
        this.starHalfDrawable = starHalfDrawable;
    }

    public void setStarCount(int startCount) {
        this.starCount = starCount;
    }

    public void setratingCount(float ratingCount) {
        this.ratingCount = ratingCount;
        setStar(ratingCount);
    }

    public void setStarImageSize(float starImageSize) {
        this.starImageSize = starImageSize;
    }

    private int startCount;


    public void setBindObject(Object bindObject) {
        this.bindObject = bindObject;
    }

    public void setOnRatingListener(OnRatingListener onRatingListener) {
        this.onRatingListener = onRatingListener;
    }

    public RatingBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.HORIZONTAL);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RatingBarView);// R.styleable.RatingBarViewRatingBarView
        starImageSize = a.getDimension(R.styleable.RatingBarView_starImageSize, 20);
        starCount = a.getInteger(R.styleable.RatingBarView_starCount, 5);
        ratingCount = a.getFloat(R.styleable.RatingBarView_ratingCount, 1.0f);
        isClickable = a.getBoolean(R.styleable.RatingBarView_isClickable, false);
        starEmptyDrawable = a.getDrawable(R.styleable.RatingBarView_starEmpty);
        starFillDrawable = a.getDrawable(R.styleable.RatingBarView_starFill);
        starHalfDrawable = a.getDrawable(R.styleable.RatingBarView_starHalf);

        for (int i = 0; i < starCount; ++i) {
            ImageView imageView = getStarImageView(context, attrs);
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isClickable) {
                        setStar((float)indexOfChild(v) + 1);
                        if (onRatingListener != null) {
                            onRatingListener.onRating(bindObject, indexOfChild(v) + 1);
                        }
                    }
                }
            });

            addView(imageView);
        }
        if (0 != ratingCount) {
            setStar(ratingCount);
        }

    }

    private ImageView getStarImageView(Context context, AttributeSet attrs) {
        ImageView imageView = new ImageView(context);
        ViewGroup.LayoutParams para = new ViewGroup.LayoutParams(Math.round(starImageSize), Math.round(starImageSize));
        imageView.setLayoutParams(para);
        imageView.setPadding(0, 0, 5, 0);
        imageView.setImageDrawable(starEmptyDrawable);
        imageView.setMaxWidth(10);
        imageView.setMaxHeight(10);
        return imageView;

    }

    public void setStar(float starCount) {
        starCount = starCount > this.starCount ? this.starCount : starCount;
        starCount = starCount < 0 ? 0 : starCount;
        //TODO
        int mark = 0;
        for (int i = 0; i < (int) starCount; ++i) {
            ((ImageView) getChildAt(i)).setImageDrawable(starFillDrawable);
        }

        for (int i = this.starCount - 1; i >= (int) starCount; --i) {
            ((ImageView) getChildAt(i)).setImageDrawable(starEmptyDrawable);
            Log.i("star >>> :", i + "<<<<<<< ");
            mark = i;
        }

        if (ratingCount - mark == 0.5) {
            ((ImageView) getChildAt(mark)).setImageDrawable(starHalfDrawable);
        } else if (ratingCount - mark == 0 && mark != 0) {
            ((ImageView) getChildAt(mark)).setImageDrawable(starEmptyDrawable);
        }
    }


    /**
     * 该监听器用于监听选中Tab时View的变化
     */
    public interface OnRatingListener {

        void onRating(Object bindObject, int RatingScore);

    }
}

