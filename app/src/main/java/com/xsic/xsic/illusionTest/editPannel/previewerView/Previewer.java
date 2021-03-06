package com.xsic.xsic.illusionTest.editPannel.previewerView;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;

import com.xsic.xsic.illusionTest.base.BaseView4;
import com.xsic.xsic.illusionTest.base.ViewSupport;
import com.xsic.xsic.utils.LogUtil;

public class Previewer extends BaseView4 {
    private boolean mIsHandlingMorePoint = false;
    private ViewSupport mInitItem = new ViewSupport();

    public Previewer(Context context) {
        super(context);
    }

    public Previewer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Previewer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        init();
    }

    private void init(){
        mInitItem = mSourceImg.clone();
        MIN_SCALE *= mInitItem.mScaleX;
        MAX_SCALE *= mInitItem.mScaleX;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                if (mIsAnimating && valueAnimator!=null && valueAnimator.isRunning()){
                    valueAnimator.cancel();
                }
                mTempProperty = mSourceImg.clone();
                initTranslate(event.getX(),event.getY());
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                mTempProperty = mSourceImg.clone();
                initMorePointTranslate(mSourceImg,event);
                initMorePointScale(event);
                break;

            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() > 1){
                    mIsHandlingMorePoint = true;
                    mSourceImg.set(mTempProperty);
                    morePointTranslate(mSourceImg,event);
                    morePointScale(mSourceImg,event);
                    postMatrix(mSourceImg);
                }else {
                    if (mIsHandlingMorePoint) break;
                    mSourceImg.set(mTempProperty);
                    translate(mSourceImg,event.getX(),event.getY());
                    postMatrix(mSourceImg);
                }
                break;

            case MotionEvent.ACTION_POINTER_UP:
                break;

            case MotionEvent.ACTION_UP:
                mIsHandlingMorePoint = false;
                doSpringBackIfNeed();
                break;
        }
        invalidate();
        return true;
    }

    public void setImage(String filePath){
        super.setImage(BitmapFactory.decodeFile(filePath));
    }

    private void doSpringBackIfNeed(){
        boolean needSpringBack = false;

        float difX = mSourceImg.mBitmap.getWidth()*mSourceImg.mScaleX - getWidth();
        float difY = mSourceImg.mBitmap.getHeight()*mSourceImg.mScaleY - getHeight();

        float maxCenterX = mInitItem.mCenterX + difX;
        float maxCenterY = mInitItem.mCenterY + difY;
        float minCenterX = mInitItem.mCenterX - difX;
        float minCenterY = mInitItem.mCenterY - difY;

//        if (mTempProperty.mCenterX < minCenterX){
//            mTempProperty.mCenterX = minCenterX;
//            mTempProperty.mX += minCenterX - mSpringBackItem.mCenterX;
//        }
//        if (mTempProperty.mCenterX > maxCenterX){
//            mTempProperty.mCenterX = maxCenterX;
//            mTempProperty.mX += maxCenterX - mSpringBackItem.mCenterX;
//        }
//        if (mTempProperty.mCenterY < minCenterY){
//            mTempProperty.mCenterY = minCenterY;
//            mTempProperty.mY += minCenterY - mSpringBackItem.mCenterY;
//        }
//        if (mTempProperty.mCenterY > maxCenterY){
//            mTempProperty.mCenterY = maxCenterY;
//            mTempProperty.mY += maxCenterY - mSpringBackItem.mCenterY;
//        }

        if (mTempProperty.mScaleX > MAX_SCALE){
            needSpringBack = true;
            mTempProperty.mScaleX = MAX_SCALE;
            mTempProperty.mScaleY = MAX_SCALE;
        }else if (mTempProperty.mScaleX < MIN_SCALE){
            needSpringBack = true;
            mTempProperty.mScaleX = MIN_SCALE;
            mTempProperty.mScaleY = MIN_SCALE;
        }else {
            if (mSourceImg.mScaleX < MIN_SCALE){
                needSpringBack = true;
                mTempProperty.mScaleX = MIN_SCALE;
                mTempProperty.mScaleY = MIN_SCALE;
            }else {
                mTempProperty.mScaleX = mSourceImg.mScaleX;
                mTempProperty.mScaleY = mSourceImg.mScaleY;
            }
        }

        if (mSourceImg.mCenterX > mTempProperty.mCenterX){
            if (mSourceImg.mCenterX > maxCenterX){
                if (mSourceImg.mCenterX - maxCenterX > 0){
                    needSpringBack = true;
                    mTempProperty.mCenterX = maxCenterX;
                    mTempProperty.mX = mSourceImg.mCenterX - maxCenterX;
                }else {
                    mTempProperty.mCenterX = mSourceImg.mCenterX;
                    mTempProperty.mX = mSourceImg.mX;
                }
            }
        }else {
            if (mSourceImg.mCenterX < minCenterX){
                if (mSourceImg.mCenterX - minCenterX < 0){
                    needSpringBack = true;
                    mTempProperty.mCenterX = minCenterX;
                    mTempProperty.mX = mSourceImg.mCenterX - minCenterX;
                }else {
                    mTempProperty.mCenterX = mSourceImg.mCenterX;
                    mTempProperty.mX = mSourceImg.mX;
                }
            }
        }

        if (mSourceImg.mCenterY > mTempProperty.mCenterY){
            if (mSourceImg.mCenterY > maxCenterY){
                if (mSourceImg.mCenterY - maxCenterY > 0){
                    needSpringBack = true;
                    mTempProperty.mCenterY = maxCenterY;
                    mTempProperty.mY = mSourceImg.mCenterY - maxCenterY;
                }else {
                    mTempProperty.mCenterY = mSourceImg.mCenterY;
                    mTempProperty.mY = mSourceImg.mY;
                }
            }
        }else {
            if (mSourceImg.mCenterY < minCenterY){
                if (mSourceImg.mCenterY - minCenterY < 0){
                    needSpringBack = true;
                    mTempProperty.mCenterY = minCenterY;
                    mTempProperty.mY = mSourceImg.mCenterY - minCenterY;
                }else {
                    mTempProperty.mCenterY = mSourceImg.mCenterY;
                    mTempProperty.mY = mSourceImg.mY;
                }
            }
        }

        LogUtil.d("weqewqe",mTempProperty.toString() +","+ MAX_SCALE +","+ MIN_SCALE);

        if (needSpringBack){
            springBackAnimation(mSourceImg,mTempProperty);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
