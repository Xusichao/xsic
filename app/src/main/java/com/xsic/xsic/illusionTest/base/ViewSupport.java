package com.xsic.xsic.illusionTest.base;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class ViewSupport {
    public Object mSource;
    public Bitmap mBitmap;
    public Matrix mMatrix = new Matrix();
    public float mX;
    public float mY;
    public float mScaleX = 1.0f;
    public float mScaleY = 1.0f;
    public float mCenterX, mCenterY;
    public float mRotate;

    public void set(ViewSupport viewSupport){
        if (viewSupport != null){
            mX = viewSupport.mX;
            mY = viewSupport.mY;
            mMatrix = viewSupport.mMatrix;
            mScaleX = viewSupport.mScaleX;
            mScaleY = viewSupport.mScaleY;
            mCenterX = viewSupport.mCenterX;
            mCenterY = viewSupport.mCenterY;
            mRotate = viewSupport.mRotate;
        }
    }

    public ViewSupport clone(){
        ViewSupport out = new ViewSupport();
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
        return out;
    }

    @Override
    public String toString() {
        return "ViewSupport{" +
                "mSource=" + mSource +
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
}
