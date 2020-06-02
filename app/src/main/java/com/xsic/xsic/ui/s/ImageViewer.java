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
    private Context mContext;
    private final double maxScale = 2.5;
    private final double minScale = 0.8;
    private Paint mPaint;
    private Drawable mDrawable;
    private Bitmap mBitmap;
    private int mDrawableWidth;
    private int mDrawableHeight;
    private GestureDetector mGestureDetector;
    private Matrix mMatrix;
    private int mPointCount;
    private int mCurAction;

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
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
        mBitmap = ((BitmapDrawable)mDrawable).getBitmap();
        setBitmapCenter();
        canvas.drawBitmap(mBitmap,mMatrix,mPaint);
    }

    /**
     * 让图片居中并放大
     */
    private void setBitmapCenter(){
        int mBitmapHeight = mBitmap.getHeight();
        int mBitmapWidth = mBitmap.getWidth();

        float scaleTime = Math.min(((float) ScreenUtil.getScreenHeight()/(float) mBitmapHeight),((float) ScreenUtil.getScreenWidth()/(float) mBitmapWidth));
        mMatrix.setScale(scaleTime,scaleTime);
        //mMatrix.setTranslate();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mPointCount = event.getPointerCount();
        mCurAction = event.getAction();
        switch (event.getAction()){
            case MotionEvent.ACTION_UP:
                break;

            case MotionEvent.ACTION_DOWN:
                break;

            case MotionEvent.ACTION_MOVE:
                break;
        }
        return super.onTouchEvent(event);
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
     * 放大操作
     */
    private void zoomIn(){

    }

    /**
     * 缩小操作
     */
    private void zoomOut(){

    }

    /**
     * 单击/下拉操作 实现退出预览
     */
    private void exit(){

    }
}
