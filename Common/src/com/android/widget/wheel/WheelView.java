package com.android.widget.wheel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.TextView;

import com.android.common.R;

public class WheelView extends TosGallery {
    /**
     * The selector.
     */
    private Drawable mSelectorDrawable = null;

    /**
     * The bound rectangle of selector.
     */
    private Rect mSelectorBound = new Rect();

    /**
     * The top shadow.
     */
    private GradientDrawable mTopShadow = null;

    /**
     * The bottom shadow.
     */
    private GradientDrawable mBottomShadow = null;

    /**
     * Shadow colors
     * 0xFF111111, 0x00AAAAAA, 0x00AAAAAA
     */
    private static final int[] SHADOWS_COLORS = {0xFFFFFF, 0xFFFFFF, 0xFFFFFF};

    /**
     * The constructor method.
     *
     * @param context
     */
    public WheelView(Context context) {
        super(context);

        initialize(context);
    }

    /**
     * The constructor method.
     *
     * @param context
     * @param attrs
     */
    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initialize(context);
    }

    /**
     * The constructor method.
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    public WheelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initialize(context);
    }

    /**
     * Initialize.
     *
     * @param context
     */
    private void initialize(Context context) {
        this.setVerticalScrollBarEnabled(false);
        this.setSlotInCenter(true);
        this.setOrientation(TosGallery.VERTICAL);
        this.setGravity(Gravity.CENTER_HORIZONTAL);
        this.setUnselectedAlpha(1.0f);

        // This lead the onDraw() will be called.
        this.setWillNotDraw(false);

        // The selector rectangle drawable.
        this.mSelectorDrawable = getContext().getResources().getDrawable(R.drawable.wheel_val);
        this.mTopShadow = new GradientDrawable(Orientation.TOP_BOTTOM, SHADOWS_COLORS);
        this.mBottomShadow = new GradientDrawable(Orientation.BOTTOM_TOP, SHADOWS_COLORS);

        // The default background.
        this.setBackgroundResource(R.drawable.wheel_bg);

        // Disable the sound effect default.
        this.setSoundEffectsEnabled(false);
    }

    /**
     * Called by draw to draw the child views. This may be overridden by derived classes to gain
     * control just before its children are drawn (but after its own view has been drawn).
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
//        mSelectorBound
        Paint paint = new Paint();
        paint.setColor(0x70333333);


        int width = mSelectorBound.width();
        int height = mSelectorBound.height();

//        canvas.drawLine(0, 0, 0, height, paint);
        canvas.drawLine(0, mSelectorBound.top - 2, width, mSelectorBound.top - 2, paint);
        canvas.drawLine(0, mSelectorBound.bottom - 2, width, mSelectorBound.bottom - 2, paint);
//        canvas.drawLine(width - 1, height -1, width - 1, 0, paint);
//        super.onDraw(canvas);
//        canvas.drawLine();
        // After draw child, we do the following things:
        // +1, Draw the center rectangle.
        // +2, Draw the shadows on the top and bottom.

//        drawCenterRect(canvas);

//        drawShadows(canvas);
    }

    /**
     * setOrientation
     */
    @Override
    public void setOrientation(int orientation) {
        if (TosGallery.HORIZONTAL == orientation) {
            throw new IllegalArgumentException("The orientation must be VERTICAL");
        }

        super.setOrientation(orientation);
    }

    /**
     * Call when the ViewGroup is layout.
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        int galleryCenter = getCenterOfGallery();
        View v = this.getChildAt(0);

        int height = (null != v) ? v.getMeasuredHeight() : 50;
        int top = galleryCenter - height / 2;
        int bottom = top + height;

        mSelectorBound.set(getPaddingLeft(), top, getWidth() - getPaddingRight(), bottom);
    }

    /**
     */
    @Override
    protected void selectionChanged() {
        super.selectionChanged();

        playSoundEffect(SoundEffectConstants.CLICK);
    }

    /**
     * Draw the selector drawable.
     *
     * @param canvas
     */
    private void drawCenterRect(Canvas canvas) {
        if (null != mSelectorDrawable) {
            mSelectorDrawable.setBounds(mSelectorBound);
            mSelectorDrawable.draw(canvas);
        }
    }

    /**
     * Draw the shadow
     *
     * @param canvas
     */
    private void drawShadows(Canvas canvas) {
        /*
        int height = (int) (2.0 * mSelectorBound.height());
        mTopShadow.setBounds(0, 0, getWidth(), height);
        mTopShadow.draw(canvas);

        mBottomShadow.setBounds(0, getHeight() - height, getWidth(), getHeight());
        mBottomShadow.draw(canvas);
        */
    }

    private TextView last;
    private float size = 17;
    private float topSize = 15;

    @Override
    void setNextSelectedPositionInt(int position) {
//        getCenterOfGallery();
        super.setNextSelectedPositionInt(position);
//        Logs.w("mSelectedChild", mSelectedChild + " " + size);
        if (last != null && mSelectedChild != null && last.equals(mSelectedChild)) {
            TextView txt = (TextView) mSelectedChild;
            txt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
            txt.postInvalidate();
            return;
        }

        if (last != null)
            last.setTextSize(topSize);
        if (mSelectedChild != null && mSelectedChild instanceof TextView) {
            TextView txt = (TextView) mSelectedChild;
            if (size == -1)
                size = txt.getTextSize() / 2;

            txt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
            txt.postInvalidate();
            last = txt;
        }
//        Logs.w("getChildAt(position) instanceof TextView" , (getChildAt(position)) +" ");
//        Logs.w("setNextSelectedPositionInt : ", position + " ... ");
//        Logs.w("getSelectedView()" ,getChildAt(position) == null ? "null" :getChildAt(position).getTag() + " ..");
    }
}
