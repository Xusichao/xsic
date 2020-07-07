package com.xsic.xsic.ui.s;

import android.animation.Animator;
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
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

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
    private float initTopLeft_X;
    private float initTopLeft_Y;

    private float mInitBitmapHeight;
    private float mInitBitmapWidth;
    private float mBitmapHeight;
    private float mBitmapWidth;

    //放大过程中产生的净偏移量
    private float zoomOffset_X;
    private float zoomOffset_Y;

    //辅助作用，用于保存动画中上一帧animationValue的值
    //平移回弹辅助值
    private float mSupOffsetX = 0;
    private float mSupOffsetY = 0;

    //缩放回弹辅助值
    private float mSupZoomOffsetX = 0;
    private float mSupZoomOffsetY = 0;
    private float mSupZoomFactor = 1f;

    //平移动画
    private ValueAnimator transAnimatorX;
    private ValueAnimator transAnimatorY;
    private AnimatorSet transAnimatorSet = new AnimatorSet();

    //缩放动画
    private ValueAnimator zoomAnimator_1;
    private ValueAnimator zoomAnimator_2;
    private ValueAnimator zoomAnimator_3;
    private AnimatorSet zoomAnimatorSet = new AnimatorSet();

    private boolean isDoingTranslateAnimation = false;

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

        setInitTopLeft();
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
                if (!isDoubleFinger){
                    translateSpringBack();
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
                zoomSpringBack();
                setmLastMatrix();
                break;

            default:break;
        }
        return true;
    }

    private void translate(){
        if (transAnimatorX!=null && transAnimatorY!=null){
            if (transAnimatorX.isRunning() || transAnimatorY.isRunning()){
                clearTranslateAnimator();
                LogUtil.e(TAG,"translate里面停动画了");
            }
        }
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
        if (mMatrixValues[Matrix.MSCALE_X]/SCALE_RATIO <= LIMIT_MIN_SCALE){
            mCurMatrix.postScale(1.0f,1.0f,zoomCenter_X, zoomCenter_Y);
            invalidate();
            return;
        }
        if (mMatrixValues[Matrix.MSCALE_X]/SCALE_RATIO >= LIMIT_MAX_SCALE){
            mCurMatrix.postScale(1.0f,1.0f,zoomCenter_X, zoomCenter_Y);
            invalidate();
            return;
        }

        mCurMatrix.reset();
        mCurMatrix.postScale(zoomFactor, zoomFactor, zoomCenter_X, zoomCenter_Y);
        mCurMatrix.setConcat(mCurMatrix,mLastMatrix);
        invalidate();

        setTopLeft();
        mCurMatrix.getValues(mMatrixValues);
        setmBitmapSize(mBitmap.getHeight() * mMatrixValues[Matrix.MSCALE_Y],mBitmap.getWidth() * mMatrixValues[Matrix.MSCALE_X]);
        setIsWeakSideTouchedScreen();
    }

    private void setInitTopLeft(){
        mInitMatrix.getValues(mMatrixValues);
        initTopLeft_X = mMatrixValues[Matrix.MTRANS_X];
        initTopLeft_Y = mMatrixValues[Matrix.MTRANS_Y];
    }

    /**
     * 设置左上角坐标值
     */
    private void setTopLeft(){
        mCurMatrix.getValues(mMatrixValues);
        mTopLeft_X = mMatrixValues[Matrix.MTRANS_X];
        mTopLeft_Y = mMatrixValues[Matrix.MTRANS_Y];
    }

    /**
     * 手动设置左上角坐标值，用于回弹
     */
    private void setTopLeftManual(float x, float y){
        mTopLeft_X = x;
        mTopLeft_Y = y;
    }

    private void clearTranslateAnimator(){
        transAnimatorX.cancel();
        transAnimatorY.cancel();
        transAnimatorX.removeAllUpdateListeners();
        transAnimatorY.removeAllUpdateListeners();
    }

    /**
     * 思路：松开手时，从当前位置的顶点移动到上一位置的顶点
     * 当前位置：已在一开始得到四个顶点位置
     */
    private void translateSpringBack(){
        LogUtil.d(TAG,"调了这里");
        if (transAnimatorX!=null && transAnimatorY!=null){
            if (transAnimatorX.isRunning() || transAnimatorY.isRunning()){
                clearTranslateAnimator();
            }
        }
        // 松手时4个顶点坐标
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
            if (mBitmapHeight == ScreenUtil.getScreenHeight() || mBitmapWidth == ScreenUtil.getScreenWidth()){
                //相当于放大倍数为1.0时
                topLeftLimit_X = initTopLeft_X;
                topLeftLimit_Y = initTopLeft_Y;
                topRightLimit_X = topLeftLimit_X + mBitmapWidth;
                topRightLimit_Y = topLeftLimit_Y;
                bottomRightLimit_X = topLeftLimit_X + mBitmapWidth;
                bottomRightLimit_Y = topLeftLimit_Y + mBitmapHeight;
                bottomLeftLimit_X = topLeftLimit_X;
                bottomLeftLimit_Y = topLeftLimit_Y + mBitmapHeight;
            }
            if (mBitmapWidth > ScreenUtil.getScreenWidth()){
                //图片宽度大于屏幕，有平移的空间
                mLastMatrix.getValues(mMatrixValues);
                topLeftLimit_X = 0;
                topLeftLimit_Y = mMatrixValues[Matrix.MTRANS_Y];
                topRightLimit_X = ScreenUtil.getScreenWidth();
                topRightLimit_Y = topLeftLimit_Y;
                bottomRightLimit_X = ScreenUtil.getScreenWidth();
                bottomRightLimit_Y = topLeftLimit_Y + mBitmapHeight;
                bottomLeftLimit_X = 0;
                bottomLeftLimit_Y = topLeftLimit_Y + mBitmapHeight;
            }
            if (mBitmapHeight > ScreenUtil.getScreenHeight()){
                //图片高度大于屏幕，有平移的空间
                mLastMatrix.getValues(mMatrixValues);
                topLeftLimit_X = mMatrixValues[Matrix.MTRANS_X];
                topLeftLimit_Y = 0;
                topRightLimit_X = topLeftLimit_X + mBitmapWidth;
                topRightLimit_Y = 0;
                bottomRightLimit_X = topLeftLimit_X + mBitmapWidth;
                bottomRightLimit_Y = ScreenUtil.getScreenHeight();
                bottomLeftLimit_X = topLeftLimit_X;
                bottomLeftLimit_Y = ScreenUtil.getScreenHeight();
            }
        }



        float animationValue_X = 0;
        float animationValue_Y = 0;
        //当前某一顶点的XY
        float curPoint_X = 0;
        float curPoint_Y = 0;

        // 不影响，因为左下横坐标和右上纵坐标分别等于左上角x，y，最后指向的点还是左上角
        if (mTopLeft_X > topLeftLimit_X){
            animationValue_X = topLeftLimit_X - mTopLeft_X;
            curPoint_X = mTopLeft_X;
        }
        if (mTopLeft_Y > topLeftLimit_Y){
            animationValue_Y = topLeftLimit_Y - mTopLeft_Y;
            curPoint_Y = mTopLeft_Y;
        }
        if (mTopRight_X < topRightLimit_X){
            animationValue_X = topRightLimit_X - mTopRight_X;
            curPoint_X = mTopRight_X;
        }
        if (mTopRight_Y > topRightLimit_Y){
            animationValue_Y = topRightLimit_Y - mTopRight_Y;
            curPoint_Y = mTopRight_Y;
        }
        if (mBottomRight_X < bottomRightLimit_X  ){
            animationValue_X = bottomRightLimit_X - mBottomRight_X;
            curPoint_X = mBottomRight_X;
        }
        if (mBottomRight_Y < bottomRightLimit_Y){
            animationValue_Y = bottomRightLimit_Y - mBottomRight_Y;
            curPoint_Y = mBottomRight_Y;
        }
        if (mBottomLeft_X > bottomLeftLimit_X){
            animationValue_X = bottomLeftLimit_X - mBottomLeft_X;
            curPoint_X = mBottomLeft_X;
        }
        if (mBottomLeft_Y < bottomLeftLimit_Y){
            animationValue_Y = bottomLeftLimit_Y - mBottomLeft_Y;
            curPoint_Y = mBottomLeft_Y;
        }
//        mCurMatrix.postTranslate(animationValue_X,animationValue_Y);
//        invalidate();

        mSupOffsetX = curPoint_X;
        mSupOffsetY = curPoint_Y;

        transAnimatorX = ValueAnimator.ofFloat(curPoint_X, animationValue_X + curPoint_X);
        transAnimatorY = ValueAnimator.ofFloat(curPoint_Y, animationValue_Y + curPoint_Y);
        transAnimatorSet.setDuration(ANIMATION_DURATION);
        transAnimatorSet.setInterpolator(new LinearInterpolator());
        transAnimatorSet.playTogether(transAnimatorX,transAnimatorY);
        transAnimatorSet.start();

        transAnimatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //LogUtil.d(TAG,"X = "+(float) animation.getAnimatedValue());
                mCurMatrix.postTranslate((float) animation.getAnimatedValue() - mSupOffsetX, 0);
                invalidate();
                mSupOffsetX = (float) animation.getAnimatedValue();
                //需要不断地设置上一矩阵，防止中断动画的时候发生抖动
                setmLastMatrix();
            }
        });
        transAnimatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                LogUtil.d(TAG,"这是Y = " + ((float) animation.getAnimatedValue()) + "，" + mSupOffsetY);
                mCurMatrix.postTranslate(0, (float) animation.getAnimatedValue() - mSupOffsetY);
                invalidate();
                mSupOffsetY = (float) animation.getAnimatedValue();
                //需要不断地设置上一矩阵，防止中断动画的时候发生抖动
                setmLastMatrix();
            }
        });

        transAnimatorX.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                ((ValueAnimator)animation).removeAllUpdateListeners();
                setTopLeftManual(initTopLeft_X,initTopLeft_Y);
                setmLastMatrix();
                //mSupOffsetX = 0;
            }
            @Override
            public void onAnimationCancel(Animator animation) {
                ((ValueAnimator)animation).removeAllUpdateListeners();
                setmLastMatrix();
                //mSupOffsetX = 0;
            }
            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        transAnimatorY.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                ((ValueAnimator)animation).removeAllUpdateListeners();
                LogUtil.d(TAG,"这是end");
                setmLastMatrix();
                setTopLeftManual(initTopLeft_X,initTopLeft_Y);
                //mSupOffsetY = 0;
            }
            @Override
            public void onAnimationCancel(Animator animation) {
                ((ValueAnimator)animation).removeAllUpdateListeners();
                LogUtil.d(TAG,"这是cancel");
                setmLastMatrix();
                //mSupOffsetX = 0;

            }
            @Override
            public void onAnimationRepeat(Animator animation) {}
        });

    }

    /**
     * 缩放后的回弹
     * 1、小于1.0后回弹至1.0、大于2.5回弹
     * 2、缩小后左上角不在初始位置时，动画平移至初始位置
     * 3、放大后上下边距与手机屏幕的距离不对称，修正至对称，只有当没有放大到弱边也接触屏幕的时候才需要
     * PS：注意放大和平移的顺序！！！！
     */
    private void zoomSpringBack(){
        //当前相对于1.0的真实放大倍数
        mCurMatrix.getValues(mMatrixValues);
        float curRealZoomFactor = mMatrixValues[Matrix.MSCALE_X] / SCALE_RATIO;

//        //缩小后左上角不在初始位置时，动画平移至初始位置
//        if(curRealZoomFactor < 1f){
//            float offsetX = 0;
//            float offsetY = 0;
//            if (mTopLeft_X > initTopLeft_X){
//                offsetX = initTopLeft_X - mTopLeft_X;
//            }
//            if (mTopLeft_Y > initTopLeft_Y){
//                offsetY = initTopLeft_Y - mTopLeft_Y;
//            }
//            mCurMatrix.postTranslate(offsetX, offsetY);
//        }
//
//
//
//
//
//        //小于1.0后回弹至1.0、大于2.5回弹
//        float targetZoomFactor = 0;
//        float centerX = 0;
//        float centerY = 0;
//        if (curRealZoomFactor < MIN_SCALE){
//            targetZoomFactor = MIN_SCALE/curRealZoomFactor;
//            centerX = initTopLeft_X;
//            centerY = initTopLeft_Y;
//
//            mCurMatrix.postScale(MIN_SCALE/curRealZoomFactor, MIN_SCALE/curRealZoomFactor,initTopLeft_X,initTopLeft_Y);
//            setmBitmapSize(mInitBitmapHeight,mInitBitmapWidth);
//        }else if (curRealZoomFactor > MAX_SCALE){
//            targetZoomFactor = MAX_SCALE/curRealZoomFactor;
//            centerX = (mTouch_1_X + mTouch_2_X)/2f;
//            centerY = (mTouch_1_Y + mTouch_2_Y)/2f;
//
//            mCurMatrix.postScale(MAX_SCALE/curRealZoomFactor, MAX_SCALE/curRealZoomFactor,centerX,centerY);
//            setmBitmapSize(mInitBitmapHeight,mInitBitmapWidth);
//        }


        zoomSpringBackLimitSituation(curRealZoomFactor);






        //修正上下边距
        if (!isWeakSideTouchScreen && curRealZoomFactor > 1f){
            float mBottomLeftY = mTopLeft_Y + mBitmapHeight;
            if (mTopLeft_Y != (ScreenUtil.getScreenHeight() - mBottomLeftY)){
                float translateOffset = ((ScreenUtil.getScreenHeight() - mBottomLeftY) - mTopLeft_Y)/2f;
                mCurMatrix.postTranslate(0,translateOffset);
                invalidate();
            }
        }

    }

    private ValueAnimator valueAnimator;
    private void xsic(float zoomFactor){
        if (zoomFactor >= MIN_SCALE && zoomFactor <= MAX_SCALE){
            return;
        }
        //true：大于MAX  false：小于MIN
        boolean isBigger = zoomFactor > MAX_SCALE;
        //当前实际的缩放倍数（相对于1.0），受限定值影响
        float curZoomFactor;
        float targetZoomFactor;
        if (zoomFactor > LIMIT_MIN_SCALE && zoomFactor < MIN_SCALE){
            curZoomFactor = zoomFactor;
            targetZoomFactor = MIN_SCALE;
        }else if (zoomFactor > MAX_SCALE && zoomFactor < LIMIT_MAX_SCALE){
            curZoomFactor = zoomFactor;
            targetZoomFactor = MAX_SCALE;
        }else if (zoomFactor <= LIMIT_MIN_SCALE){
            curZoomFactor = LIMIT_MIN_SCALE;
            targetZoomFactor = MIN_SCALE;
        }else {
            curZoomFactor = LIMIT_MAX_SCALE;
            targetZoomFactor = MAX_SCALE;
        }
        //设置缩放中心点
        float centerX;
        float centerY;
        if (zoomFactor > MAX_SCALE){
            //放大倍数大于MAX，缩放中心为两指中点
            centerX = (mTouch_1_X + mTouch_2_X)/2f;
            centerY = (mTouch_1_Y + mTouch_2_Y)/2f;
        }else {
            //缩放中心为图片中心
            centerX = (mTopLeft_X +(mTopLeft_X + mBitmapWidth))/2f;
            centerY = (mTopLeft_Y +(mTopLeft_Y + mBitmapHeight))/2f;
        }

        mSupZoomOffsetX = mTopLeft_X;
        mSupZoomOffsetY = mTopLeft_Y;
        mSupZoomFactor = curZoomFactor;

        valueAnimator = ValueAnimator.ofFloat(0, 1);
        if (isBigger){
            //放大后回弹
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float mZoomFactor = ((float)animation.getAnimatedValue()/mSupZoomOffsetY) * (curZoomFactor/targetZoomFactor);
                    mCurMatrix.postScale(mZoomFactor,mZoomFactor,centerX,centerY);
                    invalidate();
                    setmLastMatrix();
                }
            });
            valueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {}

                @Override
                public void onAnimationEnd(Animator animation) {
                    ((ValueAnimator)animation).removeAllUpdateListeners();
                    setmLastMatrix();
//                    setmBitmapSize(MAX_SCALE * SCALE_RATIO * mBitmapHeight,MAX_SCALE * SCALE_RATIO * mBitmapWidth);
                }
                @Override
                public void onAnimationCancel(Animator animation) {

                }
                @Override
                public void onAnimationRepeat(Animator animation) {}
            });
        }else {
            //缩小后回弹+平移
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float mTranslateX = ((float)animation.getAnimatedValue() - mSupZoomOffsetX) * (mTopLeft_X - initTopLeft_X);
                    float mTranslateY = ((float)animation.getAnimatedValue() - mSupZoomOffsetY) * (mTopLeft_Y - initTopLeft_Y);
                    float mZoomFactor = ((float)animation.getAnimatedValue()/mSupZoomOffsetY) * (curZoomFactor/targetZoomFactor);

                    mCurMatrix.postTranslate(mTranslateX,mTranslateY);
                    mCurMatrix.postScale(mZoomFactor,mZoomFactor);
                    invalidate();
                    setmLastMatrix();
                }
            });
        }

    }

    /**
     * 在边界情况下的缩放回弹
     * 1、小于1.0后回弹至1.0、大于2.5回弹
     * 2、缩小后左上角不在初始位置时，动画平移至初始位置
     */
    private void zoomSpringBackLimitSituation(float zoomFactor){
        if (zoomFactor >= MIN_SCALE && zoomFactor <= MAX_SCALE){
            return;
        }
        mSupZoomOffsetX = mTopLeft_X;
        mSupZoomOffsetY = mTopLeft_Y;
        if (zoomFactor < MIN_SCALE){
            if (mTopLeft_X > initTopLeft_X || mTopLeft_Y > initTopLeft_Y){
                zoomAnimator_1 = ValueAnimator.ofFloat(mTopLeft_X, initTopLeft_X);
                zoomAnimator_2 = ValueAnimator.ofFloat(mTopLeft_Y, initTopLeft_Y);
                zoomAnimator_1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
//                        LogUtil.d(TAG,"平移：X = "+((float)animation.getAnimatedValue() - mSupZoomOffsetX) + " ，未减："+(float)animation.getAnimatedValue());
//                        mCurMatrix.getValues(mMatrixValues);
//                        LogUtil.e(TAG,"当前的平移："+mMatrixValues[Matrix.MTRANS_X] + "数值相加后：" +
//                                (mMatrixValues[Matrix.MTRANS_X] + ((float)animation.getAnimatedValue() - mSupZoomOffsetX)));

                        mCurMatrix.postTranslate((float)animation.getAnimatedValue() - mSupZoomOffsetX,0);//
                        mSupZoomOffsetX = (float)animation.getAnimatedValue();
                        invalidate();
                        setmLastMatrix();
                    }
                });
                zoomAnimator_2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
//                        LogUtil.d(TAG,"平移：Y = "+((float)animation.getAnimatedValue() - mSupZoomOffsetY) + " ，未减："+(float)animation.getAnimatedValue());
//                        mCurMatrix.getValues(mMatrixValues);
//                        LogUtil.e(TAG,"当前的平移："+mMatrixValues[Matrix.MTRANS_Y] + "数值相加后：" +
//                                (mMatrixValues[Matrix.MTRANS_Y] + ((float)animation.getAnimatedValue() - mSupZoomOffsetY)));

                        mCurMatrix.postTranslate(0, (float)animation.getAnimatedValue() - mSupZoomOffsetY);
                        invalidate();
                        mSupZoomOffsetY = (float)animation.getAnimatedValue();
                        setmLastMatrix();
                    }
                });
                zoomAnimator_1.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {}
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ((ValueAnimator)animation).removeAllUpdateListeners();
                        setmLastMatrix();
                        setTopLeftManual(initTopLeft_X,initTopLeft_Y);
                    }
                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }
                    @Override
                    public void onAnimationRepeat(Animator animation) {}
                });
                zoomAnimator_2.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {}
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ((ValueAnimator)animation).removeAllUpdateListeners();
                        setmLastMatrix();
                        setTopLeftManual(initTopLeft_X,initTopLeft_Y);
                    }
                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }
                    @Override
                    public void onAnimationRepeat(Animator animation) {}
                });
            }
        }




        if (zoomFactor < MIN_SCALE || zoomFactor > MAX_SCALE){
            float curZoomFactor = 0;
            float targetZoomFactor = 1f;
            //缩放中心点设置图片中心
            float centerX = (mTopLeft_X +(mTopLeft_X + mBitmapWidth))/2f;//(mTouch_1_X + mTouch_2_X)/2f;
            float centerY = (mTopLeft_Y +(mTopLeft_Y + mBitmapHeight))/2f;//(mTouch_1_Y + mTouch_2_Y)/2f;
            if (zoomFactor < MIN_SCALE){
                /**
                 * 当前缩放倍数分为：
                 * 1：大于不响应缩放的限定值，同时小于最小值  => 取当前实际缩放值
                 * 2：小于不响应缩放的限定值 => 取限定值
                 * 同理用于放大回弹
                 */
                if (zoomFactor > LIMIT_MIN_SCALE && zoomFactor < MIN_SCALE){
                    curZoomFactor = zoomFactor;
                }else {
                    curZoomFactor = LIMIT_MIN_SCALE;
                }
                targetZoomFactor = MIN_SCALE;
                centerX = initTopLeft_X;
                centerY = initTopLeft_Y;
            }else{
                if (zoomFactor > MAX_SCALE && zoomFactor < LIMIT_MAX_SCALE){
                    curZoomFactor = zoomFactor;
                }else {
                    curZoomFactor = LIMIT_MAX_SCALE;
                }
                targetZoomFactor = MAX_SCALE;
            }
            mSupZoomFactor = curZoomFactor;
            zoomAnimator_3 = ValueAnimator.ofFloat(curZoomFactor, targetZoomFactor);
            float finalCenterX = centerX;
            float finalCenterY = centerY;
            zoomAnimator_3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
//                    mCurMatrix.getValues(mMatrixValues);
//                    LogUtil.e(TAG,"原缩放："+mMatrixValues[Matrix.MSCALE_X] + "  ， 传进来的："+zoomFactor);
//                    LogUtil.d(TAG,"缩放："+(float)animation.getAnimatedValue()/mSupZoomFactor);

                    mCurMatrix.postScale((float)animation.getAnimatedValue()/mSupZoomFactor,(float)animation.getAnimatedValue()/mSupZoomFactor,
                            finalCenterX, finalCenterY);
                    invalidate();
                    mSupZoomFactor = (float)animation.getAnimatedValue();
                    setmLastMatrix();
                }
            });
            zoomAnimator_3.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {}
                @Override
                public void onAnimationEnd(Animator animation) {
                    ((ValueAnimator)animation).removeAllUpdateListeners();
                    setmLastMatrix();
                    setmBitmapSize(mInitBitmapHeight,mInitBitmapWidth);
                }
                @Override
                public void onAnimationCancel(Animator animation) {

                }
                @Override
                public void onAnimationRepeat(Animator animation) {}
            });
        }

        zoomAnimatorSet.setDuration(ANIMATION_DURATION);
        zoomAnimatorSet.setInterpolator(new LinearInterpolator());
        if (zoomFactor > MAX_SCALE){
            zoomAnimatorSet.playTogether(zoomAnimator_3);
        }else {
            zoomAnimatorSet.playTogether(zoomAnimator_1, zoomAnimator_2,zoomAnimator_3);//
        }
        zoomAnimatorSet.start();
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
//        mCurMatrix.getValues(mMatrixValues);
        mLastMatrix.getValues(mMatrixValues);
        LogUtil.i(TAG,"当前平移量： X轴 = " + mMatrixValues[Matrix.MTRANS_X] + "  ,  Y轴 = " + mMatrixValues[Matrix.MTRANS_Y]);
//        LogUtil.d(TAG,"左上角坐标：  X = " + mTopLeft_X + "  ,  Y = " + mTopLeft_Y);
//        LogUtil.w(TAG,"Bitmap的宽度： " + mBitmapWidth + "  ,  Bitmap的高度： " + mBitmapHeight);
//        LogUtil.w(TAG,"放大倍数："+mMatrixValues[Matrix.MSCALE_X]);
    }
}
