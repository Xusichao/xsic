package com.xsic.xsic.ui.dadad;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
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
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.xsic.xsic.R;
import com.xsic.xsic.ui.xx.ViewerSupport;
import com.xsic.xsic.utils.LogUtil;
import com.xsic.xsic.utils.ScreenUtil;

public class ImageViewer extends View {
    private final String TAG = "ImageViewer";
    private final float MAX_SCALE = 2.5f;
    private final float MIN_SCALE = 1.0f;
    private final float LIMIT_MAX_SCALE = 4.0f;     //大于这个值不响应缩放
    private final float LIMIT_MIN_SCALE = 0.5f;     //小于这个值不响应缩放
    private final int ANIMATION_DURATION = 300;

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

    private ViewerSupport viewerSupport;

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

    private float mInitTopLeft_X;
    private float mInitTopLeft_Y;
    private float mInitBitmapHeight;
    private float mInitBitmapWidth;

    //辅助变量，通常用来保存上一操作的值，在没有多个操作叠加时可重复使用
    //每完成一个操作，需要重置
    private float mSupTransValueX = 0;
    private float mSupTransValueY = 0;
    private float mSupZoomValue = 1f;


    private boolean isDoubleFinger = false;         //是否双指操作
    private boolean isFullHeight = false;           //true：高度铺满     false：宽度铺满
    private boolean isWeakSideTouchScreen = false;  //短的一边是否被放大到接触屏幕

    ValueAnimator animator = ValueAnimator.ofFloat(0,0);

    ValueAnimator zoomAnimator = ValueAnimator.ofFloat(0,0);
    ValueAnimator translateXAnimator = ValueAnimator.ofFloat(0,0);
    ValueAnimator translateYAnimator = ValueAnimator.ofFloat(0,0);

    AnimatorSet animatorSet = new AnimatorSet();


    //动画分开




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

        viewerSupport = new ViewerSupport();
    }

    private void initMatrix(){
        mInitMatrix.getValues(mMatrixValues);
        mCurMatrix.setValues(mMatrixValues);
        mLastMatrix.setValues(mMatrixValues);
    }

    private void setSCALE_RATIO(){
        mCurMatrix.setValues(mMatrixValues);
        viewerSupport.SCALE_RATIO = mMatrixValues[Matrix.MSCALE_X];
    }

    private void initBitmap(){
        //高度的放大系数
        float heightScaleTime = (float) ScreenUtil.getScreenHeight()/(float) mBitmap.getHeight();
        //宽度的放大系数
        float widthScaleTime = (float) ScreenUtil.getScreenWidth()/(float) mBitmap.getWidth();
        //选择小的缩放系数对图片进行初始化
        float initScaleFactor = Math.min(heightScaleTime,widthScaleTime);
        //图片放大之后的尺寸
        float initHeight = mBitmap.getHeight() * initScaleFactor;
        float initWidth = mBitmap.getWidth() * initScaleFactor;

        float initTranslateX;
        float initTranslateY;
        float initRotate = 0;
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
        mInitMatrix.postScale(initScaleFactor,initScaleFactor);
        mInitMatrix.postTranslate(initTranslateX,initTranslateY);
        mInitMatrix.postRotate(initRotate);
        invalidate();


        setInitTopLeft(initTranslateX, initTranslateY);
        setInitBitmapSize(initHeight, initWidth);

        viewerSupport.mBitmapHeight = initHeight;
        viewerSupport.mBitmapWidth = initWidth;
        viewerSupport.mRotate = initRotate;
        viewerSupport.mZoomFactor = initScaleFactor;
        viewerSupport.mTopLeft_X = initTranslateX;
        viewerSupport.mTopLeft_Y = initTranslateY;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
        canvas.drawBitmap(mBitmap,mCurMatrix,mPaint);
    }

    private void setLastMatrix(){
        mCurMatrix.getValues(mMatrixValues);
        mLastMatrix.setValues(mMatrixValues);
    }

    private void setmBitmapSize(float height, float width){
        viewerSupport.mBitmapHeight = height;
        viewerSupport.mBitmapWidth = width;
    }

    private void setInitTopLeft(float x, float y){
        mInitMatrix.getValues(mMatrixValues);
        mInitTopLeft_X = mMatrixValues[Matrix.MTRANS_X];
        mInitTopLeft_Y = mMatrixValues[Matrix.MTRANS_Y];
    }

    private void setInitBitmapSize(float initHeight, float initWidth){
        mInitBitmapHeight = initHeight;
        mInitBitmapWidth = initWidth;
    }

    private void setIsWeakSideTouchedScreen(){
        if (isFullHeight){
            if (viewerSupport.mZoomFactor/viewerSupport.SCALE_RATIO >= ScreenUtil.getScreenWidth()/mInitBitmapWidth){
                isWeakSideTouchScreen = true;
            }else {
                isWeakSideTouchScreen = false;
            }
        }else {
            if (viewerSupport.mZoomFactor/viewerSupport.SCALE_RATIO >= ScreenUtil.getScreenHeight()/mInitBitmapHeight){
                isWeakSideTouchScreen = true;
            }else {
                isWeakSideTouchScreen = false;
            }
        }
    }

    private void setSupMatrix(Matrix matrix){
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
                if (!isDoubleFinger){
                    setLastMatrix();
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
                setLastMatrix();
                zoomSpringBack();
                setLastMatrix();
                break;

            default:break;
        }
        return true;
    }

    /**
     * 单一操作可用，如缩放、平移
     */
    private void setTopLeft(){
        mCurMatrix.getValues(mMatrixValues);
        viewerSupport.mTopLeft_X = mMatrixValues[Matrix.MTRANS_X];
        viewerSupport.mTopLeft_Y = mMatrixValues[Matrix.MTRANS_Y];
    }

    private void setTopLeftManualy(float x, float y){
        viewerSupport.mTopLeft_X = x;
        viewerSupport.mTopLeft_Y = y;
    }

    private void resetSupValue(){
        mSupTransValueX = 0;
        mSupTransValueY = 0;
        mSupZoomValue = 1f;
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

        //放大倍数是相对于上一矩阵
        float zoomFactor = distanceOf2Point / distanceOf2PointFirstTouch;

        //大于或小于极限值时不消化缩放
        mCurMatrix.getValues(mMatrixValues);
        if (mMatrixValues[Matrix.MSCALE_X]/viewerSupport.SCALE_RATIO <= LIMIT_MIN_SCALE){
            mCurMatrix.postScale(1.0f,1.0f,zoomCenter_X, zoomCenter_Y);
            invalidate();
            viewerSupport.mZoomFactor = LIMIT_MIN_SCALE * viewerSupport.SCALE_RATIO;
            return;
        }
        if (mMatrixValues[Matrix.MSCALE_X]/viewerSupport.SCALE_RATIO >= LIMIT_MAX_SCALE){
            mCurMatrix.postScale(1.0f,1.0f,zoomCenter_X, zoomCenter_Y);
            invalidate();
            viewerSupport.mZoomFactor = LIMIT_MAX_SCALE * viewerSupport.SCALE_RATIO;
            return;
        }

        mCurMatrix.reset();
        mCurMatrix.postScale(zoomFactor, zoomFactor, zoomCenter_X, zoomCenter_Y);
        mCurMatrix.setConcat(mCurMatrix,mLastMatrix);
        invalidate();

        viewerSupport.mZoomFactor = mMatrixValues[Matrix.MSCALE_X];

        setTopLeft();
        setmBitmapSize(mBitmap.getHeight() * mMatrixValues[Matrix.MSCALE_Y],mBitmap.getWidth() * mMatrixValues[Matrix.MSCALE_X]);
        setIsWeakSideTouchedScreen();

        //LogUtil.d(TAG,viewerSupport.mTopLeft_X+"");
    }

    private void zoomSpringBack(){
        zoomSpringBackLimitSituation();
    }

    private void zoomSpringBackLimitSituation(){
        float curZoomFactor;
        float targetZoomFactor;
        float zoomFactorRelativeTo1 = viewerSupport.mZoomFactor;
        if (zoomFactorRelativeTo1 >= MIN_SCALE * viewerSupport.SCALE_RATIO && zoomFactorRelativeTo1 <= MAX_SCALE * viewerSupport.SCALE_RATIO){
            return;
        }
        //1、设置  当前缩放倍数、 目标缩放倍数
        if (zoomFactorRelativeTo1 > LIMIT_MIN_SCALE * viewerSupport.SCALE_RATIO && zoomFactorRelativeTo1 < MIN_SCALE * viewerSupport.SCALE_RATIO){
            curZoomFactor = zoomFactorRelativeTo1;
            targetZoomFactor = MIN_SCALE * viewerSupport.SCALE_RATIO;
        }else if (zoomFactorRelativeTo1 > MAX_SCALE * viewerSupport.SCALE_RATIO && zoomFactorRelativeTo1 < LIMIT_MAX_SCALE * viewerSupport.SCALE_RATIO){
            curZoomFactor = zoomFactorRelativeTo1;
            targetZoomFactor = MAX_SCALE * viewerSupport.SCALE_RATIO;
        }else if (zoomFactorRelativeTo1 <= LIMIT_MIN_SCALE * viewerSupport.SCALE_RATIO){
            curZoomFactor = LIMIT_MIN_SCALE * viewerSupport.SCALE_RATIO;
            targetZoomFactor = MIN_SCALE * viewerSupport.SCALE_RATIO;
        }else {
            curZoomFactor = LIMIT_MAX_SCALE * viewerSupport.SCALE_RATIO;
            targetZoomFactor = MAX_SCALE * viewerSupport.SCALE_RATIO;
        }
        //2、设置缩放中心点
        float centerX;
        float centerY;
        if (zoomFactorRelativeTo1 > MAX_SCALE * viewerSupport.SCALE_RATIO){
            //放大倍数大于MAX，缩放中心为两指中点
            centerX = (mTouch_1_X + mTouch_2_X)/2f;
            centerY = (mTouch_1_Y + mTouch_2_Y)/2f;
        }else {
            //缩放中心为图片中心
            centerX = (viewerSupport.mTopLeft_X +(viewerSupport.mTopLeft_X + viewerSupport.mBitmapWidth))/2f;
            centerY = (viewerSupport.mTopLeft_Y +(viewerSupport.mTopLeft_Y + viewerSupport.mBitmapHeight))/2f;
        }
        tempCenterX = centerX;
        tempCenterY = centerY;
        //3、动画
        mSupZoomValue = viewerSupport.mZoomFactor;
        mSupTransValueX = 0;
        mSupTransValueY = 0;
        if (zoomFactorRelativeTo1 > MAX_SCALE * viewerSupport.SCALE_RATIO){

        }else {
            //缩小后回弹

            zoomAnimator = ValueAnimator.ofFloat(curZoomFactor,targetZoomFactor);
            zoomAnimator.setDuration(ANIMATION_DURATION);
            zoomAnimator.setInterpolator(new LinearInterpolator());
            zoomAnimator.addUpdateListener(zoomListener);

//            float offsetX = centerX - (targetZoomFactor*(centerX - viewerSupport.mTopLeft_X))/curZoomFactor;
//            float offsetY = centerY - (targetZoomFactor*(centerY - viewerSupport.mTopLeft_Y))/curZoomFactor;
            float offsetX = centerX - ScreenUtil.getScreenWidth()/2f;
            float offsetY = centerY - ScreenUtil.getScreenHeight()/2f;
            LogUtil.e(TAG,"offsetX = "+offsetX);
            translateXAnimator = ValueAnimator.ofFloat(0, -offsetX);
            translateXAnimator.setDuration(ANIMATION_DURATION);
            translateXAnimator.setInterpolator(new LinearInterpolator());
            translateXAnimator.addUpdateListener(translateXListener);

            translateYAnimator = ValueAnimator.ofFloat(0, -offsetY);
            translateXAnimator.setDuration(ANIMATION_DURATION);
            translateXAnimator.setInterpolator(new LinearInterpolator());
            translateXAnimator.addUpdateListener(translateYListener);

            animatorSet.setDuration(ANIMATION_DURATION);
            animatorSet.setInterpolator(new LinearInterpolator());
            animatorSet.playTogether(zoomAnimator);
            animatorSet.addListener(animatorListener);
            animatorSet.start();
        }

    }

    float tempCenterX;
    float tempCenterY;
    private ValueAnimator.AnimatorUpdateListener zoomListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            mLastMatrix.getValues(mMatrixValues);

            mCurMatrix.reset();
            mCurMatrix.postScale((float)animation.getAnimatedValue()/mMatrixValues[Matrix.MSCALE_X],(float)animation.getAnimatedValue()/mMatrixValues[Matrix.MSCALE_Y],
                    tempCenterX,tempCenterY);
            mCurMatrix.setConcat(mCurMatrix,mLastMatrix);
            invalidate();

            setLastMatrix();
            //setTopLeftManualy();
        }
    };
    float aaa = 0;
    private ValueAnimator.AnimatorUpdateListener translateXListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float[] a = new float[9];
            mCurMatrix.getValues(a);
            float[] b = new float[9];
            mLastMatrix.getValues(b);
            LogUtil.e(TAG,a[Matrix.MTRANS_X] + " , " + b[Matrix.MTRANS_X]);


//            mCurMatrix.reset();
//            mCurMatrix.postTranslate((float)animation.getAnimatedValue() - mSupTransValueX, 0);
//            mCurMatrix.setConcat(mCurMatrix,mLastMatrix);
//            invalidate();

            mCurMatrix.reset();
            mCurMatrix.postTranslate((float)animation.getAnimatedValue() - mSupTransValueX, 0);
            mCurMatrix.setConcat(mCurMatrix,mLastMatrix);
            invalidate();
            aaa += (float)animation.getAnimatedValue() - mSupTransValueX;
            LogUtil.w(TAG,(float)animation.getAnimatedValue() + " , " + mSupTransValueX + " , " +
                    ((float)animation.getAnimatedValue() - mSupTransValueX) + " , " + aaa);

            mSupTransValueX = (float)animation.getAnimatedValue();
            setLastMatrix();
            //setTopLeftManualy();
        }
    };

    private ValueAnimator.AnimatorUpdateListener translateYListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            mCurMatrix.reset();
            mCurMatrix.postTranslate((float)animation.getAnimatedValue() - mSupTransValueY, 0);
            mCurMatrix.setConcat(mCurMatrix,mLastMatrix);
            invalidate();

            mSupTransValueY = (float)animation.getAnimatedValue();
            setLastMatrix();
            //setTopLeftManualy();
        }
    };
    private AnimatorSet.AnimatorListener animatorListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationCancel(Animator animation) {
            super.onAnimationCancel(animation);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            //resetSupValue();
            mCurMatrix.getValues(mMatrixValues);
            LogUtil.e(TAG,mMatrixValues[Matrix.MTRANS_X]+"");
            translateXAnimator.start();

        }
    };




































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
}
