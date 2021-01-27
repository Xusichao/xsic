package com.xsic.xsic.illusionTest.textEdit;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;

import androidx.core.app.CoreComponentFactory;

import com.xsic.xsic.R;
import com.xsic.xsic.illusionTest.base.ViewSupport;
import com.xsic.xsic.utils.LogUtil;

public class TextItem extends ViewSupport {
    public static final float MAX_SCALE = 8.0f;
    public static final float MIN_SCALE = 0.4f;

    public static final int NONE = -1;
    public static final int DELETE = 1;
    public static final int REVERSE = 2;
    public static final int ADD = 3;
    public static final int ROTATEANDSCALE = 4;

    public static final int CONTROLLER_LENGTH = 60;
    public final int mBitmapSize = 25;
    public final float mGapX = 23.5f;
    public final float mGapY = 13.5f;

    public String mText;
    public float mTextSize;
    public RectF mRect = new RectF();
    public int mTextColor = Color.WHITE;
    public Typeface mTypeface;
    public float mOpacity;

    public void set(TextItem viewSupport) {
        super.set(viewSupport);
        mText = viewSupport.mText;
        mTextSize = viewSupport.mTextSize;
        mRect = viewSupport.mRect;
        mTextColor = viewSupport.mTextColor;
        mTypeface = viewSupport.mTypeface;
        mOpacity = viewSupport.mOpacity;
    }


    public TextItem clone() {
        TextItem out = new TextItem();
        out.mSource = mSource;
        out.mBitmap = mBitmap;
        out.mMatrix.set(mMatrix);
        out.mX = mX;
        out.mY = mY;
        out.mScaleX = mScaleX;
        out.mScaleY = mScaleY;
        out.mCenterX = mCenterX;
        out.mCenterY = mCenterY;
        out.mRotate = mRotate;
        out.mText = mText;
        out.mTextSize = mTextSize;
        out.mRect.set(mRect);
        out.mTextColor = mTextColor;
        out.mTypeface = mTypeface;
        out.mOpacity = mOpacity;
        return out;
    }


    @Override
    public String toString() {
        return "TextItem{" +
                "mBitmapSize=" + mBitmapSize +
                ", mGapX=" + mGapX +
                ", mGapY=" + mGapY +
                ", mText='" + mText + '\'' +
                ", mTextSize=" + mTextSize +
                ", mRect=" + mRect +
                ", mTextColor=" + mTextColor +
                ", mTypeface=" + mTypeface +
                ", mOpacity=" + mOpacity +
                ", mSource=" + mSource +
                ", mBitmap=" + mBitmap +
                ", mMatrix=" + mMatrix +
                ", mX=" + mX +
                ", mY=" + mY +
                ", mScaleX=" + mScaleX +
                ", mScaleY=" + mScaleY +
                ", mCenterX=" + mCenterX +
                ", mCenterY=" + mCenterY +
                ", mRotate=" + mRotate +
                '}';
    }

    public void debug(){
        LogUtil.d("rtrtt","ViewSupport{" +
//                "mSource=" + mSource +
//                ", mBitmap=" + mBitmap +
                ", mRect=" + mRect +
//                ", mMatrix=" + mMatrix +
//                ", mX=" + mX +
//                ", mY=" + mY +
//                ", mScaleX=" + mScaleX +
//                ", mScaleY=" + mScaleY +
//                ", mCenterX=" + mCenterX +
//                ", mCenterY=" + mCenterY +
//                ", mRotate=" + mRotate +
                '}');
    }
}
