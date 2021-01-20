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
    private RectFItem mRectItem = new RectFItem();
    private RectFItem mSpringBackItem = new RectFItem();

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
        mRectItem.mRectF.set(mShowRect);
        mSpringBackItem.set(mSourceImg.clone());
        mSpringBackItem.mRectF.set(mShowRect);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //if (!isPointAtImg(event.getX(),event.getY())) return false;
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
                    //矩形区域缩放
                    morePointTranslate(mRectItem,event);
                    morePointScale(mRectItem,event);
                    postMatrix(mRectItem);
                    mRectItem.mRectF.set(mShowRect);
                    mRectItem.mMatrix.mapRect(mRectItem.mRectF);
                }else {
                    if (mIsHandlingMorePoint) break;
                    mSourceImg.set(mTempProperty);
                    translate(mSourceImg,event.getX(),event.getY());
                    postMatrix(mSourceImg);
                    //矩形区域平移
                    translate(mRectItem,event.getX(),event.getY());
                    postMatrix(mRectItem);
                    mRectItem.mRectF.set(mShowRect);
                    mRectItem.mMatrix.mapRect(mRectItem.mRectF);
                }
                break;

            case MotionEvent.ACTION_POINTER_UP:
                break;

            case MotionEvent.ACTION_UP:
                mIsHandlingMorePoint = false;
                doSpringBackIfNeed();
                break;
        }
        LogUtil.d("ttteeee",mRectItem.mRectF.toString());
        invalidate();
        return true;
    }

    private boolean isPointAtImg(float x, float y){
        return false;//mRectF.contains(x, y);
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
        springBackAnimation(mSourceImg,mSpringBackItem,mRectItem);
        LogUtil.d("ttteeee",mRectItem.mRectF.toString());

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
