package com.xsic.xsic.ui.s;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.xsic.xsic.R;
import com.xsic.xsic.utils.LogUtil;
import com.xsic.xsic.utils.ScreenUtil;

public class ImageViewer4 extends View {
    //常量
    private final String TAG = "ImageViewer";
    private final float MAX_SCALE = 2.5f;
    private final float MIN_SCALE = 1.0f;
    private final float LIMIT_MAX_SCALE = 4.0f;     //大于这个值不响应缩放
    private final float LIMIT_MIN_SCALE = 0.5f;     //小于这个值不响应缩放
    private final int ANIMATION_DURATION = 300;
    private float SCALE_RATIO = 0;                  //缩放系数，每个手机不同

    private Context mContext;
    private Paint mPaint;
    private GestureDetector mGestureDetector;
    private Drawable mDrawable;
    private Bitmap mBitmap;
    private boolean isOperateUIEnable = true;       //是否可操作UI
    private boolean isTranslateEnable = true;
    private boolean isRotateEnable = true;
    private boolean isScaleEnable = true;

    private Matrix mInitMatrix;
    private Matrix mCurMatrix;
    private Matrix mLastMatrix;
    private Matrix mSupMatrix;                      //辅助矩阵 ，用于回弹等操作

    private float[] mMatrixValues = new float[9];   //存放矩阵信息的数组

    //单指操作
    private float mTouchX;          //刚接触屏幕的坐标
    private float mTouchY;
    private float mDownX;           //移动时不断变化的坐标
    private float mDownY;

    //双指操作
    private float mTouch_1_X;        //刚接触屏幕的坐标
    private float mTouch_1_Y;
    private float mTouch_2_X;
    private float mTouch_2_Y;
    private float mDown_1_X;        //移动时不断变化的坐标
    private float mDown_1_Y;
    private float mDown_2_X;
    private float mDown_2_Y;

    private boolean isDoubleFinger = false;         //是否双指操作
    private boolean isFullHeight = false;           //true：高度铺满     false：宽度铺满
    private boolean isWeakSideTouchScreen = false;  //短的一边是否被放大到接触屏幕

    //左上角坐标
    private float mTopLeft_X;
    private float mTopLeft_Y;

    private float mInitBitmapHeight;
    private float mInitBitmapWidth;
    private float mBitmapHeight;
    private float mBitmapWidth;

    public ImageViewer4(Context context) {
        this(context,null,0);
    }

    public ImageViewer4(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ImageViewer4(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initAttrs(attrs, defStyleAttr);
        initVariable();
        initBitmap();
        initMatrix();
        setSCALE_RATIO();
    }

    private void initAttrs(AttributeSet attrs, int defStyleAttr){
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.ImageViewer,defStyleAttr,0);
        mDrawable = typedArray.getDrawable(R.styleable.ImageViewer_src);
        if (mDrawable == null){
            //设置默认图片，仅供调试
            mDrawable = ContextCompat.getDrawable(mContext,R.drawable.test1);
        }
        typedArray.recycle();
    }

    private void initVariable(){
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mGestureDetector = new GestureDetector(mContext,mOnGestureListener);
        mBitmap = ((BitmapDrawable)mDrawable).getBitmap();

        mInitMatrix = new Matrix();
        mCurMatrix = new Matrix();
        mLastMatrix = new Matrix();
        mSupMatrix = new Matrix();
    }

    private void initMatrix(){
        mInitMatrix.getValues(mMatrixValues);
        mCurMatrix.setValues(mMatrixValues);
        mLastMatrix.setValues(mMatrixValues);
    }

    private void setSCALE_RATIO(){
        mCurMatrix.setValues(mMatrixValues);
        SCALE_RATIO = mMatrixValues[Matrix.MSCALE_X];
    }

    private void initBitmap(){
        //高度的放大系数
        float heightScaleTime = (float) ScreenUtil.getScreenHeight()/(float) mBitmap.getHeight();
        //宽度的放大系数
        float widthScaleTime = (float) ScreenUtil.getScreenWidth()/(float) mBitmap.getWidth();
        //选择小的缩放系数对图片进行初始化
        float initScaleTime = Math.min(heightScaleTime,widthScaleTime);
        //图片放大之后的尺寸
        float initHeight = mBitmap.getHeight() * initScaleTime;
        float initWidth = mBitmap.getWidth() * initScaleTime;

        float initTranslateX;
        float initTranslateY;
        float initRoate = 0;
        if (heightScaleTime >= widthScaleTime){
            //1、宽度铺满
            isFullHeight = false;
            initTranslateX = 0;
            initTranslateY = (ScreenUtil.getScreenHeight() - initHeight)/2f;
        }else {
            //2、高度铺满
            isFullHeight = true;
            initTranslateX = (ScreenUtil.getScreenWidth() - initWidth)/2f;
            initTranslateY = 0;
        }

        //先后乘的顺序不能变
        mInitMatrix.reset();
        mInitMatrix.postScale(initScaleTime,initScaleTime);
        mInitMatrix.postTranslate(initTranslateX,initTranslateY);
        mInitMatrix.postRotate(initRoate);
        invalidate();


        mInitBitmapHeight = initHeight;
        mInitBitmapWidth = initWidth;
        setmBitmapSize(initHeight, initWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
        canvas.drawBitmap(mBitmap,mCurMatrix,mPaint);
    }

    private void setmLastMatrix(){
        mCurMatrix.getValues(mMatrixValues);
        mLastMatrix.setValues(mMatrixValues);
    }

    private void setmBitmapSize(float height, float width){
        mBitmapHeight = height;
        mBitmapWidth = width;
    }

    private void setIsWeakSideTouchedScreen(){
        mCurMatrix.getValues(mMatrixValues);
        float curZoomFactor = mMatrixValues[Matrix.MSCALE_X];
        if (isFullHeight){
            if (curZoomFactor/SCALE_RATIO >= ScreenUtil.getScreenWidth()/mInitBitmapWidth){
                isWeakSideTouchScreen = true;
            }else {
                isWeakSideTouchScreen = false;
            }
        }else {
            if (curZoomFactor/SCALE_RATIO >= ScreenUtil.getScreenHeight()/mInitBitmapHeight){
                isWeakSideTouchScreen = true;
            }else {
                isWeakSideTouchScreen = false;
            }
        }
    }

    //为 辅助矩阵 赋值
    private void setmSupMatrix(Matrix matrix){
        mSupMatrix.set(matrix);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        switch (event.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
                isDoubleFinger = false;
                mTouchX = event.getX();
                mTouchY = event.getY();
                mDownX = event.getX();
                mDownY = event.getY();
                break;

            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_UP:
                setmSupMatrix(mCurMatrix);
                if (!isDoubleFinger){
                    setmLastMatrix();
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() > 1){
                    mDown_1_X = event.getX(0);
                    mDown_1_Y = event.getY(0);
                    mDown_2_X = event.getX(1);
                    mDown_2_Y = event.getY(1);

                    zoom();
                }else {
                    if (!isDoubleFinger){
                        mDownX = event.getX();
                        mDownY = event.getY();

                        translate();
                    }
                }
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                isDoubleFinger = true;
                mTouch_1_X = event.getX(0);
                mTouch_1_Y = event.getY(0);
                mTouch_2_X = event.getX(1);
                mTouch_2_Y = event.getY(1);

                mDown_1_X = event.getX(0);
                mDown_1_Y = event.getY(0);
                mDown_2_X = event.getX(1);
                mDown_2_Y = event.getY(1);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                setmLastMatrix();
                break;

            default:break;
        }
        return true;
    }

    private void translate(){
        mLastMatrix.getValues(mMatrixValues);
        float curTranslateX = mMatrixValues[Matrix.MTRANS_X] + (mDownX - mTouchX);
        float curTranslateY = mMatrixValues[Matrix.MTRANS_Y] + (mDownY - mTouchY);
        float lastTranslateX = mMatrixValues[Matrix.MTRANS_X];
        float lastTranslateY = mMatrixValues[Matrix.MTRANS_Y];

        mCurMatrix.reset();
        mCurMatrix.postTranslate(curTranslateX - lastTranslateX , curTranslateY - lastTranslateY);
        mCurMatrix.setConcat(mCurMatrix,mLastMatrix);
        invalidate();
        setTopLeft();

    }

    private void zoom(){
        //首次接触屏幕时的双指距离
        float distanceOf2PointFirstTouch = (float) Math.sqrt(Math.pow(mTouch_1_X - mTouch_2_X,2)+Math.pow(mTouch_1_Y - mTouch_2_Y,2));
        //缩放时不断变化的双指距离
        float distanceOf2Point = (float) Math.sqrt(Math.pow(mDown_1_X - mDown_2_X,2)+Math.pow(mDown_1_Y - mDown_2_Y,2));
        if (distanceOf2Point == distanceOf2PointFirstTouch){
            return;
        }
        //首次接触屏幕时的双指中心点，即缩放中心点
        float zoomCenter_X = (mTouch_1_X + mTouch_2_X)/2f;
        float zoomCenter_Y = (mTouch_1_Y + mTouch_2_Y)/2f;

        float zoomFactor = distanceOf2Point / distanceOf2PointFirstTouch;

        mCurMatrix.reset();
        mCurMatrix.postScale(zoomFactor, zoomFactor, zoomCenter_X, zoomCenter_Y);
        mCurMatrix.setConcat(mCurMatrix,mLastMatrix);
        invalidate();

        setTopLeft();
        setmBitmapSize(mBitmap.getHeight() * SCALE_RATIO * zoomFactor, mBitmap.getWidth() * SCALE_RATIO * zoomFactor);
        setIsWeakSideTouchedScreen();
        LogUtil.d(TAG,isWeakSideTouchScreen+"");
    }

    /**
     * 设置左上角坐标值
     */
    private void setTopLeft(){
        mCurMatrix.getValues(mMatrixValues);
        mTopLeft_X = mMatrixValues[Matrix.MTRANS_X];
        mTopLeft_Y = mMatrixValues[Matrix.MTRANS_Y];
    }

    private void springBack(){
        mCurMatrix.getValues(mMatrixValues);
        // 4个顶点坐标
        float mTopRight_X = mTopLeft_X + mBitmapWidth;
        float mTopRight_Y = mTopLeft_Y;
        float mBottomRight_X = mTopLeft_X + mBitmapWidth;
        float mBottomRight_Y = mTopLeft_Y + mBitmapHeight;
        float mBottomLeft_X = mTopLeft_X;
        float mBottomLeft_Y = mTopLeft_Y + mBitmapHeight;

        // 边界点
        float topLeftLimit_X = 0;
        float topLeftLimit_Y = 0;
        float topRightLimit_X = 0;
        float topRightLimit_Y = 0;
        float bottomLeftLimit_X = 0;
        float bottomLeftLimit_Y = 0;
        float bottomRightLimit_X = 0;
        float bottomRightLimit_Y = 0;
        if (isWeakSideTouchScreen){
            topLeftLimit_X = 0;
            topLeftLimit_Y = 0;
            topRightLimit_X = ScreenUtil.getScreenWidth();
            topRightLimit_Y = 0;
            bottomRightLimit_X = ScreenUtil.getScreenWidth();
            bottomRightLimit_Y = ScreenUtil.getScreenWidth();
            bottomLeftLimit_X = 0;
            bottomLeftLimit_Y = ScreenUtil.getScreenHeight();
        }else {
            mSupMatrix.getValues(mMatrixValues);
            topLeftLimit_X = mMatrixValues[Matrix.MTRANS_X];
            topLeftLimit_Y = mMatrixValues[Matrix.MTRANS_Y];
            topRightLimit_X = mTopLeft_X + mBitmapWidth;
            topRightLimit_Y = topLeftLimit_Y;
            bottomRightLimit_X = topLeftLimit_X + mBitmapWidth;
            bottomRightLimit_Y = topLeftLimit_Y + mBitmapHeight;
            bottomLeftLimit_X = topLeftLimit_X;
            bottomLeftLimit_Y = topLeftLimit_Y + mBitmapHeight;
        }
    }



























    private GestureDetector.OnGestureListener mOnGestureListener = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    };

    private void debug(){
        mCurMatrix.getValues(mMatrixValues);
//        LogUtil.i(TAG,"当前平移量： X轴 = " + mMatrixValues[Matrix.MTRANS_X] + "  ,  Y轴 = " + mMatrixValues[Matrix.MTRANS_Y]);
//        LogUtil.d(TAG,"左上角坐标：  X = " + mTopLeft_X + "  ,  Y = " + mTopLeft_Y);
        LogUtil.w(TAG,"Bitmap的宽度： " + mBitmapWidth + "  ,  Bitmap的高度： " + mBitmapHeight);
    }
}
