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
    private ViewSupport mSpringBackItem = new ViewSupport();
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
        mSpringBackItem = mSourceImg.clone();
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
                    if (mSourceImg.mScaleX > MAX_SCALE){
                        mSpringBackItem.mScaleX = MAX_SCALE;
                        mSpringBackItem.mScaleY = MAX_SCALE;
                    }else if (mSourceImg.mScaleX < MIN_SCALE){
                        mSpringBackItem.mScaleX = MIN_SCALE;
                        mSpringBackItem.mScaleY = MIN_SCALE;
                    }else {
                        mSpringBackItem.mScaleX = mSourceImg.mScaleX;
                        mSpringBackItem.mScaleY = mSourceImg.mScaleY;
                    }
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
        float maxWidth = MAX_SCALE*mInitItem.mBitmap.getWidth();
        float maxHeight = MAX_SCALE*mInitItem.mBitmap.getHeight();

        float minCenterX = getWidth() - maxWidth/2f;
        float minCenterY = getHeight() - maxHeight/2f;
        float maxCenterX = maxWidth/2f;
        float maxCenterY = maxHeight/2f;

//        mSpringBackItem.mX = mSourceImg.mX;
//        mSpringBackItem.mY = mSourceImg.mY;
//        mSpringBackItem.mCenterX = mSourceImg.mCenterX;
//        mSpringBackItem.mCenterY = mSourceImg.mCenterY;

        if (mSourceImg.mCenterX < minCenterX){
            mSpringBackItem.mCenterX = minCenterX;
            mSpringBackItem.mX += minCenterX - mSpringBackItem.mCenterX;
        }
        if (mSourceImg.mCenterX > maxCenterX){
            mSpringBackItem.mCenterX = maxCenterX;
            mSpringBackItem.mX += maxCenterX - mSpringBackItem.mCenterX;
        }
        if (mSourceImg.mCenterY < minCenterY){
            mSpringBackItem.mCenterY = minCenterY;
            mSpringBackItem.mY += minCenterY - mSpringBackItem.mCenterY;
        }
        if (mSourceImg.mCenterY > maxCenterY){
            mSpringBackItem.mCenterY = maxCenterY;
            mSpringBackItem.mY += maxCenterY - mSpringBackItem.mCenterY;
        }

        springBackAnimation(mSourceImg,mSpringBackItem);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
