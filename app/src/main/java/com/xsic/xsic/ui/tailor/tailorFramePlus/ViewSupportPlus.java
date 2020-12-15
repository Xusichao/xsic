package com.xsic.xsic.ui.tailor.tailorFramePlus;

import android.graphics.Matrix;
import android.graphics.RectF;

public class ViewSupportPlus {
    public RectF mRectF;
    public Matrix mMatrix;
    public float mX;
    public float mY;
    public float mCenterX, mCenterY;

    public void set(ViewSupportPlus viewSupportPlus){
        if (viewSupportPlus != null){
            mRectF = viewSupportPlus.mRectF;
            mX = viewSupportPlus.mX;
            mY = viewSupportPlus.mY;
            mMatrix = viewSupportPlus.mMatrix;
            mCenterX = viewSupportPlus.mCenterX;
            mCenterY = viewSupportPlus.mCenterY;
        }
    }

    public ViewSupportPlus clone(){
        ViewSupportPlus out = new ViewSupportPlus();
        out.mRectF.set(mRectF);
        out.mMatrix.set(mMatrix);
        out.mX = mX;
        out.mY = mY;
        out.mCenterX = mCenterX;
        out.mCenterY = mCenterY;
        return out;
    }
}
