package com.xsic.xsic.ui.s;

import android.animation.Animator;
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

public class ImageViewer extends View {
    //常量
    private final String TAG = "ImageViewer";
    private final float MAX_SCALE = 2.5f;
    private final float MIN_SCALE = 1.0f;
    private final float LIMIT_MAX_SCALE = 4.0f;     //大于这个值不响应缩放
    private final float LIMIT_MIN_SCALE = 0.5f;     //小于这个值不响应缩放
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
        mInitInfo.setmScale(scaleTime);
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
        mLastInfo.setmScale(mCurInfo.getmScale());
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
                if (!isTwoFinger){
                    if (mCurInfo.getmScale() <= MIN_SCALE){
                        translateToCenter_Plus();
                    }
                    setCurInfoToLastInfo();
                }

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
                LogUtil.e(TAG,mCurInfo.getmScale()+"松开手时的放大倍数");
                setCurInfoToLastInfo();
                if (mCurInfo.getmScale() > MAX_SCALE){
                    //zoomSpringBack(true);
                    noAnimationTest(true);
                }else if (mCurInfo.getmScale() < MIN_SCALE){
                    //zoomSpringBack(false);
                    noAnimationTest(false);
                    translateToCenter();
                }
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
        mCurInfo.setmScale(scaleTime * mLastInfo.getmScale());
        LogUtil.d(TAG,"缩放时的真实放大倍数："+mCurInfo.getmScale());

        //大于或小于极限值时不消化缩放
//        if (mCurInfo.getmScale() < LIMIT_MIN_SCALE){
//            mCurInfo.setmScale((float) LIMIT_MIN_SCALE);
//            return;
//        }else if (mCurInfo.getmScale() > LIMIT_MAX_SCALE){
//            mCurInfo.setmScale((float) LIMIT_MAX_SCALE);
//            return;
//        }
        mCurInfo.getmMatrix().reset();
        mCurInfo.getmMatrix().postScale(scaleTime,scaleTime,mCurInfo.getmMiddleOfTwoPointX(),mCurInfo.getmMiddleOfTwoPointY());
        mCurInfo.getmMatrix().setConcat(mCurInfo.getmMatrix(),mLastInfo.getmMatrix());
        invalidate();
    }

    /**
     * 平移
     */
    private void translate(){
        // TODO: 2020/6/9 将操作封装进类里面
        mCurInfo.setmTranslateX(finger_1_X - mCurInfo.getmTouchX());
        mCurInfo.setmTranslateY(finger_1_Y - mCurInfo.getmTouchY());
        //移动中心点
        mCurInfo.setmCenterPointX(mInitInfo.getmCenterPointX()+mCurInfo.getmTranslateX());
        mCurInfo.setmCenterPointY(mInitInfo.getmCenterPointY()+mCurInfo.getmTranslateY());

//        LogUtil.d(TAG,"平移时候的初始中心点X轴与当前中心点x轴对比 = 当前中心点："+mCurInfo.getmCenterPointX()+"  =====  初始中心点："+mInitInfo.getmCenterPointX()+"  =====  上一操作中心点："+mLastInfo.getmCenterPointX());
//        LogUtil.e(TAG,"当前位移 = "+mCurInfo.getmTranslateX()+"，中心点移动的距离跟当前位移一致");

        mCurInfo.getmMatrix().reset();
        mCurInfo.getmMatrix().postTranslate(mCurInfo.getmTranslateX(),mCurInfo.getmTranslateY());
        mCurInfo.getmMatrix().setConcat(mCurInfo.getmMatrix(),mLastInfo.getmMatrix());
        invalidate();
    }

    /**
     * 移动小于最小值时返回正中间
     */
    private void translateToCenter_Plus(){
        //将当前产生的所有位移信息清空，再与之前保存的操作对象相乘
        if (mCurInfo.getmCenterPointX() != mInitInfo.getmCenterPointX() || mCurInfo.getmCenterPointY() != mInitInfo.getmCenterPointY()){
            mCurInfo.getmMatrix().reset();
            mCurInfo.getmMatrix().setConcat(mCurInfo.getmMatrix(),mLastInfo.getmMatrix());
            invalidate();

            mCurInfo.setmCenterPointX(mInitInfo.getmCenterPointX());
            mCurInfo.setmCenterPointY(mInitInfo.getmCenterPointY());
        }
    }

    /**
     * 放大后平移，如果图片边界小于屏幕时回弹
     */
    private void translateToCenter_Plus_Plus(){

    }

    /**
     * 无动画测试
     * @param isBigger
     */
    private void noAnimationTest(boolean isBigger){
        float limitScaleTime;
        if (isBigger){
            limitScaleTime = MAX_SCALE;
        }else {
            limitScaleTime = MIN_SCALE;
        }
        LogUtil.w(TAG,"回弹后倍数："+limitScaleTime/mCurInfo.getmScale());
        mCurInfo.getmMatrix().reset();
        mCurInfo.getmMatrix().postScale(limitScaleTime/mCurInfo.getmScale(),limitScaleTime/mCurInfo.getmScale(),
                mInitInfo.getmCenterPointX(),mInitInfo.getmCenterPointY());
        mCurInfo.getmMatrix().setConcat(mCurInfo.getmMatrix(),mLastInfo.getmMatrix());
        invalidate();


        mCurInfo.setmScale(limitScaleTime);
        //动画结束之后因为有修改了大小，需要调一次ACTION_UP时候调的赋值方法
        setCurInfoToLastInfo();
    }

    /**
     * 缩放小于最小值时返回正中间
     */
    private void translateToCenter(){
        float[] values = new float[9];
        mCurInfo.getmMatrix().getValues(values);

        mCurInfo.getmMatrix().reset();
        mCurInfo.getmMatrix().postTranslate(0-values[Matrix.MTRANS_X],0-values[Matrix.MTRANS_Y]+mInitInfo.getmTranslateY());
        mCurInfo.getmMatrix().setConcat(mCurInfo.getmMatrix(),mLastInfo.getmMatrix());

        mCurInfo.setmTranslateX(0-values[Matrix.MTRANS_X]);
        mCurInfo.setmTranslateY(0-values[Matrix.MTRANS_Y]+mInitInfo.getmTranslateY());
        setCurInfoToLastInfo();
    }


    /**
     * 缩放后回弹
     */
    private void zoomSpringBack(boolean isBigger){
        float limitScaleTime;
        if (isBigger){
            limitScaleTime = MAX_SCALE;
        }else {
            limitScaleTime = MIN_SCALE;
        }
        LogUtil.e(TAG,"动画需要从： "+mCurInfo.getmScale()+" 放大到"+limitScaleTime/mLastInfo.getmScale());
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(mCurInfo.getmScale(),limitScaleTime/mLastInfo.getmScale());
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setDuration(ANIMATION_DURATION);
        valueAnimator.start();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurInfo.getmMatrix().reset();
                mCurInfo.getmMatrix().postScale((float)animation.getAnimatedValue(),(float)animation.getAnimatedValue(),
                        mInitInfo.getmCenterPointX(),mInitInfo.getmCenterPointY());
                mCurInfo.getmMatrix().setConcat(mCurInfo.getmMatrix(),mLastInfo.getmMatrix());
                invalidate();
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurInfo.setmScale(limitScaleTime);
                //动画结束之后因为有修改了大小，需要调一次ACTION_UP时候调的赋值方法
                setCurInfoToLastInfo();
            }
            @Override
            public void onAnimationCancel(Animator animation) {}
            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }
}
