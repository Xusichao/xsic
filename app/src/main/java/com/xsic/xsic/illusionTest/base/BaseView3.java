package com.xsic.xsic.illusionTest.base;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;

import com.xsic.xsic.utils.ImageUtils;


public class BaseView3 extends BaseView2 {

    protected ViewSupport mBaseProperty = new ViewSupport();
    protected ViewSupport mTempProperty = new ViewSupport();

    protected float mDownX_1;
    protected float mDownY_1;
    protected float mDownX_2;
    protected float mDownY_2;
    protected float mMoreDownX;
    protected float mMOreDownY;

    protected float mInitDistance;
    protected float mInitRotate;


    public BaseView3(Context context) {
        super(context);
    }

    public BaseView3(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseView3(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void initTranslate(float x, float y){
        mDownX_1 = x;
        mDownY_1 = y;
    }

    protected void translate(ViewSupport viewSupport, float x, float y){
        float transX = x - mDownX_1;
        float transY = y - mDownY_1;
        if (viewSupport != null){
            viewSupport.mX += transX;
            viewSupport.mY += transY;
            viewSupport.mCenterX += transX;
            viewSupport.mCenterY += transY;
        }
    }

    protected void initScale(float x1,float y1,float x2,float y2){
        mInitDistance = ImageUtils.Spacing(x1-x2, y1-y2);
    }

    protected void scale(ViewSupport viewSupport,float x1,float y1,float x2,float y2){
        float tempDistance = ImageUtils.Spacing(x1-x2,y1-y2);

        float curZoomFactor = tempDistance/mInitDistance;

        //缩放中心
        float zoomCenter_X = (x1 + x2)/2f;
        float zoomCenter_Y = (y1 + y2)/2f;

        if (viewSupport!=null){
            viewSupport.mScaleX *= curZoomFactor;
            viewSupport.mScaleY *= curZoomFactor;
        }
    }

    /**
     * 多指移动
     * @param viewSupport
     * @param event
     */
    protected void initMorePointTranslate(ViewSupport viewSupport, MotionEvent event){
        float downX1 = event.getX(0);
        float downY1 = event.getY(0);
        float downX2 = event.getX(1);
        float downY2 = event.getY(1);

        float x = (downX1 + downX2)/2f;
        float y = (downY1 + downY2)/2f;
        mMoreDownX = x;
        mMOreDownY = y;
    }

    protected void initMorePointScale(MotionEvent event){
        mDownX_1 = event.getX(0);
        mDownY_1 = event.getY(0);
        mDownX_2 = event.getX(1);
        mDownY_2 = event.getY(1);
        mInitDistance = ImageUtils.Spacing(mDownX_2 - mDownX_1, mDownY_2 - mDownY_1);
    }

    /**
     * 多指移动
     * @param viewSupport
     * @param event
     */
    protected void morePointTranslate(ViewSupport viewSupport,MotionEvent event) {
        float downX1 = event.getX(0);
        float downY1 = event.getY(0);
        float downX2 = event.getX(1);
        float downY2 = event.getY(1);

        float x = (downX1 + downX2)/2f;
        float y = (downY1 + downY2)/2f;

        float translateX = x - mMoreDownX;
        float translateY = y - mMOreDownY;

        if(viewSupport != null){
            viewSupport.mX = viewSupport.mX + translateX;
            viewSupport.mY = viewSupport.mY + translateY;
            viewSupport.mCenterX = viewSupport.mCenterX + translateX;
            viewSupport.mCenterY = viewSupport.mCenterY + translateY;
        }
    }

    /**
     * 多指放大
     * @param viewSupport
     * @param event
     */
    protected void morePointScale(ViewSupport viewSupport, MotionEvent event) {
        float downX1 = event.getX(0);
        float downY1 = event.getY(0);
        float downX2 = event.getX(1);
        float downY2 = event.getY(1);

        float tempSpace = ImageUtils.Spacing(downX1 - downX2,downY1 - downY2);

        float curScale = tempSpace*1f/mInitDistance;

        if(viewSupport != null){
            viewSupport.mScaleX = viewSupport.mScaleX*curScale;
            viewSupport.mScaleY = viewSupport.mScaleY*curScale;
        }
    }

    protected void postMatrix(ViewSupport viewSupport){
        if (viewSupport!=null){
            viewSupport.mMatrix.reset();
            viewSupport.mMatrix.postTranslate(viewSupport.mX,viewSupport.mY);
            viewSupport.mMatrix.postScale(viewSupport.mScaleX,viewSupport.mScaleY,viewSupport.mCenterX,viewSupport.mCenterY);
            viewSupport.mMatrix.postRotate(viewSupport.mRotate);
        }
    }

    protected void postMatrix(Matrix matrix, ViewSupport viewSupport){
        if (matrix!=null){
            matrix.reset();
            matrix.postTranslate(viewSupport.mX,viewSupport.mY);
            matrix.postScale(viewSupport.mScaleX,viewSupport.mScaleY,viewSupport.mCenterX,viewSupport.mCenterY);
            matrix.postRotate(viewSupport.mRotate);
        }
    }


}
