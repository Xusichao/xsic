package com.xsic.xsic.illusionTest.base;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.xsic.xsic.illusionTest.editPannel.previewerView.RectFItem;
import com.xsic.xsic.utils.LogUtil;

public class BaseView4 extends BaseView3 {
    protected static float MIN_SCALE = 1.0f;
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

    /**
     * 回弹动画
     * @param curProperty 当前图片的属性
     * @param springBackProperty 回弹位置的属性
     */
    protected void springBackAnimation(ViewSupport curProperty, ViewSupport springBackProperty){
        valueAnimator = ValueAnimator.ofFloat(0,1f);
        valueAnimator.setDuration(DURATION);
        float tempX = curProperty.mX;
        float tempY = curProperty.mY;
        float tempCenterX = curProperty.mCenterX;
        float tempCenterY = curProperty.mCenterY;
        float tempScaleX = curProperty.mScaleX;
        float tempScaleY = curProperty.mScaleY;

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //图片回弹
                curProperty.mX = tempX + animation.getAnimatedFraction() * (springBackProperty.mX - tempX);
                curProperty.mY = tempY + animation.getAnimatedFraction() * (springBackProperty.mY - tempY);
                curProperty.mCenterX = tempCenterX + animation.getAnimatedFraction() * (springBackProperty.mCenterX - tempCenterX);
                curProperty.mCenterY = tempCenterY + animation.getAnimatedFraction() * (springBackProperty.mCenterY - tempCenterY);
                curProperty.mScaleX = tempScaleX + animation.getAnimatedFraction() * (springBackProperty.mScaleX - tempScaleX);
                curProperty.mScaleY = tempScaleY + animation.getAnimatedFraction() * (springBackProperty.mScaleY - tempScaleY);
                postMatrix(curProperty);
                invalidate();
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
