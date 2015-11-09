package com.android.animation;

/**
 * Created by xudong on 2015/4/7.
 */

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * 3D翻转动画
 */
public class Flip3dAnimation extends Animation {
    private float mFromDegrees;
    private float mToDegrees;
    private  float mCenterX;
    private  float mCenterY;
    private Camera mCamera;


    public Flip3dAnimation(float fromDegrees, float toDegrees,
                           float centerX, float centerY) {
        mFromDegrees = fromDegrees;
        mToDegrees = toDegrees;
        mCenterX = centerX;
        mCenterY = centerY;
    }

    public void setStartAndOff(float start, float off,float centerX,float centerY) {
        this.mFromDegrees = start;
        this.mToDegrees = off;
        this.mCenterX = centerX;
        this.mCenterY = centerY;
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        mCamera = new Camera();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        final float fromDegrees = mFromDegrees;
        float degrees = fromDegrees + ((mToDegrees - fromDegrees) * interpolatedTime);

        final float centerX = mCenterX;
        final float centerY = mCenterY;
        final Camera camera = mCamera;

        final Matrix matrix = t.getMatrix();

        camera.save();

        camera.rotateY(degrees);

        camera.getMatrix(matrix);
        camera.restore();

        matrix.preTranslate(-centerX, -centerY);
        matrix.postTranslate(centerX, centerY);

    }

}