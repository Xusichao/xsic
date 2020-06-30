package com.xsic.xsic.ui.b;

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

public class ImageViewer3 extends View {
    public static final int TRANSLATE = 1;
    public static final int ZOOM = 2;
    public static final int ROTATE = 3;

    //常量
    private final String TAG = "ImageViewer";
    private final float MAX_SCALE = 2.5f;
    private final float MIN_SCALE = 1.0f;
    private final float LIMIT_MAX_SCALE = 4.0f;     //大于这个值不响应缩放
    private final float LIMIT_MIN_SCALE = 0.5f;     //小于这个值不响应缩放
    private final int ANIMATION_DURATION = 300;

    private Context mContext;
    private ActionInfo3 mInitInfo;
    private ActionInfo3 mLastInfo;
    private ActionInfo3 mCurInfo;
    private Paint mPaint;
    private GestureDetector mGestureDetector;
    private Drawable mDrawable;
    private Bitmap mBitmap;

    //双指的手指坐标
    private float finger_1_X;
    private float finger_1_Y;
    private float finger_2_X;
    private float finger_2_Y;
    //单指的手指坐标
    private float finger_X;
    private float finger_Y;

    private boolean isTwoFinger = false;        //是否是两根手指，用于区分move操作


    public ImageViewer3(Context context) {
        this(context,null,0);
    }

    public ImageViewer3(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ImageViewer3(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
            mDrawable = ContextCompat.getDrawable(mContext,R.drawable.test1);
        }
        typedArray.recycle();
    }

    private void init(){
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mInitInfo = new ActionInfo3();
        mLastInfo = new ActionInfo3();
        mCurInfo = new ActionInfo3();
        mGestureDetector = new GestureDetector(mContext,mOnGestureListener);
        mBitmap = ((BitmapDrawable)mDrawable).getBitmap();
        initPlace();
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
        float initScaleTime = Math.min(heightScaleTime,widthScaleTime);
        //图片放大之后的尺寸
        float initHeight = mBitmap.getHeight() * initScaleTime;
        float initWidth = mBitmap.getWidth() * initScaleTime;
        float initTranslateX;
        float initTranslateY;
        mCurInfo.getmMatrix().postScale(initScaleTime,initScaleTime);
        if (heightScaleTime >= widthScaleTime){
            //1、宽度铺满
            mCurInfo.getmMatrix().postTranslate(0, (ScreenUtil.getScreenHeight() - initHeight)/2);
            initTranslateX = 0;
            initTranslateY = (ScreenUtil.getScreenHeight() - initHeight)/2;
        }else {
            //2、高度铺满
            mCurInfo.getmMatrix().postTranslate((ScreenUtil.getScreenWidth() - initWidth)/2,0);
            initTranslateX = (ScreenUtil.getScreenWidth() - initWidth)/2;
            initTranslateY = 0;
        }
        setmInitInfo(mCurInfo.getmMatrix(),initScaleTime,initHeight,initWidth,initTranslateX,initTranslateY);
        //将mInit的所有属性赋给mCur，再由mCur去赋值mLast
        setmCurInfo();
        setCurInfoToLastInfo();
    }

    /**
     * 初始化操作信息
     */
    private void setmInitInfo(Matrix matrix,float scaleTime,float height,float width,float translateX,float translateY){
        float[] values = new float[9];
        matrix.getValues(values);
        mInitInfo.setmMatrix(values);
        mInitInfo.setmRealScale(scaleTime);
        mInitInfo.setmScale(1.0f);
        mInitInfo.setmBitmapHeight(height);
        mInitInfo.setmBitmapWidth(width);
        mInitInfo.setmTranslateX(translateX);
        mInitInfo.setmTranslateY(translateY);
        mInitInfo.setmRotate(0);
        mInitInfo.setmTopPoint(ScreenUtil.getScreenHeight()/2f - height/2);
        mInitInfo.setmRightPoint(ScreenUtil.getScreenWidth()/2f + width/2);
        mInitInfo.setmBottomPoint(ScreenUtil.getScreenHeight()/2f + height/2);
        mInitInfo.setmLeftPoint(ScreenUtil.getScreenWidth()/2f - width/2);

        mInitInfo.setmTopLeft(mInitInfo.getmLeftPoint(),mInitInfo.getmTopPoint());
        mInitInfo.setmTopRight(mInitInfo.getmRightPoint(),mInitInfo.getmTopPoint());
        mInitInfo.setmBottomLeft(mInitInfo.getmLeftPoint(),mInitInfo.getmBottomPoint());
        mInitInfo.setmBottomRight(mInitInfo.getmRightPoint(),mInitInfo.getmBottomPoint());
    }

    /**
     * 初始化完mInit后，将值赋给mCur，以初始化mCur
     */
    private void setmCurInfo(){
        float[] values = new float[9];
        mInitInfo.getmMatrix().getValues(values);
        mCurInfo.setmMatrix(values);
        mCurInfo.setmRealScale(mInitInfo.getmRealScale());
        mCurInfo.setmScale(mInitInfo.getmScale());
        mCurInfo.setmBitmapHeight(mInitInfo.getmBitmapHeight());
        mCurInfo.setmBitmapWidth(mInitInfo.getmBitmapWidth());
        mCurInfo.setmTranslateX(mInitInfo.getmTranslateX());
        mCurInfo.setmTranslateY(mInitInfo.getmTranslateY());
        mCurInfo.setmRotate(mInitInfo.getmRotate());
        mCurInfo.setmTopPoint(mInitInfo.getmTopPoint());
        mCurInfo.setmRightPoint(mInitInfo.getmRightPoint());
        mCurInfo.setmBottomPoint(mInitInfo.getmBottomPoint());
        mCurInfo.setmLeftPoint(mInitInfo.getmLeftPoint());
        mCurInfo.setmTopLeft(mInitInfo.getmTopLeft_X(),mInitInfo.getmTopLeft_Y());
        mCurInfo.setmTopRight(mInitInfo.getmTopRight_X(),mInitInfo.getmTopRight_Y());
        mCurInfo.setmBottomLeft(mInitInfo.getmBottomLeft_X(),mInitInfo.getmBottomLeft_Y());
        mCurInfo.setmBottomRight(mInitInfo.getmBottomRight_X(),mInitInfo.getmBottomRight_Y());

//        LogUtil.w(TAG,"初始化 ！ 缩放比例："+mCurInfo.getmScale());
//        LogUtil.d(TAG,"初始化 ! 后四条边中点左边 = 左："+mCurInfo.getmLeftPoint()+", 上："+mCurInfo.getmTopPoint()+", 右："
//                +mCurInfo.getmRightPoint()+", 下："+mCurInfo.getmBottomPoint()+", ");
//        LogUtil.v(TAG,"初始化 ! 后图片大小："+mCurInfo.getmBitmapWidth()+", "+mCurInfo.getmBitmapHeight());
        LogUtil.i(TAG,"初始化 ! 后四个顶点坐标：左上 = "+mCurInfo.getmTopLeft_X()+"， "+mCurInfo.getmTopLeft_Y()
                +" ， 右上 = "+mCurInfo.getmTopRight_X()+"， "+mCurInfo.getmTopRight_Y()
                +" ， 右下 = "+mCurInfo.getmBottomRight_X()+"， "+mCurInfo.getmBottomRight_Y()
                +" ， 左下 = "+mCurInfo.getmBottomLeft_X()+"， "+mCurInfo.getmBottomLeft_Y());

    }


    /**
     * 当每一次操作完成时
     * 将当前操作mCur备份到上一操作mLast
     */
    private void setCurInfoToLastInfo(){
        float[] values = new float[9];
        mCurInfo.getmMatrix().getValues(values);
        mLastInfo.setmMatrix(values);
        mLastInfo.setmRealScale(mCurInfo.getmRealScale());
        mLastInfo.setmScale(mCurInfo.getmScale());
        mLastInfo.setmBitmapHeight(mCurInfo.getmBitmapHeight());
        mLastInfo.setmBitmapWidth(mCurInfo.getmBitmapWidth());
        mLastInfo.setTempTranslateX(mCurInfo.getTempTranslateX());
        mLastInfo.setTempTranslateY(mCurInfo.getTempTranslateY());
        mLastInfo.setmTranslateX(mCurInfo.getmTranslateX());
        mLastInfo.setmTranslateY(mCurInfo.getmTranslateY());
        mLastInfo.setmRotate(mCurInfo.getmRotate());
        mLastInfo.setmTopPoint(mCurInfo.getmTopPoint());
        mLastInfo.setmRightPoint(mCurInfo.getmRightPoint());
        mLastInfo.setmBottomPoint(mCurInfo.getmBottomPoint());
        mLastInfo.setmLeftPoint(mCurInfo.getmLeftPoint());
        mLastInfo.setmTopLeft(mCurInfo.getmTopLeft_X(),mCurInfo.getmTopLeft_Y());
        mLastInfo.setmTopRight(mCurInfo.getmTopRight_X(),mCurInfo.getmTopRight_Y());
        mLastInfo.setmBottomLeft(mCurInfo.getmBottomLeft_X(),mCurInfo.getmBottomLeft_Y());
        mLastInfo.setmBottomRight(mCurInfo.getmBottomRight_X(),mCurInfo.getmBottomRight_Y());
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
        canvas.drawBitmap(mBitmap,mCurInfo.getmMatrix(),mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        switch (event.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
                LogUtil.d(TAG,"按下时的x轴坐标："+event.getX()+", y轴坐标："+event.getY());
                isTwoFinger = false;
                mCurInfo.setmTouchX(event.getX());
                mCurInfo.setmTouchY(event.getY());
                break;

            case MotionEvent.ACTION_UP:
                if (!isTwoFinger){
                    //回弹
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
                mCurInfo.setmMiddleOfTwoPointX((finger_1_X+finger_2_X)/2f);
                mCurInfo.setmMiddleOfTwoPointY((finger_1_Y+finger_2_Y)/2f);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                setCurInfoToLastInfo();
                break;

            default:break;
        }
        return true;
    }


    /**
     * 缩放
     */
    private void zoom(){
        float scaleTime = mCurInfo.getmDistanceOfPoint()/mCurInfo.getmDistanceOfPointFirst();
        mCurInfo.setmRealScale(scaleTime * mLastInfo.getmRealScale());
        mCurInfo.setmScale(scaleTime * mLastInfo.getmScale());
        setPointValue();

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
        //为了统一处理，偏移量全部加上初始偏移
        mCurInfo.setmTranslateX((mInitInfo.getmTranslateX() + finger_1_X) - mCurInfo.getmTouchX());
        mCurInfo.setmTranslateY((mInitInfo.getmTranslateY() + finger_1_Y) - mCurInfo.getmTouchY());
        setPointValue();

        mCurInfo.getmMatrix().reset();
        mCurInfo.getmMatrix().postTranslate(mCurInfo.getmTranslateX() - mInitInfo.getmTranslateX(),
                mCurInfo.getmTranslateY() - mInitInfo.getmTranslateY());
        mCurInfo.getmMatrix().setConcat(mCurInfo.getmMatrix(),mLastInfo.getmMatrix());
        invalidate();
    }

    /**
     * 平移回弹
     */
    private void translateSpringBack(){
        //区分高度铺满和宽度铺满
        if (mCurInfo.getmLeftPoint() > 0){
            mCurInfo.getmMatrix().reset();
        }
    }


    /**
     * 以左上角顶点为基准点
     * 在缩放、偏移、旋转等操作时不断修改
     * 1、图片大小
     * 2、四条边中点坐标
     * 3、四个顶点坐标
     */
    private void setPointValue(){
        float values[] = new float[9];
        mCurInfo.getmMatrix().getValues(values);
        //这个偏移量包含了初始偏移，所以计算时需要减去初始偏移
        float tempTranslateX = values[Matrix.MTRANS_X] - mInitInfo.getmTranslateX();
        float tempTranslateY = values[Matrix.MTRANS_Y] - mInitInfo.getmTranslateY();
        mCurInfo.setTempTranslateX(tempTranslateX);
        mCurInfo.setTempTranslateY(tempTranslateY);

        //1、设置图片大小
        mCurInfo.setmBitmapWidth(mInitInfo.getmBitmapWidth()*(mCurInfo.getmRealScale()/mInitInfo.getmRealScale()));
        mCurInfo.setmBitmapHeight(mInitInfo.getmBitmapHeight()*(mCurInfo.getmRealScale()/mInitInfo.getmRealScale()));

        //2、设置四个顶点坐标
        mCurInfo.setmTopLeft(mLastInfo.getmTopLeft_X()+tempTranslateX , mLastInfo.getmTopLeft_Y()+tempTranslateY);

        mCurInfo.setmTopRight(mCurInfo.getmBitmapWidth() - Math.abs(mCurInfo.getmTopLeft_X()),
                mCurInfo.getmTopLeft_Y());

        mCurInfo.setmBottomRight(mCurInfo.getmTopRight_X(),
                mCurInfo.getmBitmapHeight() + mCurInfo.getmTopLeft_Y());

        mCurInfo.setmBottomLeft(mCurInfo.getmTopLeft_X(),
                mCurInfo.getmBottomRight_Y());

        //3、设置四条边的的坐标
        mCurInfo.setmTopPoint(mCurInfo.getmTopLeft_Y());
        mCurInfo.setmRightPoint(mCurInfo.getmTopRight_X());
        mCurInfo.setmBottomPoint(mCurInfo.getmBottomRight_Y());
        mCurInfo.setmLeftPoint(mCurInfo.getmBottomLeft_X());



//        LogUtil.i(TAG,"原始："+values[Matrix.MTRANS_X]+"，"+values[Matrix.MTRANS_Y]);
//        LogUtil.d(TAG,"偏移："+tempTranslateX+"，"+tempTranslateY);
//        LogUtil.i(TAG,"产生的临时偏移量："+tempTranslateX+", "+tempTranslateY);
//        LogUtil.v(TAG,"重新设置 ！ 缩放比例："+mCurInfo.getmScale() + " ， 比例相除："+mCurInfo.getmScale()/mInitInfo.getmScale());
//        LogUtil.d(TAG,"初始化 ! 后四条边中点左边 = 左："+mCurInfo.getmLeftPoint()+", 上："+mCurInfo.getmTopPoint()+", 右："
//                +mCurInfo.getmRightPoint()+", 下："+mCurInfo.getmBottomPoint()+", ");
//        LogUtil.d(TAG,"重新设置后中点坐标："+mCurInfo.getmCenterPointX()+", "+mCurInfo.getmCenterPointY());
//        LogUtil.w(TAG,"重新设置 ! 后图片大小："+mCurInfo.getmBitmapWidth()+", "+mCurInfo.getmBitmapHeight());
//        LogUtil.d(TAG,"重新设置 ! 后四个顶点坐标：左上 = "+mCurInfo.getmTopLeft_X()+"， "+mCurInfo.getmTopLeft_Y()
//                +" ， 右上 = "+mCurInfo.getmTopRight_X()+"， "+mCurInfo.getmTopRight_Y()
//                +" ， 右下 = "+mCurInfo.getmBottomRight_X()+"， "+mCurInfo.getmBottomRight_Y()
//                +" ， 左下 = "+mCurInfo.getmBottomLeft_X()+"， "+mCurInfo.getmBottomLeft_Y());
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
