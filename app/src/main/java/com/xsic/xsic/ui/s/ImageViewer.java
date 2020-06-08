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
import com.xsic.xsic.utils.ScreenUtil;

public class ImageViewer extends View {
    //常量
    private final double MAX_SCALE = 2.5;
    private final double MIN_SCALE = 1.0;
    private final int ANIMATION_DURATION = 300;

    private Context mContext;
    private ActionInfo mInitInfo;
    private ActionInfo mLastInfo;
    private ActionInfo mCurInfo;
    private Paint mPaint;
    private Matrix mMatrixNow;
    private Matrix mMatrixLast;
    private GestureDetector mGestureDetector;
    private Drawable mDrawable;
    private Bitmap mBitmap;
    private boolean isFullHeight;


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
        mMatrixNow = new Matrix();
        mMatrixLast = new Matrix();
        mInitInfo = new ActionInfo();
        mGestureDetector = new GestureDetector(mContext,mOnGestureListener);
        mBitmap = ((BitmapDrawable)mDrawable).getBitmap();
        initPlace();
        setmMatrixLast();
    }

    /**
     *
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
        mMatrixNow.postScale(tempScaleTime,tempScaleTime);
        if (heightScaleTime >= widthScaleTime){
            //1、宽度铺满
            isFullHeight = false;
            mMatrixNow.postTranslate(0, (ScreenUtil.getScreenHeight() - mHeightAfterScale)/2);
            tempTranslateX = 0;
            tempTranslateY = (ScreenUtil.getScreenHeight() - mHeightAfterScale)/2;
        }else {
            //2、高度铺满
            isFullHeight = true;
            mMatrixNow.postTranslate((ScreenUtil.getScreenWidth() - mWidthAfterScale)/2,0);
            tempTranslateX = (ScreenUtil.getScreenWidth() - mWidthAfterScale)/2;
            tempTranslateY = 0;
        }
        setmInitInfo(mInitInfo,mMatrixNow,tempScaleTime,tempTranslateX,tempTranslateY);
    }

    /**
     * 保存初始化时的所有信息
     */
    private void setmInitInfo(ActionInfo actionInfo,Matrix matrix,float scaleTime,float translateX,float translateY){
        actionInfo.setmMatrix(matrix);
        actionInfo.setmScaleX(scaleTime);
        actionInfo.setmScaleY(scaleTime);
        actionInfo.setmTranslateX(translateX);
        actionInfo.setmTranslateY(translateY);
        // TODO: 2020/6/9
        actionInfo.setmRotate(0);
        actionInfo.setmCenterPointX(ScreenUtil.getScreenWidth()/2);
        actionInfo.setmCenterPointY(ScreenUtil.getScreenHeight()/2);
        actionInfo.setmDistanceOfPoint(0);
    }

    /**
     * 将当前的矩阵保存在备份矩阵中
     */
    private void setmMatrixLast(){
        float[] values = new float[9];
        mMatrixNow.getValues(values);
        mMatrixLast.setValues(values);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
        canvas.drawBitmap(mBitmap,mMatrixNow,mPaint);
    }

    /**
     * 移动回初始位置
     */
    private void moveToInitPlace(){
        mMatrixNow.reset();
        mMatrixNow.setConcat(mMatrixNow, mInitInfo.getmMatrix());
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        switch (event.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
                break;

            case MotionEvent.ACTION_UP:
                break;

            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() > 1){

                }else {

                }
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                break;

            case MotionEvent.ACTION_POINTER_UP:
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

}
