package com.xsic.xsic.ui.s;

import android.graphics.Matrix;

public class ActionInfo {
    /**
     * 存放初始化、上一次操作、这次操作的所有信息
     */
    private Matrix mMatrix;
    private float mScaleX;
    private float mScaleY;
    private float mTranslateX;
    private float mTranslateY;
    private float mRotate;
    private float mCenterPointX;
    private float mCenterPointY;

    public float getmDistanceOfPoint() {
        return mDistanceOfPoint;
    }

    public void setmDistanceOfPoint(float mDistanceOfPoint) {
        this.mDistanceOfPoint = mDistanceOfPoint;
    }

    private float mDistanceOfPoint;

    public float getmCenterPointX() {
        return mCenterPointX;
    }

    public void setmCenterPointX(float mCenterPointX) {
        this.mCenterPointX = mCenterPointX;
    }

    public float getmCenterPointY() {
        return mCenterPointY;
    }

    public void setmCenterPointY(float mCenterPointY) {
        this.mCenterPointY = mCenterPointY;
    }

    public Matrix getmMatrix() {
        return mMatrix;
    }

    public void setmMatrix(Matrix mMatrix) {
        this.mMatrix = mMatrix;
    }

    public float getmScaleX() {
        return mScaleX;
    }

    public void setmScaleX(float mScaleX) {
        this.mScaleX = mScaleX;
    }

    public float getmScaleY() {
        return mScaleY;
    }

    public void setmScaleY(float mScaleY) {
        this.mScaleY = mScaleY;
    }

    public float getmTranslateX() {
        return mTranslateX;
    }

    public void setmTranslateX(float mTranslateX) {
        this.mTranslateX = mTranslateX;
    }

    public float getmTranslateY() {
        return mTranslateY;
    }

    public void setmTranslateY(float mTranslateY) {
        this.mTranslateY = mTranslateY;
    }

    public float getmRotate() {
        return mRotate;
    }

    public void setmRotate(float mRotate) {
        this.mRotate = mRotate;
    }
}
