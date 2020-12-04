package com.xsic.xsic.ui.tailor.tailorFrame;

import android.graphics.Matrix;

import com.xsic.xsic.utils.LogUtil;
import com.xsic.xsic.utils.ScreenUtil;

/**
 * 外部通过改变这里的值来改变TailorFrameView的框比例
 */
public class ViewSupport {
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

    public Matrix mMatrix = new Matrix();
    public float mZoomFactor = 1.0f;

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


    public ViewSupport() {

        /* 只需要改变这部份 */
        mTopLeft_X = 100;
        mTopLeft_Y = 100;
        mTopRight_X = ScreenUtil.getScreenWidth() - 100;
        mTopRight_Y = 100;
        mBottomRight_X = ScreenUtil.getScreenWidth() - 100;
        mBottomRight_Y = ScreenUtil.getScreenHeight() - 100;
        mBottomLeft_X = 100;
        mBottomLeft_Y = ScreenUtil.getScreenHeight() - 100;
        /* 只需要改变这部份 */

        updateRelativeData();
    }

    public void setTopLeft(float x, float y){
        mTopLeft_X += x;
        mTopLeft_Y += y;
        LogUtil.d("xsafafaf","mTopLeft_X = "+mTopLeft_X + " ， mTopLeft_Y = " + mTopLeft_Y);
        updateRelativeData();
    }

    public void setTopRight(float x, float y){
        mTopRight_X += x;
        mTopRight_Y += y;
        updateRelativeData();
    }

    public void setBottomRight(float x, float y){
        mBottomRight_X += x;
        mBottomRight_Y += y;
        updateRelativeData();
    }

    public void setBottomLeft(float x, float y){
        mBottomLeft_X += x;
        mBottomLeft_Y += y;
        updateRelativeData();
    }

    public void setPointByZoom(float zoomFactor){
        mTopLeft_X *= zoomFactor;
        mTopLeft_Y *= zoomFactor;
        mTopRight_X *= zoomFactor;
        mTopRight_Y *= zoomFactor;
        mBottomRight_X *= zoomFactor;
        mBottomRight_Y *= zoomFactor;
        mBottomLeft_X *= zoomFactor;
        mBottomLeft_Y *= zoomFactor;
        updateRelativeData();
    }

    private void updateRelativeData(){
        mTop = mTopLeft_Y;
        mLeft = mTopLeft_X;
        mRight = mBottomRight_X;
        mBottom = mBottomRight_Y;

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

    public void debug4Point(String flag){
        LogUtil.d(flag, " ： mTopLeft_X = " +mTopLeft_X + " ， mTopLeft_Y = " + mTopLeft_Y);
    }
}
