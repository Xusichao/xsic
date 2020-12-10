package com.xsic.xsic.ui.tailor.tailorFrame;

import android.graphics.Matrix;

import com.xsic.xsic.utils.LogUtil;
import com.xsic.xsic.utils.ScreenUtil;

/**
 * 外部通过改变这里的值来改变TailorFrameView的框比例
 */
public class ViewSupportNew {
    /**
     * 取zoomFactor数组时的index
     */
    public static final int ZOOM_X = 0;
    public static final int ZOOM_Y = 1;
    /**
     * 控制点区域的index
     */
    public static final int NONE = -1;
    public static final int LEFT_TOP = 0;
    public static final int RIGHT_TOP = 1;
    public static final int BOTTOM_RIGHT = 2;
    public static final int BOTTOM_LEFT = 3;
    /**
     * 界限值，超过该值不响应缩放操作
     * 有可能由：
     *          1、图片大小所限制
     *          2、框的比例
     */
    private float LIMIT_TOP_LEFT_X_MAX;
    private float LIMIT_TOP_LEFT_Y_MAX;
    private float LIMIT_BOTTOM_RIGHT_X_MAX;
    private float LIMIT_BOTTOM_RIGHT_Y_MAX;

    /**
     * 界限缩放值：
     *          1、最小值：为最大值的0.1倍，即0.1
     *          2、最大值：由LIMIT值算出
     */
    public static final float LIMIT_ZOOMFACTOR_MIN = 0.6f;
    public static float LIMIT_ZOOMFACTOR_MAX;

    /**
     * 初始宽高，用来计算当前缩放量
     */
    private float mInitWidth;
    private float mInitHeight;

    //实际当前缩放倍数，相对于初始状态
    private float mZoomFactor_X = 1.0f;
    private float mZoomFactor_Y = 1.0f;

    public Matrix mMatrix = new Matrix();

    //自由缩放时，判断是否处于“小于”的临界状态
    private boolean mIsLeftLocked = false;
    private boolean mIsTopLocked = false;
    private boolean mIsRightLocked  = false;
    private boolean mIsBottomLocked = false;

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

        LIMIT_TOP_LEFT_X_MAX = 0;
        LIMIT_TOP_LEFT_Y_MAX = 0;
        LIMIT_BOTTOM_RIGHT_X_MAX = ScreenUtil.getScreenWidth();
        LIMIT_BOTTOM_RIGHT_Y_MAX = ScreenUtil.getScreenHeight();

        setLimitZoomfactorMax();
        setInitSize();
        updateRelativeData();
    }

    private void setInitSize(){
        mInitHeight = mBottom - mTop;
        mInitWidth = mRight - mLeft;
    }

    private void setLimitZoomfactorMax(){
        LIMIT_ZOOMFACTOR_MAX = (LIMIT_BOTTOM_RIGHT_X_MAX - LIMIT_TOP_LEFT_X_MAX) / mWidth;
    }

    public void setLeft_Move(float l){
        if (mIsRightOutOfBoundary){
            //当对角点抵达边界时，重新调整本点的坐标，去除误差
            mLeft = LIMIT_BOTTOM_RIGHT_X_MAX - mWidth;
            updateRelativeData();
            return;
        }
        LogUtil.w("controlss","mLeft = "+mLeft+" , l = "+l+" , (mLeft + l) = "+(mLeft + l));
        mLeft += l;
        if ((mLeft + l) <= LIMIT_TOP_LEFT_X_MAX){
            mLeft = LIMIT_TOP_LEFT_X_MAX;
            mIsLeftOutOfBoundary = true;
        }else {
            mIsLeftOutOfBoundary = false;
        }
        updateRelativeData();
    }

    public void setTop_Move(float t){
        if (mIsBottomOutOfBoundary){
            mTop = LIMIT_BOTTOM_RIGHT_Y_MAX - mHeight;
            updateRelativeData();
            return;
        }
        mTop += t;
        if ((mTop + t) <= LIMIT_TOP_LEFT_Y_MAX){
            mTop = LIMIT_TOP_LEFT_Y_MAX;
            mIsTopOutOfBoundary = true;
        }else {
            mIsTopOutOfBoundary = false;
        }
        updateRelativeData();
    }

    public void setRight_Move(float r){
        if (mIsLeftOutOfBoundary){
            mRight = LIMIT_TOP_LEFT_X_MAX + mWidth;
            updateRelativeData();
            return;
        }
        mRight += r;
        if ((mRight + r) >= LIMIT_BOTTOM_RIGHT_X_MAX){
            mRight = LIMIT_BOTTOM_RIGHT_X_MAX;
            mIsRightOutOfBoundary = true;
        }else {
            mIsRightOutOfBoundary = false;
        }
        updateRelativeData();
    }

    public void setBottom_Move(float b){
        if (mIsTopOutOfBoundary){
            mBottom = LIMIT_TOP_LEFT_Y_MAX + mHeight;
            updateRelativeData();
            return;
        }
        mBottom += b;
        if ((mBottom + b) >= LIMIT_BOTTOM_RIGHT_Y_MAX){
            mBottom = LIMIT_BOTTOM_RIGHT_Y_MAX;
            mIsBottomOutOfBoundary = true;
        }else {
            mIsBottomOutOfBoundary = false;
        }
        updateRelativeData();
    }



    public float[] getZoomFactor(){
        return new float[]{mZoomFactor_X,mZoomFactor_Y};
    }

    public void setZoomFactor(float zf_x, float zf_y){
        float tempZoomX = mZoomFactor_X * zf_x;
        float tempZoomY = mZoomFactor_Y * zf_y;

        mZoomFactor_X *= zf_x;
        mZoomFactor_Y *= zf_y;

        if (tempZoomX >= LIMIT_ZOOMFACTOR_MAX){
            mZoomFactor_X = LIMIT_ZOOMFACTOR_MAX;
        }
        if (tempZoomX <= LIMIT_ZOOMFACTOR_MIN){
            mZoomFactor_X = LIMIT_ZOOMFACTOR_MIN;
        }
        if (tempZoomY >= LIMIT_ZOOMFACTOR_MAX){
            mZoomFactor_Y = LIMIT_ZOOMFACTOR_MAX;
        }
        if (tempZoomY <= LIMIT_ZOOMFACTOR_MIN){
            mZoomFactor_Y = LIMIT_ZOOMFACTOR_MIN;
        }

        LogUtil.d("setZoomFactor",mZoomFactor_X + " , " + mZoomFactor_Y);
    }

    public void setLeft_Zoom(float l){
        mLeft = l;
        if (mLeft <= LIMIT_TOP_LEFT_X_MAX){
            mLeft = LIMIT_TOP_LEFT_X_MAX;
        }
        updateRelativeData();
        updateHeightAndWidth();
        LogUtil.d("zoomFactrorFuck"," mZoomFactor_X = "+mZoomFactor_X + " , mZoomFactor_Y = "+mZoomFactor_Y);
    }

    public void setTop_Zoom(float t){
        mTop = t;
        if (mTop <= LIMIT_TOP_LEFT_Y_MAX){
            mTop = LIMIT_TOP_LEFT_Y_MAX;
        }
        updateRelativeData();
        updateHeightAndWidth();
        LogUtil.d("zoomFactrorFuck"," mZoomFactor_X = "+mZoomFactor_X + " , mZoomFactor_Y = "+mZoomFactor_Y);
    }

    public void setRight_Zoom(float r){
        mRight = r;
        if (mRight >= LIMIT_BOTTOM_RIGHT_X_MAX){
            mRight = LIMIT_BOTTOM_RIGHT_X_MAX;
        }
        updateRelativeData();
        updateHeightAndWidth();
        LogUtil.d("zoomFactrorFuck"," mZoomFactor_X = "+mZoomFactor_X + " , mZoomFactor_Y = "+mZoomFactor_Y);
    }

    public void setBottom_Zoom(float b){
        mBottom = b;
        if (mBottom >= LIMIT_BOTTOM_RIGHT_Y_MAX){
            mBottom = LIMIT_BOTTOM_RIGHT_Y_MAX;
        }
        updateRelativeData();
        updateHeightAndWidth();
        LogUtil.d("zoomFactrorFuck"," mZoomFactor_X = "+mZoomFactor_X + " , mZoomFactor_Y = "+mZoomFactor_Y);
    }






    public void setLeft_Free(float l){
        //如果当前的mLeft加上偏移量会导致触发临界机制的话，则将l重新赋值
        if ((mRight - (mLeft+l))/(mInitWidth) <= LIMIT_ZOOMFACTOR_MIN){
            mZoomFactor_X = LIMIT_ZOOMFACTOR_MIN;
            l = 0;
        }
//        if (mZoomFactor_X < LIMIT_ZOOMFACTOR_MIN){
//            mZoomFactor_X = LIMIT_ZOOMFACTOR_MIN;
//            l = 0;
//            mIsLeftLocked = true;
//        }else {
//            mIsLeftLocked = false;
//        }
        mLeft += l;
        if (mLeft <= LIMIT_TOP_LEFT_X_MAX){
            mLeft = LIMIT_TOP_LEFT_X_MAX;
            mIsLeftOutOfBoundary = true;
        }else {
            mIsLeftOutOfBoundary = false;
        }
        updateZoomFactor();
        updateRelativeData();
        updateHeightAndWidth();
        LogUtil.i("zoomFactrorFuck"," mZoomFactor_X = "+mZoomFactor_X + " , mZoomFactor_Y = "+mZoomFactor_Y);
    }

    public void setTop_Free(float t){
        if ((mBottom - (mTop+t))/(mInitHeight) <= LIMIT_ZOOMFACTOR_MIN){
            mZoomFactor_Y = LIMIT_ZOOMFACTOR_MIN;
            t = 0;
        }
        mTop += t;
        if (mTop <= LIMIT_TOP_LEFT_Y_MAX){
            mLeft = LIMIT_TOP_LEFT_Y_MAX;
            mIsTopOutOfBoundary = true;
        }else {
            mIsTopOutOfBoundary = false;
        }
        updateZoomFactor();
        updateRelativeData();
        updateHeightAndWidth();
        LogUtil.i("zoomFactrorFuck"," mZoomFactor_X = "+mZoomFactor_X + " , mZoomFactor_Y = "+mZoomFactor_Y);
    }

    public void setRight_Free(float r){
        if (((mRight+r) - mLeft)/(mInitWidth) <= LIMIT_ZOOMFACTOR_MIN){
            mZoomFactor_X = LIMIT_ZOOMFACTOR_MIN;
            r = 0;
        }
        mRight += r;
        if (mRight >= LIMIT_BOTTOM_RIGHT_X_MAX){
            mRight = LIMIT_BOTTOM_RIGHT_X_MAX;
            mIsRightOutOfBoundary = true;
        }else {
            mIsRightOutOfBoundary = false;
        }
        updateZoomFactor();
        updateRelativeData();
        updateHeightAndWidth();
        LogUtil.i("zoomFactrorFuck"," mZoomFactor_X = "+mZoomFactor_X + " , mZoomFactor_Y = "+mZoomFactor_Y);
    }

    public void setBottom_Free(float b){
        if (((mBottom+b) - mTop)/(mInitHeight) <= LIMIT_ZOOMFACTOR_MIN){
            LogUtil.e("zoomFactrorFuck",((mBottom+b) - mTop)/(mInitHeight)+" , " + b);
            mZoomFactor_Y = LIMIT_ZOOMFACTOR_MIN;
            b = 0;
        }
        mBottom += b;
        if (mBottom >= LIMIT_BOTTOM_RIGHT_Y_MAX){
            mBottom = LIMIT_BOTTOM_RIGHT_Y_MAX;
            mIsBottomOutOfBoundary = true;
        }else {
            mIsBottomOutOfBoundary = false;
        }
        updateZoomFactor();
        updateRelativeData();
        updateHeightAndWidth();
        LogUtil.i("zoomFactrorFuck"," mZoomFactor_X = "+mZoomFactor_X + " , mZoomFactor_Y = "+mZoomFactor_Y);
    }





    private void updateZoomFactor(){
        mZoomFactor_X = (mRight - mLeft)/mInitWidth;
        mZoomFactor_Y = (mBottom - mTop)/mInitHeight;

        // TODO: 2020/12/10 待验证
        if (mZoomFactor_X <= LIMIT_ZOOMFACTOR_MIN){
            mZoomFactor_X = LIMIT_ZOOMFACTOR_MIN;
        }
        if (mZoomFactor_X >= LIMIT_ZOOMFACTOR_MAX){
            mZoomFactor_X = LIMIT_ZOOMFACTOR_MAX;
        }
        if (mZoomFactor_Y <= LIMIT_ZOOMFACTOR_MIN){
            mZoomFactor_Y = LIMIT_ZOOMFACTOR_MIN;
        }
        if (mZoomFactor_Y >= LIMIT_ZOOMFACTOR_MAX){
            mZoomFactor_Y = LIMIT_ZOOMFACTOR_MAX;
        }
        LogUtil.d("updateZoomFactor",mZoomFactor_X + " , " + mZoomFactor_Y);
    }

    public void setPointByZoom(float zoomFactor){
        mZoomFactor_X *= zoomFactor;
        mZoomFactor_Y *= zoomFactor;
        LogUtil.d("zoomFactor",mZoomFactor_X + " , " + mZoomFactor_Y);

        mLeft *= zoomFactor;
        mTop *= zoomFactor;
        mRight *= zoomFactor;
        mBottom *= zoomFactor;

        //大于临界值
        if (mLeft <= LIMIT_TOP_LEFT_X_MAX){
            mLeft = LIMIT_TOP_LEFT_X_MAX;
        }
        if (mTop <= LIMIT_TOP_LEFT_Y_MAX){
            mTop = LIMIT_TOP_LEFT_Y_MAX;
        }
        if (mRight >= LIMIT_BOTTOM_RIGHT_X_MAX){
            mRight = LIMIT_BOTTOM_RIGHT_X_MAX;
        }
        if (mBottom >= LIMIT_BOTTOM_RIGHT_Y_MAX){
            mBottom = LIMIT_BOTTOM_RIGHT_Y_MAX;
        }

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


        LogUtil.d("updateRelativeData","mTopLeft = ("+mTopLeft_X+" , "+mTopLeft_Y+")"+", " +
                "mTopRight = ("+mTopRight_X+" , "+mTopRight_Y+")"+", " +
                "mBottomRight = ("+mBottomRight_X+" , "+mBottomRight_Y+")"+", " +
                "mBottomLeft = ("+mBottomLeft_X+" , "+mBottomLeft_Y+")");


        LogUtil.i("zoomFactror"," mZoomFactor_X = "+mZoomFactor_X + " , mZoomFactor_Y = "+mZoomFactor_Y);

        LogUtil.i("zoomFactrorQQQQ",(mRight - mLeft) + " ,  "+(mBottom - mTop));

    }

}
