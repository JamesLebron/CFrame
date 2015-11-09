package com.android.animation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.widget.AutoScrollViewPager;


/**
 * Created by xpx on 2015/4/22.
 */
public class ImageAnimation {
    // 以下为必传参数
    private View hotel_scrollview_details;// 让整个布局透明掉
    private View hotel_hotel_image_translation; //  点击图片后隐藏该图片
    private View hotel_details_image; // 移动的其实是这布局
    private View hotel_image_text; // 移动完毕后显示当前是第几张
    private int windowheight; // 屏幕高度
    private View hotel_top_hotel; // 头部,后用于计算位置
    private View hotel_hotel_image_all_hide; //点击viewpager后把这个布局里面的全部隐藏掉
    private View hotel_imagecount; // 点击viewpager后把hotel_hotel_image_translation里面的hotel_imagecount隐藏掉
    private View hotel_visibility_goneorvisible; // 进入图片详情后点击外面白色也可以关闭动画退出
    private View hotel_viewpager_addorremove; // 移除里面的ViewPager
    // 以下参数不用传
    private ObjectAnimator lucencyopenorcloseanimator; // 透明动画
    private ObjectAnimator translationopeanimator;    // 平移动画
    private AnimatorSet animSet; // 动画集合用于同时开启动画
    private boolean boolanimation = true; // 判断是否在图片详情页面
    private boolean isautoscrollviewpager = false; // 判断是否是AutoScrollViewPager
    private int translationlinearLayoutheight = 0; // 移动布局的高度的一部分
    private int hotel_hotel_image_translationheight; // 平移布局的高度中的一部分
    private int hotel_top_hotelheight; // 平移布局的高度中的一部分
    private boolean calculateensemblehowmanyok = true; // 用来判断是否计算一次过
    private boolean animatorend = false; // 动画是否到最后

    /**
     * 构造函数
     * 注意: 该类有局限性，因为时覆盖在布局上面执行的，所以局限性很大，必须满足下面的要求。传进来的控件监听以写
     * 参数(7个参数)
     *
     * @param hotel_scrollview_details       1.需要透明的布局 --> 图片移动时背景要慢慢透明
     * @param hotel_hotel_image_translation  2. 需要隐藏的布局 --> 原理: 一个控件覆盖在另一个控件上所以单点击图片进入图片详情时，要让前一个控件隐藏，另外一个控件开始平移.
     * @param hotel_details_image            3. 需要平移的布局 --> 让这个布局显示出来平移到中间位置  特别注意 : clickable 要设置为true
     * @param hotel_image_text               4. 图片详情下面的TextView -->由于移动图片时,TextView不需要立即显示，等待动画结束后显示
     * @param windowheight                   5. 屏幕的高度的一般 --> 在oncreate去获取，用于让图片平移到屏幕的中间 记得传过来的是屏幕高度的一半
     * @param hotel_top_hotel                6. 头部的布局 --> 用于让图片平移到屏幕的中间,由于大部分头部都引用了一个布局所以图片详情一般都是在头部的下面，所以还要根据头部高度计算
     * @param hotel_visibility_goneorvisible 7. 包裹着平移布局的布局 -->   推荐这个布局覆盖掉整个屏幕，里面写要平移的布局,平移的布局离这个布局的间距高度为头部的高度 特别注意 :clickable 要设置为true
     */

    public ImageAnimation(View hotel_scrollview_details, View hotel_hotel_image_translation, View hotel_details_image, View hotel_image_text, int windowheight, View hotel_top_hotel, final View hotel_visibility_goneorvisible) {
        this.hotel_scrollview_details = hotel_scrollview_details;
        this.hotel_hotel_image_translation = hotel_hotel_image_translation;
        this.hotel_details_image = hotel_details_image;
        this.hotel_image_text = hotel_image_text;
        this.windowheight = windowheight;
        this.hotel_top_hotel = hotel_top_hotel;
        this.hotel_visibility_goneorvisible = hotel_visibility_goneorvisible;
        animSet = new AnimatorSet();
        animSet.setInterpolator(new LinearInterpolator());
        animSet.setStartDelay(200); //执行动画之前的延迟
        this.hotel_details_image.measure(0, 0);
        translationlinearLayoutheight = this.hotel_details_image.getMeasuredHeight();
        this.hotel_visibility_goneorvisible.setClickable(true);
        hotel_image_text.setVisibility(View.GONE);
        setmonitor();
    }

    /**
     * 构造函数(适用于跟酒店详情一模一样的图片布局)
     * 注意: 根据页面不同,有些详情只是一个ImageView或SimpleDraweeView,所以这个构造函数是针对第三个参数传的是ViewPager
     * 参数(10个参数)
     *
     * @param hotel_scrollview_details       1. 需要透明的布局 --> 图片移动时背景要慢慢透明
     * @param hotel_hotel_image_translation  2. 需要移除的viewpager --> 原理: 把这个viewpager移除掉添加到要平移的布局里
     * @param hotel_viewpager_addorremove    3. 需要移除(4,5)的父控件 --> 需要移除ViewPager的父控件 (必须是RelativeLayout)
     * @param hotel_imagecount               4. 需要移除viewpager下角的提示在第几张图片布局
     * @param hotel_hotel_image_all_hide     5. 需要移除viewpager和下角的父布局
     * @param hotel_details_image            6. 需要平移的布局 --> 让这个布局显示出来平移到中间位置  特别注意 : clickable 要设置为true
     * @param hotel_image_text               7. 图片详情下面的TextView -->由于移动图片时,TextView不需要立即显示，等待动画结束后显示
     * @param windowheight                   8. 屏幕的高度的一般 --> 在oncreate去获取，用于让图片平移到屏幕的中间 记得传过来的是屏幕高度的一半
     * @param hotel_top_hotel                9. 头部的布局 --> 用于让图片平移到屏幕的中间,由于大部分头部都引用了一个布局所以图片详情一般都是在头部的下面，所以还要根据头部高度计算
     * @param hotel_visibility_goneorvisible 10. 包裹着平移布局的布局 -->   推荐这个布局覆盖掉整个屏幕，里面写要平移的布局,平移的布局离这个布局的间距高度为头部的高度 特别注意 :clickable 要设置为true
     */
    public ImageAnimation(View hotel_scrollview_details, View hotel_hotel_image_translation, View hotel_viewpager_addorremove, View hotel_imagecount, View hotel_hotel_image_all_hide, View hotel_details_image, View hotel_image_text, int windowheight, View hotel_top_hotel, final View hotel_visibility_goneorvisible) {
        this.hotel_scrollview_details = hotel_scrollview_details;
        this. hotel_hotel_image_translation = hotel_hotel_image_translation;
        this.hotel_details_image = hotel_details_image;
        this.hotel_image_text = hotel_image_text;
        this.windowheight = windowheight;
        this.hotel_top_hotel = hotel_top_hotel;
        this.hotel_visibility_goneorvisible = hotel_visibility_goneorvisible;
        this.hotel_imagecount = hotel_imagecount;
        this.hotel_hotel_image_all_hide = hotel_hotel_image_all_hide;
        this.hotel_viewpager_addorremove = hotel_viewpager_addorremove;
        animSet = new AnimatorSet();
        animSet.setInterpolator(new LinearInterpolator());
        animSet.setStartDelay(200); //执行动画之前的延迟
        this.hotel_details_image.measure(0, 0);
        translationlinearLayoutheight = this.hotel_details_image.getMeasuredHeight();
        this.hotel_visibility_goneorvisible.setClickable(true);
        hotel_image_text.setVisibility(View.GONE);
        setmonitor();
    }

    public void setmonitor() {
        this.hotel_visibility_goneorvisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if (!boolanimation && animatorend) {
                        openorcloseanimation();// 调用动画
                }
            }
        });
        if (this.hotel_hotel_image_translation instanceof ImageView) {
            this.hotel_hotel_image_translation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        if (boolanimation && !animatorend) {
                            openorcloseanimation();// 调用动画
                    }
                }
            });
        } else {
            this.hotel_hotel_image_translation.setOnTouchListener(new View.OnTouchListener() {
                private boolean moved;
                private float x
                        ,
                        y;

                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        moved = false;
                        x = motionEvent.getX();
                        y = motionEvent.getY();
                    }
                    if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                        if (Math.abs(motionEvent.getY() - y) < 10 && Math.abs(motionEvent.getX() - x) < 10) {
                            moved = false;
                        } else {
                            moved = true;
                        }

                    }
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        if (!moved) {
                            view.performClick();
                        }
                    }
                    return false;
                }
            });
            this.hotel_hotel_image_translation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        if (boolanimation && !animatorend) {
                            openorcloseanimation();// 调用动画
                        }
                }
            });
        }
    }

    /**
     * 开启动画
     * <p/>
     * 该函数会自动判断是否在图片详情页面，会自动调用开启或关闭动画
     * 构造函数执行后只要调用该方法就可以
     */
    public void openorcloseanimation() {
        if (calculateensemblehowmanyok) {
            hotel_hotel_image_translationheight = hotel_hotel_image_translation.getHeight();
            hotel_top_hotelheight = hotel_top_hotel.getHeight();
            calculateensemblehowmanyok = false;
        }
        if (translationlinearLayoutheight > hotel_hotel_image_translationheight) {
            translationlinearLayoutheight = translationlinearLayoutheight - hotel_hotel_image_translationheight;
        }

        if (boolanimation) { // 开启动画
            boolanimation = false;
            animSet.setDuration(500);
            animSet.removeAllListeners();
            lucencyopenorcloseanimator = ObjectAnimator.ofFloat(hotel_scrollview_details, "alpha", 1, 0);// 属性透明动画
            translationopeanimator = ObjectAnimator.ofFloat(hotel_details_image, "translationY", 0, windowheight - hotel_hotel_image_translationheight / 2 - hotel_top_hotelheight - translationlinearLayoutheight);// 属性平移动画,移动控件一起移动
            animSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    if (hotel_hotel_image_translation instanceof ImageView) {
                        hotel_hotel_image_translation.setVisibility(View.GONE);
                        hotel_visibility_goneorvisible.setVisibility(View.VISIBLE);
                    } else if (hotel_hotel_image_translation instanceof ViewPager) {
                        hotel_visibility_goneorvisible.setVisibility(View.VISIBLE);
                        hotel_imagecount.setVisibility(View.GONE);
                        ((RelativeLayout) hotel_viewpager_addorremove).removeView(hotel_hotel_image_all_hide);
                        ((LinearLayout) hotel_details_image).removeView(hotel_image_text);
                        ((LinearLayout) hotel_details_image).addView(hotel_hotel_image_all_hide);
                        ((LinearLayout) hotel_details_image).addView(hotel_image_text);
                        if (isautoscrollviewpager) {
                            ((AutoScrollViewPager) hotel_hotel_image_translation).stopAutoScroll();
                            ((AutoScrollViewPager) hotel_hotel_image_translation).setStopScrollWhenTouch(false);
                        }
                    }
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    hotel_image_text.setVisibility(View.VISIBLE);
                    animatorend = true;
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            // 两个动画同时执行
            animSet.playTogether(lucencyopenorcloseanimator, translationopeanimator);
            animSet.start();
        } else { // 关闭动画
            boolanimation = true;

            animSet.setDuration(300);
            animSet.removeAllListeners();
            lucencyopenorcloseanimator = ObjectAnimator.ofFloat(hotel_scrollview_details, "alpha", 0, 1); // 属性透明动画,移动控件一起移动
            translationopeanimator = ObjectAnimator.ofFloat(hotel_details_image, "translationY", hotel_details_image.getTranslationY(), 0); // 属性平移动画,移动控件一起移动
            animSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    hotel_image_text.setVisibility(View.GONE);
                }
                @Override
                public void onAnimationEnd(Animator animator) {
                    animatorend = false;
                    if (hotel_hotel_image_translation instanceof ImageView) {
                        hotel_visibility_goneorvisible.setVisibility(View.GONE);
                        hotel_hotel_image_translation.setVisibility(View.VISIBLE);
                    } else if (hotel_hotel_image_translation instanceof ViewPager) {
                        ((LinearLayout) hotel_details_image).removeView(hotel_hotel_image_all_hide);
                        ((RelativeLayout) hotel_viewpager_addorremove).addView(hotel_hotel_image_all_hide);
                        hotel_visibility_goneorvisible.setVisibility(View.GONE);
                        hotel_imagecount.setVisibility(View.VISIBLE);
                        if (isautoscrollviewpager) {
                            ((AutoScrollViewPager) hotel_hotel_image_translation).startAutoScroll();
                            ((AutoScrollViewPager) hotel_hotel_image_translation).setStopScrollWhenTouch(true);
                        }
                    }

                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            // 两个动画同时执行
            animSet.playTogether(lucencyopenorcloseanimator, translationopeanimator);
            animSet.start();
        }
    }

    /**
     * 让动画直接到最后，一般用于退出图片详情页面，动画还在播放，但是用户又按了一下返回那么直接让动画直接到最后，但后退出;
     */

    public void endanimation() {
        animSet.end();
    }

    /**
     * 返回动画是否在运行
     */
    public boolean isHotel_animator_terminal() {
        return animSet.isRunning();
    }

    /**
     * 返回是否在图片详情页面
     */
    public boolean isBoolanimation() {
        return boolanimation;
    }

    /**
     * 设置传进来的值是否是AutoScrollViewPager
     */

    public void setIsautoscrollviewpager(boolean isautoscrollviewpager) {
        this.isautoscrollviewpager = isautoscrollviewpager;
    }

}
