package com.xsic.xsic.illusionTest.base;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.xsic.xsic.utils.LogUtil;

public class BaseView4 extends BaseView3 {
    protected static float MIN_SCALE = 0.4f;
    protected static float MAX_SCALE = 2.5f;
    protected final int DURATION = 300;

    protected boolean mIsAnimating = false;
    protected ValueAnimator valueAnimator;

    public BaseView4(Context context) {
        super(context);
    }

    public BaseView4(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseView4(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void springBackAnimation(ViewSupport curProperty, ViewSupport initProperty){
        valueAnimator = ValueAnimator.ofFloat(0,1f);
        valueAnimator.setDuration(DURATION);
        float tempX = curProperty.mX;
        float tempY = curProperty.mY;
        float tempCenterX = curProperty.mCenterX;
        float tempCenterY = curProperty.mCenterY;
        float tempScaleX = curProperty.mScaleX;
        float tempScaleY = curProperty.mScaleY;
        LogUtil.i("weqewqe",curProperty.mX+"");
        LogUtil.i("weqewqe",initProperty.mX+"");
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                curProperty.mX = tempX + animation.getAnimatedFraction() * (initProperty.mX - tempX);
                curProperty.mY = tempY + animation.getAnimatedFraction() * (initProperty.mY - tempY);
                curProperty.mCenterX = tempCenterX + animation.getAnimatedFraction() * (initProperty.mCenterX - tempCenterX);
                curProperty.mCenterY = tempCenterY + animation.getAnimatedFraction() * (initProperty.mCenterY - tempCenterY);
                curProperty.mScaleX = tempScaleX + animation.getAnimatedFraction() * (initProperty.mScaleX - tempScaleX);
                curProperty.mScaleY = tempScaleY + animation.getAnimatedFraction() * (initProperty.mScaleY - tempScaleY);
                postMatrix(curProperty);
                invalidate();
                LogUtil.d("weqewqe",curProperty.mX+"");
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mIsAnimating = false;
            }
            @Override
            public void onAnimationStart(Animator animation) {
                mIsAnimating = true;
            }
            @Override
            public void onAnimationPause(Animator animation) {
                mIsAnimating = false;
            }
        });
        valueAnimator.start();
    }
}
