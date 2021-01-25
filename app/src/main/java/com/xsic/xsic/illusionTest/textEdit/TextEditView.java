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

import androidx.annotation.Nullable;

import com.xsic.xsic.R;
import com.xsic.xsic.illusionTest.base.BaseView3;

public class TextEditView extends BaseView3 {
    private Bitmap mTopLeft;
    private Bitmap mTopRight;
    private Bitmap mBottomRight;
    private Bitmap mBottomLeft;

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
        mTextPaint.getTextBounds(mTextItem.mText,0,mTextItem.mText.length()-1, mTextItem.mRect);
        mTextItem.mRect.offset((int)(getWidth() - mTextItem.mRect.width()/2f),(int)(getHeight() - mTextItem.mRect.height()/2f));
        mTextItem.mRect.left = (int) (mTextItem.mRect.left - mTextItem.mGapX);
        mTextItem.mRect.top = (int) (mTextItem.mRect.top - mTextItem.mGapY);
        mTextItem.mRect.right = (int) (mTextItem.mRect.right + mTextItem.mGapX);
        mTextItem.mRect.bottom = (int) (mTextItem.mRect.bottom + mTextItem.mGapY);

        mTextItem.mX = getWidth() - mTextItem.mRect.width()/2f;
        mTextItem.mY = getHeight() - mTextItem.mRect.height()/2f;
        postMatrix(mTextItem);
        mInitItem.set(mTextItem);
    }

    private void init(){
        mTextPaint = new Paint();
        mTextPaint.setColor(mTextItem.mTextColor);
        mTextPaint.setAntiAlias(true);

        mRectPaint = new Paint();
        mRectPaint.setColor(Color.WHITE);
        mRectPaint.setAntiAlias(true);
        PathEffect pathEffect = new DashPathEffect(new float[]{5, 5, 5, 5}, 2);
        mRectPaint.setPathEffect(pathEffect);

        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);

        mTopLeft = BitmapFactory.decodeResource(getResources(), R.drawable.decorate_delete);
        mTopRight = BitmapFactory.decodeResource(getResources(),R.drawable.decorate_reverse);
        mBottomLeft = BitmapFactory.decodeResource(getResources(),R.drawable.decorate_copy);
        mBottomRight = BitmapFactory.decodeResource(getResources(),R.drawable.decorate_rotate);

        mTextItem.mText = "Test";
        mTextItem.mTextSize = getResources().getDimension(R.dimen.dp_18);
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
        canvas.drawRect(mTextItem.mRect,mRectPaint);
        drawBitmap(canvas,mTextItem.mRect,mTextItem.mBitmapSize);
        canvas.drawText(mTextItem.mText,mTextItem.mRect.left,mTextItem.mRect.top,mTextPaint);
    }

    public void setImage(String filePath){
        super.setImage(BitmapFactory.decodeFile(filePath));
    }

}
