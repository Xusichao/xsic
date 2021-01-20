package com.xsic.xsic.illusionTest.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.xsic.xsic.utils.LogUtil;

public class BaseView2 extends View {
    private Paint mPicPaint = new Paint();
    protected ViewSupport mSourceImg;
    protected RectF mShowRect = new RectF();
    protected RectF mOrignalRectF = new RectF();

    public BaseView2(Context context) {
        this(context,null,0);
    }

    public BaseView2(Context context, @Nullable AttributeSet attrs){
        this(context, attrs,0);
    }

    public BaseView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mPicPaint.setAntiAlias(true);
        mSourceImg = new ViewSupport();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom){
        super.onLayout(changed, left, top, right, bottom);
        layoutSourceImg();
    }

    private void layoutSourceImg(){
        float viewHeight = getHeight();
        float viewWidth = getWidth();
        if (mSourceImg != null && mSourceImg.mBitmap != null){
            mSourceImg.mMatrix.reset();
            int bitmapW = mSourceImg.mBitmap.getWidth();
            int bitmapH = mSourceImg.mBitmap.getHeight();

            mOrignalRectF.set(0,0,bitmapW,bitmapH);

            int x = (int) ((viewWidth - bitmapW)/2f);
            int y = (int) ((viewHeight - bitmapH)/2f);

            mSourceImg.mX = x;
            mSourceImg.mY = y;
            mSourceImg.mCenterX = viewWidth/2f;
            mSourceImg.mCenterY = viewHeight/2f;
            mSourceImg.mMatrix.postTranslate(x,y);

            float scaleX = viewWidth*1f/bitmapW;
            float scaleY = viewHeight*1f/bitmapH;

            float finalScale = Math.min(scaleX, scaleY);
            mSourceImg.mScaleX = finalScale;
            mSourceImg.mScaleY = finalScale;
            mSourceImg.mMatrix.postScale(finalScale,finalScale,mSourceImg.mCenterX,mSourceImg.mCenterY);

            float left = mSourceImg.mX;
            float top = mSourceImg.mY;
            float right = mSourceImg.mX + mSourceImg.mBitmap.getWidth()*mSourceImg.mScaleX;
            float bottom = mSourceImg.mY + mSourceImg.mBitmap.getHeight()*mSourceImg.mScaleY;
            mShowRect.set(left,top,right,bottom);
        }
//        float viewHeight = getHeight();
//        float viewWidth = getWidth();
//        if (mSourceImg != null && mSourceImg.mBitmap != null){
//            mSourceImg.mMatrix.reset();
//            float bitmapHeight = mSourceImg.mBitmap.getHeight();
//            float bitmapWidth = mSourceImg.mBitmap.getWidth();
//            //中心点
//            mSourceImg.mCenterX = viewWidth/2f;
//            mSourceImg.mCenterY = viewHeight/2f;
//            //缩放
//            float scaleX = viewWidth/bitmapWidth;
//            float scaleY = viewHeight/bitmapHeight;
//            float realScale = Math.min(scaleX,scaleY);
//            mSourceImg.mScaleX = realScale;
//            mSourceImg.mScaleY = realScale;
//            mSourceImg.mMatrix.postScale(realScale,realScale,getLeft(),getTop());
//            //平移
//            float offsetX = (viewWidth - bitmapWidth*realScale)/2f;
//            float offsetY = (viewHeight - bitmapHeight*realScale)/2f;
//            mSourceImg.mX = offsetX;
//            mSourceImg.mY = offsetY;
//            mSourceImg.mMatrix.postTranslate(offsetX,offsetY);
//
//            float left = mSourceImg.mX;
//            float top = mSourceImg.mY;
//            float right = left + bitmapWidth * realScale;
//            float bottom = top + bitmapHeight * realScale;
//            mShowRect.set(left,top,right,bottom);
//        }
    }

    protected void setImage(Bitmap bitmap){
         mSourceImg.mBitmap = bitmap;
         layoutSourceImg();
         invalidate();
//         requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mSourceImg != null && mSourceImg.mBitmap != null){
            canvas.drawBitmap(mSourceImg.mBitmap,mSourceImg.mMatrix,mPicPaint);
        }
    }
}
