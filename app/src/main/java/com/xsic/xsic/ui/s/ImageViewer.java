package com.xsic.xsic.ui.s;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
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
import android.view.animation.ScaleAnimation;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.xsic.xsic.R;
import com.xsic.xsic.utils.LogUtil;
import com.xsic.xsic.utils.ScreenUtil;

public class ImageViewer extends View {
    private final double MAX_SCALE = 2.5;
    private final double MIN_SCALE = 1.0;
    private final int ANIMATION_DURATION = 300;

    private Context mContext;
    private Paint mPaint;
    private Drawable mDrawable;
    private Bitmap mBitmap;
    private int mDrawableWidth;
    private int mDrawableHeight;
    private GestureDetector mGestureDetector;
    private Matrix mMatrix;
    private float finger_1_X;
    private float finger_1_Y;
    private float finger_2_X;
    private float finger_2_Y;
    //双指的中心点 X
    private float mCenterPoint_X;
    //双指的中心点 Y
    private float mCenterPoint_Y;
    //随着滑动一直变化的两指之间的距离
    private double mDistanceOfPointNow;
    //首次或重新接触屏幕时的两指之间的距离
    private double mDistanceOfPointFirst;
    //初始放大倍数
    private float initScaleTime;
    //初始x轴平移
    private float initTranslateX;
    //初始y轴平移
    private float initTranslateY;
    //初始判断宽度铺满还是高度铺满
    private boolean isFullHeight;
    //记录上次缩放后的矩阵
    private Matrix mMatrixLast;
    //记录每一次的放大倍数
    private double mScaleTime;

    public ImageViewer(Context context) {
        this(context, null, 0, 0);
    }

    public ImageViewer(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0, 0);
    }

    public ImageViewer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ImageViewer(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        initAttrs(attrs, defStyleAttr);
        init();
    }

    /**
     * 定义自定义属性，允许通过xml设置图片
     * @param attrs
     * @param defStyleAttr
     */
    private void initAttrs(AttributeSet attrs, int defStyleAttr){
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.ImageViewer,defStyleAttr,0);
        mDrawable = typedArray.getDrawable(R.styleable.ImageViewer_src);
        if (mDrawable == null){
            //设置默认图片，仅供调试
            mDrawable = ContextCompat.getDrawable(mContext,R.drawable.test);
        }
        mDrawableWidth = mDrawable.getIntrinsicWidth();
        mDrawableHeight = mDrawable.getIntrinsicHeight();
        typedArray.recycle();
    }

    private void init(){
        mGestureDetector = new GestureDetector(mContext,mOnGestureListener);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mMatrix = new Matrix();
        mMatrixLast = new Matrix();
        mBitmap = ((BitmapDrawable)mDrawable).getBitmap();
        setBitmapCenter();
        assginMatrixToMatrixLast();
    }

    /**
     * 将现在的矩阵赋值给上一个用于记录的矩阵
     */
    private void assginMatrixToMatrixLast(){
        float[] values = new float[9];
        mMatrix.getValues(values);
        mMatrixLast.setValues(values);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
        canvas.drawBitmap(mBitmap,mMatrix,mPaint);
    }

    /**
     * 让图片居中并放大
     */
    private void setBitmapCenter(){
        int mBitmapHeight = mBitmap.getHeight();
        int mBitmapWidth = mBitmap.getWidth();
        //高度的放大系数
        float heightScaleTime = (float) ScreenUtil.getScreenHeight()/(float) mBitmapHeight;
        //宽度的放大系数
        float widthScaleTime = (float) ScreenUtil.getScreenWidth()/(float) mBitmapWidth;
        //实际应该放大的系数
        float scaleTime = Math.min(heightScaleTime,widthScaleTime);
        //图片放大之后的尺寸
        float mHeightAfterScale = mBitmapHeight * scaleTime;
        float mWidthAfterScale = mBitmapWidth * scaleTime;
        mMatrix.postScale(scaleTime,scaleTime);
        if (heightScaleTime >= widthScaleTime){
            //1、宽度铺满
            isFullHeight = false;
            mMatrix.postTranslate(0, (ScreenUtil.getScreenHeight() - mHeightAfterScale)/2);
        }else {
            //2、高度铺满
            isFullHeight = true;
            mMatrix.postTranslate((ScreenUtil.getScreenWidth() - mWidthAfterScale)/2,0);
        }
        //记录第一次初始化的参数值，减少计算
        initScaleTime = scaleTime;
        initTranslateX = (ScreenUtil.getScreenWidth() - mWidthAfterScale)/2;
        initTranslateY = (ScreenUtil.getScreenHeight() - mHeightAfterScale)/2;
    }

    /**
     * 将图片设置回初始位置
     */
    private void setBitmapToInitialPlace(){
        mMatrix.postScale(initScaleTime,initScaleTime);
        if (isFullHeight){
            mMatrix.postTranslate(initTranslateX,0);
        }else {
            mMatrix.postTranslate(0,initTranslateY);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        switch (event.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_UP:
                break;

            case MotionEvent.ACTION_DOWN:
                break;

            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() > 1){
                    finger_1_X = event.getX(0);
                    finger_1_Y = event.getY(0);
                    finger_2_X = event.getX(1);
                    finger_2_Y = event.getY(1);
                    setDistanceOfPointNow(finger_1_X,finger_2_X,finger_1_Y,finger_2_Y);
                    if (mDistanceOfPointNow != mDistanceOfPointFirst){
                        zoom();
                    }
                }else {
                    // TODO: 2020/6/3
                }
                break;

            case MotionEvent.ACTION_POINTER_UP:
                if (mScaleTime < MIN_SCALE){
                    //设置回弹
                    springBackAnimation(false);
                    mMatrix.postScale((float) MIN_SCALE,(float) MIN_SCALE);
                    invalidate();
                }else if (mScaleTime > MAX_SCALE){
                    //设置回弹
                    springBackAnimation(true);
                    mMatrix.postScale((float)MAX_SCALE,(float)MAX_SCALE);
                    invalidate();
                }
                assginMatrixToMatrixLast();
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                finger_1_X = event.getX(0);
                finger_1_Y = event.getY(0);
                finger_2_X = event.getX(1);
                finger_2_Y = event.getY(1);
                setCenterPoint(finger_1_X,finger_2_X,finger_1_Y,finger_2_Y);
                setDistanceOfPointFirst(finger_1_X,finger_2_X,finger_1_Y,finger_2_Y);
                break;

            default:
                break;
        }
        return true;
    }

    @SuppressLint("ObjectAnimatorBinding")
    private void springBackAnimation(boolean isBigger){
        if (isBigger){
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(mBitmap, "scaleX", (float) mScaleTime,(float) MAX_SCALE);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(mBitmap, "scaleY", (float) mScaleTime,(float) MAX_SCALE);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(scaleX,scaleY);
            animatorSet.setDuration(ANIMATION_DURATION);
            animatorSet.start();

        }else {
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(mBitmap, "scaleX", (float) mScaleTime,(float) MIN_SCALE);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(mBitmap, "scaleY", (float) mScaleTime,(float) MIN_SCALE);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(scaleX,scaleY);
            animatorSet.setDuration(ANIMATION_DURATION);
            animatorSet.start();
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

    /**
     * 设置双指的中心点
     * @param x1
     * @param x2
     * @param y1
     * @param y2
     */
    private void setCenterPoint(float x1, float x2, float y1, float y2){
        mCenterPoint_X = (x1+x2)/2;
        mCenterPoint_Y = (y1+y2)/2;
    }

    /**
     * 随着滑动，不断设置两点之间的距离
     * @param x1
     * @param x2
     * @param y1
     * @param y2
     */
    private void setDistanceOfPointNow(float x1, float x2, float y1, float y2){
        mDistanceOfPointNow = Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
    }

    /**
     * 首次或重新接触屏幕时，设置两点之间的距离
     * @param x1
     * @param x2
     * @param y1
     * @param y2
     */
    private void setDistanceOfPointFirst(float x1, float x2, float y1, float y2){
        mDistanceOfPointFirst = Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
    }


    /**
     * 缩放操作
     */
    private void zoom(){
        double scaleTime = mDistanceOfPointNow/mDistanceOfPointFirst;
        mScaleTime = scaleTime;
        mMatrix.reset();
        mMatrix.setConcat(mMatrixLast,mMatrix);
        mMatrix.postScale((float) scaleTime,(float) scaleTime,mCenterPoint_X,mCenterPoint_Y);
        invalidate();
    }

    /**
     * 单击/下拉操作 实现退出预览
     */
    private void exit(){

    }
}
