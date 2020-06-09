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

public class ImageViewer extends View {
    //常量
    private final String TAG = "ImageViewer";
    private final float MAX_SCALE = 2.5f;
    private final float MIN_SCALE = 1.0f;
    private final float LIMIT_MAX_SCALE = 3.0f;     //大于这个值不响应缩放
    private final float LIMIT_MIN_SCALE = 0.6f;     //小于这个值不响应缩放
    private final int ANIMATION_DURATION = 300;

    private Context mContext;
    private ActionInfo mInitInfo;
    private ActionInfo mLastInfo;
    private ActionInfo mCurInfo;
    private Paint mPaint;
    private GestureDetector mGestureDetector;
    private Drawable mDrawable;
    private Bitmap mBitmap;
    private boolean isFullHeight;
    //双指的手指坐标
    private float finger_1_X;
    private float finger_1_Y;
    private float finger_2_X;
    private float finger_2_Y;
    //单指的手指坐标
    private float finger_X;
    private float finger_Y;

    private boolean isTwoFinger = false;        //是否是两根手指，用于区分move操作


    public ImageViewer(Context context) {
        this(context,null,0);
    }

    public ImageViewer(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ImageViewer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initAttrs(attrs, defStyleAttr);
        init();
    }

    private void initAttrs(AttributeSet attrs, int defStyleAttr){
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.ImageViewer,defStyleAttr,0);
        mDrawable = typedArray.getDrawable(R.styleable.ImageViewer_src);
        if (mDrawable == null){
            //设置默认图片，仅供调试
            mDrawable = ContextCompat.getDrawable(mContext,R.drawable.test);
        }
        typedArray.recycle();
    }

    private void init(){
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mInitInfo = new ActionInfo();
        mLastInfo = new ActionInfo();
        mCurInfo = new ActionInfo();
        mGestureDetector = new GestureDetector(mContext,mOnGestureListener);
        mBitmap = ((BitmapDrawable)mDrawable).getBitmap();
        initPlace();
        setCurInfoToLastInfo();
    }

    /**
     * 初始化图片位置
     */
    private void initPlace(){
        //高度的放大系数
        float heightScaleTime = (float) ScreenUtil.getScreenHeight()/(float) mBitmap.getHeight();
        //宽度的放大系数
        float widthScaleTime = (float) ScreenUtil.getScreenWidth()/(float) mBitmap.getWidth();
        //选择小的缩放系数对图片进行初始化
        float tempScaleTime = Math.min(heightScaleTime,widthScaleTime);
        //图片放大之后的尺寸
        float mHeightAfterScale = mBitmap.getHeight() * tempScaleTime;
        float mWidthAfterScale = mBitmap.getWidth() * tempScaleTime;
        float tempTranslateX;
        float tempTranslateY;
        mCurInfo.getmMatrix().postScale(tempScaleTime,tempScaleTime);
        if (heightScaleTime >= widthScaleTime){
            //1、宽度铺满
            isFullHeight = false;
            mCurInfo.getmMatrix().postTranslate(0, (ScreenUtil.getScreenHeight() - mHeightAfterScale)/2);
            tempTranslateX = 0;
            tempTranslateY = (ScreenUtil.getScreenHeight() - mHeightAfterScale)/2;
        }else {
            //2、高度铺满
            isFullHeight = true;
            mCurInfo.getmMatrix().postTranslate((ScreenUtil.getScreenWidth() - mWidthAfterScale)/2,0);
            tempTranslateX = (ScreenUtil.getScreenWidth() - mWidthAfterScale)/2;
            tempTranslateY = 0;
        }
        setmInitInfo(mCurInfo.getmMatrix(),tempScaleTime,tempTranslateX,tempTranslateY,0);
    }

    /**
     * 初始化操作信息
     */
    private void setmInitInfo(Matrix matrix,float scaleTime,float translateX,float translateY,float rotate){
        float[] values = new float[9];
        matrix.getValues(values);
        mInitInfo.getmMatrix().setValues(values);
        mInitInfo.setmScaleX(scaleTime);
        mInitInfo.setmScaleY(scaleTime);
        mInitInfo.setmTranslateX(translateX);
        mInitInfo.setmTranslateY(translateY);
        mInitInfo.setmRotate(rotate);
        mInitInfo.setmCenterPointX(ScreenUtil.getScreenWidth()/2);
        mInitInfo.setmCenterPointY(ScreenUtil.getScreenHeight()/2);
        mInitInfo.setmDistanceOfPoint(0);
        mInitInfo.setmDistanceOfPointFirst(0);
    }

    /**
     * 将当前操作的信息赋予上一个操作
     */
    private void setCurInfoToLastInfo(){
        float[] values = new float[9];
        mCurInfo.getmMatrix().getValues(values);
        mLastInfo.getmMatrix().setValues(values);
        mLastInfo.setmScaleX(mCurInfo.getmScaleX());
        mLastInfo.setmScaleY(mCurInfo.getmScaleY());
        mLastInfo.setmTranslateX(mCurInfo.getmTranslateX());
        mLastInfo.setmTranslateY(mCurInfo.getmTranslateY());
        mLastInfo.setmRotate(mCurInfo.getmRotate());
        mLastInfo.setmCenterPointX(mCurInfo.getmCenterPointX());
        mLastInfo.setmCenterPointY(mCurInfo.getmCenterPointY());
        mLastInfo.setmDistanceOfPoint(mCurInfo.getmDistanceOfPoint());
        mLastInfo.setmDistanceOfPointFirst(mCurInfo.getmDistanceOfPointFirst());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
        canvas.drawBitmap(mBitmap,mCurInfo.getmMatrix(),mPaint);
    }

    /**
     * 移动回初始位置
     */
    private void moveToInitPlace(){
        mCurInfo.getmMatrix().reset();
        mCurInfo.getmMatrix().setConcat(mCurInfo.getmMatrix(), mInitInfo.getmMatrix());
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        switch (event.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
                isTwoFinger = false;
                mCurInfo.setmTouchX(event.getX());
                mCurInfo.setmTouchY(event.getY());
                break;

            case MotionEvent.ACTION_UP:
                setCurInfoToLastInfo();
                break;

            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() > 1){
                    finger_1_X = event.getX(0);
                    finger_1_Y = event.getY(0);
                    finger_2_X = event.getX(1);
                    finger_2_Y = event.getY(1);
                    mCurInfo.setmDistanceOfPoint((float) Math.sqrt(Math.pow(finger_1_X-finger_2_X,2)+Math.pow(finger_1_Y-finger_2_Y,2)));
                    if (mCurInfo.getmDistanceOfPoint() != mCurInfo.getmDistanceOfPointFirst()){
                        zoom();
                    }
                }else {
                    if (!isTwoFinger){
                        finger_1_X = event.getX(0);
                        finger_1_Y = event.getY(0);
                        translate();
                    }
                }
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                isTwoFinger = true;
                finger_1_X = event.getX(0);
                finger_1_Y = event.getY(0);
                finger_2_X = event.getX(1);
                finger_2_Y = event.getY(1);
                mCurInfo.setmDistanceOfPointFirst((float) Math.sqrt(Math.pow(finger_1_X-finger_2_X,2)+Math.pow(finger_1_Y-finger_2_Y,2)));
                mCurInfo.setmMiddleOfTwoPointX((finger_1_X+finger_2_X)/2);
                mCurInfo.setmMiddleOfTwoPointY((finger_1_Y+finger_2_Y)/2);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                setCurInfoToLastInfo();
                break;

            default:break;
        }
        return true;
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

    /**
     * 缩放
     */
    private void zoom(){
        float scaleTime = mCurInfo.getmDistanceOfPoint()/mCurInfo.getmDistanceOfPointFirst();
        mCurInfo.setmScaleX(scaleTime * mLastInfo.getmScaleX());
        mCurInfo.setmScaleY(scaleTime * mLastInfo.getmScaleY());
        LogUtil.d(TAG,mCurInfo.getmScaleX()+"");

        //大于或小于极限值时不消化缩放
        if (mCurInfo.getmScaleX() < LIMIT_MIN_SCALE){
            mCurInfo.setmScaleX((float) LIMIT_MIN_SCALE);
            mCurInfo.setmScaleY((float)LIMIT_MIN_SCALE);
            return;
        }else if (mCurInfo.getmScaleX() > LIMIT_MAX_SCALE){
            mCurInfo.setmScaleX((float) LIMIT_MAX_SCALE);
            mCurInfo.setmScaleY((float)LIMIT_MAX_SCALE);
            return;
        }
        mCurInfo.getmMatrix().reset();
        mCurInfo.getmMatrix().postScale(scaleTime,scaleTime,mCurInfo.getmMiddleOfTwoPointX(),mCurInfo.getmMiddleOfTwoPointY());
        mCurInfo.getmMatrix().setConcat(mCurInfo.getmMatrix(),mLastInfo.getmMatrix());
        invalidate();
    }

    private void translate(){
        // TODO: 2020/6/9 将操作封装进类里面
        mCurInfo.setmTranslateX(finger_1_X - mCurInfo.getmTouchX());
        mCurInfo.setmTranslateY(finger_1_Y - mCurInfo.getmTouchY());
        //移动中心点
        mCurInfo.setmCenterPointX(mCurInfo.getmCenterPointX()+mCurInfo.getmTranslateX());
        mCurInfo.setmCenterPointY(mCurInfo.getmCenterPointY()+mCurInfo.getmTranslateY());

        mCurInfo.getmMatrix().reset();
        mCurInfo.getmMatrix().postTranslate(mCurInfo.getmTranslateX(),mCurInfo.getmTranslateY());
        mCurInfo.getmMatrix().setConcat(mCurInfo.getmMatrix(),mLastInfo.getmMatrix());
        invalidate();
    }
}
