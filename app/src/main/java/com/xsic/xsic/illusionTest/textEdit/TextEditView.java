package com.xsic.xsic.illusionTest.textEdit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;

import com.xsic.xsic.R;
import com.xsic.xsic.illusionTest.base.BaseView3;
import com.xsic.xsic.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class TextEditView extends BaseView3 {
    private Bitmap mTopLeft;
    private Bitmap mTopRight;
    private Bitmap mBottomRight;
    private Bitmap mBottomLeft;

    private int mCurController = TextItem.NONE;
    private float mBaseLine = 0;

    private List<TextItem> mItemList = new ArrayList<>();
    private boolean mIsShowController = true;

    private Paint mTextPaint;
    private Paint mRectPaint;
    private Paint mBitmapPaint;

    private TextItem mTextItem = new TextItem();
    private TextItem mTempItem = new TextItem();
    private TextItem mInitItem = new TextItem();

    public TextEditView(Context context) {
        super(context);
        init();
    }

    public TextEditView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextEditView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        layoutView();
    }

    private void layoutView(){
        mTextItem.mCenterX = getWidth()/2f;
        mTextItem.mCenterY = getHeight()/2f;
        mTextItem.mRect.set(measureText(mTextItem.mText,mTextItem));
        mTextItem.mRect.left = (int) (mTextItem.mRect.left - mTextItem.mGapX);
        mTextItem.mRect.top = (int) (mTextItem.mRect.top - mTextItem.mGapY);
        mTextItem.mRect.right = (int) (mTextItem.mRect.right + mTextItem.mGapX);
        mTextItem.mRect.bottom = (int) (mTextItem.mRect.bottom + mTextItem.mGapY);
        mTextItem.mRect.offset(-mTextItem.mRect.left,-mTextItem.mRect.top);
        mTextItem.mRect.offset((int)((getWidth()/2f - mTextItem.mRect.width()/2f)),(int)((getHeight()/2f - mTextItem.mRect.height()/2f)));
        LogUtil.d("tqtqtqt",mTextItem.mCenterX+","+mTextItem.mCenterY+" == "+mTextItem.mRect.centerX()+","+mTextItem.mRect.centerY());
        mTextItem.mX = getWidth() - mTextItem.mRect.width()/2f;
        mTextItem.mY = getHeight() - mTextItem.mRect.height()/2f;
        postMatrix(mTextItem);
        mInitItem.set(mTextItem);
    }

    private void init(){
        mTextItem.mText = "Test";
        mTextItem.mTextSize = getResources().getDimension(R.dimen.dp_40);

        mTextPaint = new Paint();
        mTextPaint.setColor(mTextItem.mTextColor);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(mTextItem.mTextSize);
        mTextPaint.setTypeface(mTempItem.mTypeface);

        mRectPaint = new Paint();
        mRectPaint.setColor(Color.WHITE);
        mRectPaint.setAntiAlias(true);
        mRectPaint.setStyle(Paint.Style.STROKE);
        PathEffect pathEffect = new DashPathEffect(new float[]{5, 5, 5, 5}, 2);
        mRectPaint.setPathEffect(pathEffect);

        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);

        mTopLeft = BitmapFactory.decodeResource(getResources(), R.drawable.decorate_delete);
        mTopRight = BitmapFactory.decodeResource(getResources(),R.drawable.decorate_reverse);
        mBottomLeft = BitmapFactory.decodeResource(getResources(),R.drawable.decorate_copy);
        mBottomRight = BitmapFactory.decodeResource(getResources(),R.drawable.decorate_rotate);

    }

    private void drawBitmap(Canvas canvas, Rect dst, float size){
        RectF topleft = new RectF(dst.left-size,dst.top-size,dst.left+size,dst.top+size);
        RectF topright = new RectF(dst.right-size,dst.top-size,dst.right+size,dst.top+size);
        RectF bottomright = new RectF(dst.right-size,dst.bottom-size,dst.right+size,dst.bottom+size);
        RectF bottomleft = new RectF(dst.left-size,dst.bottom-size,dst.left+size,dst.bottom+size);
        canvas.drawBitmap(mTopLeft,null,topleft,mBitmapPaint);
        canvas.drawBitmap(mTopRight,null,topright,mBitmapPaint);
        canvas.drawBitmap(mBottomRight,null,bottomright,mBitmapPaint);
        canvas.drawBitmap(mBottomLeft,null,bottomleft,mBitmapPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mIsShowController){
            canvas.drawRect(mTextItem.mRect,mRectPaint);
            drawBitmap(canvas,mTextItem.mRect,mTextItem.mBitmapSize);
        }
        canvas.drawText(mTextItem.mText,mTextItem.mRect.left+mTextItem.mGapX,mTextItem.mRect.top+mBaseLine+mTextItem.mGapY,mTextPaint);
    }

    public void setImage(String filePath){
        super.setImage(BitmapFactory.decodeFile(filePath));
    }

    private int getControlPointAt(float x, float y){
        float l = mTextItem.mRect.centerX() - mTextItem.mRect.width()/2f;
        float t = mTextItem.mRect.centerY() - mTextItem.mRect.height()/2f;
        float r = mTextItem.mRect.centerX() + mTextItem.mRect.width()/2f;
        float b = mTextItem.mRect.centerY() + mTextItem.mRect.height()/2f;
        RectF rectF_TL = new RectF(l-TextItem.CONTROLLER_LENGTH,t-TextItem.CONTROLLER_LENGTH,
                l+TextItem.CONTROLLER_LENGTH,t+TextItem.CONTROLLER_LENGTH);
        RectF rectF_TR = new RectF(r-TextItem.CONTROLLER_LENGTH,t-TextItem.CONTROLLER_LENGTH,
                r+TextItem.CONTROLLER_LENGTH,t+TextItem.CONTROLLER_LENGTH);
        RectF rectF_BR = new RectF(r-TextItem.CONTROLLER_LENGTH,b-TextItem.CONTROLLER_LENGTH,
                r+TextItem.CONTROLLER_LENGTH,b+TextItem.CONTROLLER_LENGTH);
        RectF rectF_BL = new RectF(l-TextItem.CONTROLLER_LENGTH,b-TextItem.CONTROLLER_LENGTH,
                l+TextItem.CONTROLLER_LENGTH,b+TextItem.CONTROLLER_LENGTH);
        if (rectF_TL.contains(x, y)){
            return TextItem.DELETE;
        }
        if (rectF_TR.contains(x, y)){
            return TextItem.REVERSE;
        }
        if (rectF_BR.contains(x, y)){
            return TextItem.ROTATEANDSCALE;
        }
        if (rectF_BL.contains(x, y)){
            return TextItem.ADD;
        }
        return TextItem.NONE;
    }

    private Rect measureText(String text, TextItem textItem){
        Rect result = new Rect();
        if (text!=null && text.length()>0 && textItem!=null){
            Paint paint = new Paint();
            paint.setTextSize(textItem.mTextSize*textItem.mScaleX);
            if (textItem.mTypeface!=null){
                paint.setTypeface(textItem.mTypeface);
            }
            float width = paint.measureText(text);
            float height = paint.descent() - paint.ascent();
            mBaseLine = height/2+(Math.abs(paint.ascent())-paint.descent())/2;
            result.set(0,0,(int)width,(int)height);
        }
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                mCurController = getControlPointAt(event.getX(),event.getY());
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                break;

            case MotionEvent.ACTION_MOVE:
                break;

            case MotionEvent.ACTION_UP:
                break;

            case MotionEvent.ACTION_POINTER_UP:
                break;
        }
        return true;
    }

}
