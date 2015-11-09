package com.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.android.base.ColorPickerListener;
import com.android.common.R;

/**
 * Created by lr on 2015/6/4.
 */

public class IntegrationColorPicker extends RelativeLayout {
    private int mCircleRadius = 5; // 默认圆半径
    private Paint mPaint; // 画笔
    private Boolean isPress; // 是否按下
    private int mLooseColor = 0x00000000; // 松开的默认颜色
    private int mPressColor = 0xFF000000; // 按下的默认颜色
    private int mDrawStyle = 0; // 需要绘制图案的样式 0 默认矩形 1 圆角矩形 2 圆
    private int mRoundRadius = 10; // 圆角矩形半径
    private final int RECTROUND = 1; // 1 圆角矩形
    private final int ROUND = 2; // 2 圆
    private RectF rect; // 绘制的位置大小
    private ColorPickerDrawable mColorPicker_loose; // 按下
    private ColorPickerDrawable mColorPicker_press; // 松开
    private Context mContext; // 上下文
    private ColorPickerListener activity; // 实现ColorPickerListener实例

    public void setActivity(ColorPickerListener activity) {
        this.activity = activity;
    }

    public IntegrationColorPicker(Context context) {
        super(context);
        this.mContext = context;
        activity = (ColorPickerListener) mContext;
        initAll(context, null);
    }


    public IntegrationColorPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        activity = (ColorPickerListener) mContext;
        initAll(context, attrs);

    }

    public IntegrationColorPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        activity = (ColorPickerListener) mContext;
        initAll(context, attrs);
    }

    private void initAll(Context context, AttributeSet attrs) {
        if (attrs == null)
            return;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IntegrationColorPicker);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        mRoundRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mRoundRadius, metrics);
        mCircleRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mCircleRadius, metrics);

        mCircleRadius = a.getDimensionPixelSize(R.styleable.IntegrationColorPicker_circleRadius, mCircleRadius);
        mPressColor = a.getColor(R.styleable.IntegrationColorPicker_pressColor, mPressColor);
        mLooseColor = a.getColor(R.styleable.IntegrationColorPicker_loosenColor, mLooseColor);
        mDrawStyle = a.getInteger(R.styleable.IntegrationColorPicker_drawStyle, mDrawStyle);
        mRoundRadius = a.getDimensionPixelSize(R.styleable.IntegrationColorPicker_roundRadius, mRoundRadius);
        mColorPicker_loose = new ColorPickerDrawable(mLooseColor);
        mColorPicker_press = new ColorPickerDrawable(mPressColor);
        isPress = false;
        setBackgroundDrawable(mColorPicker_loose);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isPress = true;
                setBackgroundDrawable(mColorPicker_press);
                break;
            case MotionEvent.ACTION_UP:
                isPress = false;
                setBackgroundDrawable(mColorPicker_loose);
                activity.onPicker(this);
                break;
        }
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    class ColorPickerDrawable extends Drawable {
        int mColor;
        int mAlpha;

        private ColorPickerDrawable(int color) {
            this.mColor = color;
            this.mAlpha = 255;
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        }

        @Override
        public void draw(Canvas canvas) {
            if (isPress) {
                mPaint.setAlpha(255);
                mPaint.setColor(mPressColor);
            } else {
                mPaint.setAlpha(255);
                mPaint.setColor(mLooseColor);
            }
            mPaint.setStyle(Paint.Style.FILL);
            dispenseDraw(mDrawStyle, canvas, getRectF());
        }

        private void dispenseDraw(int type, Canvas canvas, RectF rectF) {
            switch (type) {
                case RECTROUND:
                    canvas.drawRoundRect(rectF, mRoundRadius, mRoundRadius, mPaint);
                    break;
                case ROUND:
                    canvas.drawCircle(rectF.centerX(), rectF.centerY(), mCircleRadius, mPaint);
                    break;
                default:
                    canvas.drawRect(rectF, mPaint);
                    break;
            }
        }

        private RectF getRectF() {
            final Rect bounds = getBounds();
            if (rect == null)
                rect = new RectF();
            rect.left = bounds.left;
            rect.top = bounds.top;
            rect.right = bounds.right;
            rect.bottom = bounds.bottom;
            return rect;
        }

        @Override
        public void setAlpha(int alpha) {
            this.mAlpha = alpha;
            invalidateSelf();
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            mPaint.setColorFilter(colorFilter);
        }

        @Override
        public int getOpacity() {
            return mPaint.getAlpha();
        }
    }


}
