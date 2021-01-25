package com.xsic.xsic.illusionTest.editPannel.previewerView;

import android.graphics.RectF;

import com.xsic.xsic.illusionTest.base.ViewSupport;

public class RectFItem extends ViewSupport {
    public RectF mRectF = new RectF();

    public void set(RectFItem viewSupport){
        if (viewSupport != null){
            mX = viewSupport.mX;
            mY = viewSupport.mY;
            mMatrix = viewSupport.mMatrix;
            mScaleX = viewSupport.mScaleX;
            mScaleY = viewSupport.mScaleY;
            mCenterX = viewSupport.mCenterX;
            mCenterY = viewSupport.mCenterY;
            mRotate = viewSupport.mRotate;
            mRectF.set(viewSupport.mRectF);
        }
    }

    public RectFItem clone(){
        RectFItem out = new RectFItem();
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
        out.mRectF.set(mRectF);
        return out;
    }
}
