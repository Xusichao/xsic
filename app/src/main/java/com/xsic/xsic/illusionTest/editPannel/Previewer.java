package com.xsic.xsic.illusionTest.editPannel;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;

import com.xsic.xsic.illusionTest.base.BaseView3;
import com.xsic.xsic.illusionTest.base.BaseView4;
import com.xsic.xsic.illusionTest.base.ViewSupport;
import com.xsic.xsic.utils.LogUtil;

public class Previewer extends BaseView4 {
    private boolean mIsHandlingMorePoint = false;
    private ViewSupport mTempSourceImg = new ViewSupport();

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
        initLimitParams();
    }

    private void initLimitParams(){
        mTempSourceImg = mSourceImg.clone();
        MIN_SCALE *= mSourceImg.mScaleX;
        MAX_SCALE *= mSourceImg.mScaleX;
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
        LogUtil.w("ttttqww",mSourceImg.toString());
        return true;
    }

    private boolean isPointAtImg(float x, float y){
        return false;
    }

    public void setImage(String filePath){
        super.setImage(BitmapFactory.decodeFile(filePath));
    }

    /**
     * 每次操作后更新回弹边界状态
     */
    private void updateBoundary(){

    }

    private void doSpringBackIfNeed(){
        if (mSourceImg.mScaleX >= MIN_SCALE || mSourceImg.mScaleX <= MAX_SCALE){
            return;
        }
        springBackAnimation(mSourceImg,mTempSourceImg);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
