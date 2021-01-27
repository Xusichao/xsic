package com.xsic.xsic.illusionTest.textEdit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;

import com.xsic.xsic.R;
import com.xsic.xsic.illusionTest.base.BaseView3;
import com.xsic.xsic.illusionTest.textEdit.options.IEdit;

import java.util.ArrayList;
import java.util.List;

public class TextEditView extends BaseView3 {
    private static final float itemOffset = 30;

    private Bitmap mTopLeft;
    private Bitmap mTopRight;
    private Bitmap mBottomRight;
    private Bitmap mBottomLeft;

    private int mCurController = TextItem.NONE;
    private IEdit mEditType = null;

    private List<TextItem> mItemList = new ArrayList<>();
    private boolean mIsShowController = true;
    private boolean mIsHandlingMorePoint = false;
    private boolean mIsReverse = false;

    private Paint mTextPaint;
    private Paint mRectPaint;
    private Paint mBitmapPaint;

    private TextItem mTextItem = new TextItem();
    private TextItem mTempItem = new TextItem();
    private TextItem mInitItem = new TextItem();
    private TextItem mCurTextItem = null;

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
        layoutTextItem(mTextItem);
        mInitItem.set(mTextItem);
        mCurTextItem.set(mTextItem);
    }

    private void layoutTextItem(TextItem item){
        item.mCenterX = getWidth()/2f;
        item.mCenterY = getHeight()/2f;
        measureText(item.mText,item);
        mTextItem.mRect.left = (int) (mTextItem.mRect.left - mTextItem.mGapX);
        mTextItem.mRect.top = (int) (mTextItem.mRect.top - mTextItem.mGapY);
        mTextItem.mRect.right = (int) (mTextItem.mRect.right + mTextItem.mGapX);
        mTextItem.mRect.bottom = (int) (mTextItem.mRect.bottom + mTextItem.mGapY);
        mTextItem.mRect.offset(-mTextItem.mRect.left,-mTextItem.mRect.top);
        mTextItem.mRect.offset((int)((getWidth()/2f - mTextItem.mRect.width()/2f)),(int)((getHeight()/2f - mTextItem.mRect.height()/2f)));
        mTextItem.mX = mTextItem.mRect.left;
        mTextItem.mY = mTextItem.mRect.top;
        postMatrix(mTextItem);
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

        mItemList.add(mTextItem);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.rotate(mTextItem.mRotate,mTextItem.mCenterX,mTextItem.mCenterY);
        if (mItemList!=null && mItemList.size()>0){
            for (TextItem drawItem : mItemList){
                drawText(canvas,drawItem);
                if (mIsShowController){
                    drawControlRect(canvas,drawItem.mRect,drawItem.mBitmapSize,drawItem);
                }
            }
        }
        canvas.restore();
    }

    private void drawText(Canvas canvas,TextItem item){
        measureText(item.mText,item);
        float left = item.mCenterX - item.mRect.width()/2f;
        float top = item.mCenterY - item.mRect.height()/2f - mTextPaint.getFontMetrics().ascent;
        mTextPaint.setColor(item.mTextColor);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(item.mTextSize*item.mScaleX);
        mTextPaint.setTypeface(item.mTypeface);
        canvas.drawText(item.mText,left,top,mTextPaint);
    }

    private void drawControlRect(Canvas canvas, RectF dst, float size, TextItem item){
        dst.offset(item.mCenterX-dst.width()/2f,item.mCenterY - dst.height()/2f);
        dst.left = (int) (dst.left - mTextItem.mGapX);
        dst.top = (int) (dst.top - mTextItem.mGapY);
        dst.right = (int) (dst.right + mTextItem.mGapX);
        dst.bottom = (int) (dst.bottom + mTextItem.mGapY);
        canvas.drawRect(dst,mRectPaint);


        RectF topleft = new RectF(dst.left-size,dst.top-size,dst.left+size,dst.top+size);
        RectF topright = new RectF(dst.right-size,dst.top-size,dst.right+size,dst.top+size);
        RectF bottomright = new RectF(dst.right-size,dst.bottom-size,dst.right+size,dst.bottom+size);
        RectF bottomleft = new RectF(dst.left-size,dst.bottom-size,dst.left+size,dst.bottom+size);
        canvas.drawBitmap(mTopLeft,null,topleft,mBitmapPaint);
        canvas.drawBitmap(mTopRight,null,topright,mBitmapPaint);
        canvas.drawBitmap(mBottomRight,null,bottomright,mBitmapPaint);
        canvas.drawBitmap(mBottomLeft,null,bottomleft,mBitmapPaint);
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

    private RectF measureText(String text, TextItem textItem){
        RectF result = new RectF();
        if (text!=null && text.length()>0 && textItem!=null){
            Paint paint = new Paint();
            paint.setTextSize(textItem.mTextSize*textItem.mScaleX);
            if (textItem.mTypeface!=null){
                paint.setTypeface(textItem.mTypeface);
            }
            float width = paint.measureText(text);
            float height = paint.descent() - paint.ascent();
            result.set(0,0,(int)width,(int)height);
            textItem.mRect.set(result);
        }
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                mEditType = getEditType(event.getX(),event.getY());
                if (mEditType!=null){
                    mEditType.onDown(mTextItem,event);
                }
                mTempItem = mTextItem.clone();
                initTranslate(event.getX(),event.getY());
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                mIsHandlingMorePoint = true;
                mTempItem = mTextItem.clone();
                initMorePointTranslate(mTempItem,event);
                initMorePointScale(event);
                initRotate(event.getX(0),event.getY(0),event.getX(1),event.getY(1));
                break;

            case MotionEvent.ACTION_MOVE:
                mIsShowController = false;
                if (event.getPointerCount() > 1){
                    mTextItem.set(mTempItem);
                    morePointTranslate(mTextItem,event);
                    morePointScale(mTextItem,event);
                    rotate(mTextItem,event.getX(0),event.getY(0),event.getX(1),event.getY(1));
                    if (mTextItem.mScaleX > TextItem.MAX_SCALE){
                        mTextItem.mScaleX = TextItem.MAX_SCALE;
                        mTextItem.mScaleY = TextItem.MAX_SCALE;
                    }else if (mTextItem.mScaleX < TextItem.MIN_SCALE){
                        mTextItem.mScaleX = TextItem.MIN_SCALE;
                        mTextItem.mScaleY = TextItem.MIN_SCALE;
                    }
                    postMatrix(mTextItem);
                }else {
                    if (mIsHandlingMorePoint) break;
                    if (mEditType!=null){
                        mTextItem.set(mTempItem);
                        mEditType.onMove(mTextItem,event);
                    }else {
                        if (!isPointAtText(event.getX(),event.getY(),mTextItem)) break;
                        mTextItem.set(mTempItem);
                        translate(mTextItem,event.getX(),event.getY());
                        postMatrix(mTextItem);
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                mIsShowController = isPointAtRectF(mTextItem.mRect,event.getX(),event.getY());
                mIsHandlingMorePoint = false;
                break;

            case MotionEvent.ACTION_POINTER_UP:
                break;
        }
        invalidate();
        return true;
    }

    private boolean isPointAtText(float x, float y, TextItem item){
        item.mRect.offset(item.mCenterX-item.mRect.width()/2f,item.mCenterY - item.mRect.height()/2f);
        item.mRect.left = (int) (item.mRect.left - mTextItem.mGapX);
        item.mRect.top = (int) (item.mRect.top - mTextItem.mGapY);
        item.mRect.right = (int) (item.mRect.right + mTextItem.mGapX);
        item.mRect.bottom = (int) (item.mRect.bottom + mTextItem.mGapY);
        return item.mRect.contains(x, y);
    }

    private boolean isPointAtRectF(RectF dst, float x, float y){
        if (dst.contains(x, y)){
            return true;
        }else {
            return false;
        }
    }

    private TextItem getCurTextItem(float x, float y){
        if (mItemList!=null && mItemList.size()>0){
            for (TextItem item : mItemList){
                if (item.mRect.contains(x, y)){
                    return item;
                }
            }
        }
        return null;
    }

    private IEdit getEditType(float x, float y){
        mCurController = getControlPointAt(x, y);
        if (mCurController == TextItem.DELETE){
            return new OpsDelete();
        }else if (mCurController == TextItem.ADD){
            return new OpsAdd();
        }else if (mCurController == TextItem.REVERSE){
            return new OpsReverse();
        }else if (mCurController == TextItem.ROTATEANDSCALE){
            return new OpsRotateAndScale();
        }else {
            return null;
        }
    }

    private void doReverse(Canvas canvas){

        mIsReverse = false;
    }

    class OpsAdd implements IEdit {
        @Override
        public void onDown(TextItem item, MotionEvent event) {
            TextItem mTextItemPlus = new TextItem();
            mItemList.add(mTextItemPlus);
            //layoutTextItem(mTextItemPlus);
            mTextItemPlus.set(mInitItem);
            mTextItemPlus.mCenterX += itemOffset;
            mTextItemPlus.mCenterY += itemOffset;
            mTextItemPlus.mX += itemOffset;
            mTextItemPlus.mY += itemOffset;
            postMatrix(mTextItemPlus);
            invalidate();
        }

        @Override
        public void onMove(TextItem item, MotionEvent event) {

        }
    }

    class OpsDelete implements IEdit {
        @Override
        public void onDown(TextItem item, MotionEvent event) {
            for (TextItem i : mItemList){
                if (item == i){
                    mItemList.remove(item);
                    mCurTextItem = null;
                    invalidate();
                    return;
                }
            }
        }

        @Override
        public void onMove(TextItem item, MotionEvent event) {

        }
    }

    class OpsReverse implements IEdit {
        @Override
        public void onDown(TextItem item, MotionEvent event) {

        }

        @Override
        public void onMove(TextItem item, MotionEvent event) {

        }
    }

    class OpsRotateAndScale implements IEdit {
        @Override
        public void onDown(TextItem item, MotionEvent event) {
            initScale(item.mCenterX,item.mCenterY,event.getX(),event.getY());
            initRotate(item.mCenterX,item.mCenterY,event.getX(),event.getY());
        }

        @Override
        public void onMove(TextItem item, MotionEvent event) {
            scale(item,item.mCenterX,item.mCenterY,event.getX(),event.getY());
            rotate(item,item.mCenterX,item.mCenterY,event.getX(),event.getY());
            if (item.mScaleX > TextItem.MAX_SCALE){
                item.mScaleX = TextItem.MAX_SCALE;
                item.mScaleY = TextItem.MAX_SCALE;
            }else if (item.mScaleX < TextItem.MIN_SCALE){
                item.mScaleX = TextItem.MIN_SCALE;
                item.mScaleY = TextItem.MIN_SCALE;
            }
        }
    }
}
