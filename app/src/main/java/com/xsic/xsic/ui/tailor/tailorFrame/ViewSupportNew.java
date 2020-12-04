package com.xsic.xsic.ui.tailor.tailorFrame;

import android.graphics.Matrix;

import com.xsic.xsic.utils.LogUtil;
import com.xsic.xsic.utils.ScreenUtil;

/**
 * 外部通过改变这里的值来改变TailorFrameView的框比例
 */
public class ViewSupportNew {
    /**
     * 界限值，超过该值不响应缩放操作
     * 有可能由：
     *          1、图片大小所限制
     *          2、框的比例
     */
    private float LIMIT_TOP_LEFT_X;
    private float LIMIT_TOP_LEFT_Y;
    private float LIMIT_BOTTOM_RIGHT_X;
    private float LIMIT_BOTTOM_RIGHT_Y;

    /**
     * 宽度与高度的界限值，为最大值的0.1倍
     */
    private float LIMIT_HEIGHT;
    private float LIMIT_WIDTH;

    public Matrix mMatrix = new Matrix();

    //某一边已经贴在边界上，因此不允许再往该方向做平移操作
    public boolean mIsLeftOutOfBoundary = false;
    public boolean mIsTopOutOfBoundary = false;
    public boolean mIsRightOutOfBoundary = false;
    public boolean mIsBottomOutOfBoundary = false;

    //宽高，只可以被[自由缩放]和[双指缩放]所控制
    public float mHeight;
    public float mWidth;

    //矩形四个顶点坐标
    //左上角
    public float mTopLeft_X;
    public float mTopLeft_Y;
    //右上角
    public float mTopRight_X;
    public float mTopRight_Y;
    //右下角
    public float mBottomRight_X;
    public float mBottomRight_Y;
    //左下角
    public float mBottomLeft_X;
    public float mBottomLeft_Y;

    //矩形的top、right、bottom、left
    public float mTop;
    public float mRight;
    public float mBottom;
    public float mLeft;

    //线的起终点
    public float mLine_1_start_X;
    public float mLine_1_start_Y;
    public float mLine_1_end_X;
    public float mLine_1_end_Y;

    public float mLine_2_start_X;
    public float mLine_2_start_Y;
    public float mLine_2_end_X;
    public float mLine_2_end_Y;

    public float mLine_3_start_X;
    public float mLine_3_start_Y;
    public float mLine_3_end_X;
    public float mLine_3_end_Y;

    public float mLine_4_start_X;
    public float mLine_4_start_Y;
    public float mLine_4_end_X;
    public float mLine_4_end_Y;

    /**
     * 仅允许通过RectF去修改l,t,r,b四个值，其余值由本类自行更新
     */
    public ViewSupportNew() {
        mLeft = 100;
        mTop = 100;
        mRight = ScreenUtil.getScreenWidth() - 100;
        mBottom = ScreenUtil.getScreenHeight() - 100;

        mWidth = mRight - mLeft;
        mHeight = mBottom - mTop;

        LIMIT_TOP_LEFT_X = 0;
        LIMIT_TOP_LEFT_Y = 0;
        LIMIT_BOTTOM_RIGHT_X = ScreenUtil.getScreenWidth();
        LIMIT_BOTTOM_RIGHT_Y = ScreenUtil.getScreenHeight();



        updateRelativeData();
    }

    public void setLeft_Move(float l){
        if (mIsRightOutOfBoundary){
            //当对角点抵达边界时，重新更新本点的坐标
            mLeft = LIMIT_BOTTOM_RIGHT_X - mWidth;
            updateRelativeData();
            return;
        }
        mLeft += l;
        if ((mLeft += l) <= LIMIT_TOP_LEFT_X){
            mLeft = LIMIT_TOP_LEFT_X;
            mIsLeftOutOfBoundary = true;
        }else {
            mIsLeftOutOfBoundary = false;
        }
        updateRelativeData();
    }

    public void setLeft_Free(float l){
        mLeft = l;
        if (mLeft <= LIMIT_TOP_LEFT_X){
            mLeft = LIMIT_TOP_LEFT_X;
            mIsLeftOutOfBoundary = true;
        }else {
            mIsLeftOutOfBoundary = false;
        }
        updateRelativeData();
        updateHeightAndWidth();
    }

    public void setTop_Move(float t){
        if (mIsBottomOutOfBoundary){
            mTop = LIMIT_BOTTOM_RIGHT_Y - mHeight;
            updateRelativeData();
            return;
        }
        mTop += t;
        if ((mTop += t) <= LIMIT_TOP_LEFT_Y){
            mTop = LIMIT_TOP_LEFT_Y;
            mIsTopOutOfBoundary = true;
        }else {
            mIsTopOutOfBoundary = false;
        }
        updateRelativeData();
    }

    public void setTop_Free(float t){
        mTop = t;
        if (mTop <= LIMIT_TOP_LEFT_Y){
            mLeft = LIMIT_TOP_LEFT_Y;
            mIsTopOutOfBoundary = true;
        }else {
            mIsTopOutOfBoundary = false;
        }
        updateRelativeData();
        updateHeightAndWidth();
    }

    public void setRight_Move(float r){
        if (mIsLeftOutOfBoundary){
            mRight = LIMIT_TOP_LEFT_X + mWidth;
            updateRelativeData();
            return;
        }
        mRight += r;
        if ((mRight += r) >= LIMIT_BOTTOM_RIGHT_X){
            mRight = LIMIT_BOTTOM_RIGHT_X;
            mIsRightOutOfBoundary = true;
        }else {
            mIsRightOutOfBoundary = false;
        }
        updateRelativeData();
    }

    public void setRight_Free(float r){
        mRight = r;
        if (mRight >= LIMIT_BOTTOM_RIGHT_X){
            mRight = LIMIT_BOTTOM_RIGHT_X;
            mIsRightOutOfBoundary = true;
        }else {
            mIsRightOutOfBoundary = false;
        }
        updateRelativeData();
        updateHeightAndWidth();
    }

    public void setBottom_Move(float b){
        if (mIsTopOutOfBoundary){
            mBottom = LIMIT_TOP_LEFT_Y + mHeight;
            updateRelativeData();
            return;
        }
        mBottom += b;
        if ((mBottom += b) >= LIMIT_BOTTOM_RIGHT_Y){
            mBottom = LIMIT_BOTTOM_RIGHT_Y;
            mIsBottomOutOfBoundary = true;
        }else {
            mIsBottomOutOfBoundary = false;
        }
        updateRelativeData();
    }

    public void setBottom_Free(float b){
        mBottom = b;
        if (mBottom >= LIMIT_BOTTOM_RIGHT_Y){
            mBottom = LIMIT_BOTTOM_RIGHT_Y;
            mIsBottomOutOfBoundary = true;
        }else {
            mIsBottomOutOfBoundary = false;
        }
        updateRelativeData();
        updateHeightAndWidth();
    }

    public void setPointByZoom(float zoomFactor){
        mLeft *= zoomFactor;
        mTop *= zoomFactor;
        mRight *= zoomFactor;
        mBottom *= zoomFactor;
        updateRelativeData();
        updateHeightAndWidth();
    }

    private void updateHeightAndWidth(){
        mHeight = mBottom - mTop;
        mWidth = mRight - mLeft;
    }

    private void updateRelativeData(){
        mTopLeft_X = mLeft;
        mTopLeft_Y = mTop;
        mTopRight_X = mRight;
        mTopRight_Y = mTop;
        mBottomRight_X = mRight;
        mBottomRight_Y = mBottom;
        mBottomLeft_X = mLeft;
        mBottomLeft_Y = mBottom;

        LogUtil.d("updateRelativeData","mTopLeft = ("+mTopLeft_X+" , "+mTopLeft_Y+")"+", " +
                "mTopRight = ("+mTopRight_X+" , "+mTopRight_Y+")"+", " +
                "mBottomRight = ("+mBottomRight_X+" , "+mBottomRight_Y+")"+", " +
                "mBottomLeft = ("+mBottomLeft_X+" , "+mBottomLeft_Y+")");


        LogUtil.i("updateRelativeData",(mRight - mLeft) +"");

        mLine_1_start_X = (mRight - mLeft) * (1f/3f) + mLeft;
        mLine_1_start_Y = mTop;
        mLine_1_end_X = mLine_1_start_X;
        mLine_1_end_Y = mBottom;

        mLine_2_start_X = (mRight - mLeft) * (2f/3f) + mLeft;
        mLine_2_start_Y = mTop;
        mLine_2_end_X = mLine_2_start_X;
        mLine_2_end_Y = mBottom;

        mLine_3_start_X = mLeft;
        mLine_3_start_Y = (mBottom - mTop) * (1f/3f) + mTop;
        mLine_3_end_X = mRight;
        mLine_3_end_Y = mLine_3_start_Y;

        mLine_4_start_X = mLeft;
        mLine_4_start_Y = (mBottom - mTop) * (2f/3f) + mTop;
        mLine_4_end_X = mRight;
        mLine_4_end_Y = mLine_4_start_Y;
    }

}
