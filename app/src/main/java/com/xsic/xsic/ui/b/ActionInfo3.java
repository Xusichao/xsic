package com.xsic.xsic.ui.b;

import android.graphics.Matrix;

public class ActionInfo3 {
    private Matrix mMatrix;                 //矩阵

    private float mRealScale;               //真实缩放倍数    相对于还没初始化时的大小
    private float mScale;                   //相对于1.0的放大倍数

    //偏移量包含初始偏移
    private float mTranslateX;              //x轴平移量
    private float mTranslateY;              //y轴平移量

    //不包括初始偏移量
    private float tempTranslateX;
    private float tempTranslateY;

    private float mRotate;                  //旋转角度

    private float mBitmapHeight;            //当前图片高度
    private float mBitmapWidth;             //当前图片宽度

    //双指操作
    private float mMiddleOfTwoPointX;       //两指中点的X轴
    private float mMiddleOfTwoPointY;       //两指中点的Y轴

    private float mDistanceOfPoint;         //两指距离
    private float mDistanceOfPointFirst;    //刚接触时的两指距离
    //单指操作
    private float mTouchX;                  //单指接触时的X轴
    private float mTouchY;                  //单指接触时的Y轴

    //用于放大后平移回弹效果的四条边上的中点
    private float mTopPoint;        //上边界：只关注 Y 轴，Y为 + 时回弹
    private float mLeftPoint;       //左边界：只关注 X 轴，X为 + 时回弹
    private float mRightPoint;      //右边界：只关注 X 轴，X为 - 时回弹
    private float mBottomPoint;     //下边界：只关注 Y 轴，Y为 - 时回弹
    //四个角的坐标点
    private float mTopLeft_X;
    private float mTopLeft_Y;
    private float mTopRight_X;
    private float mTopRight_Y;
    private float mBottomLeft_X;
    private float mBottomLeft_Y;
    private float mBottomRight_X;
    private float mBottomRight_Y;

    public ActionInfo3() {
        mMatrix = new Matrix();
    }

    public Matrix getmMatrix() {
        return mMatrix;
    }

    public void setmMatrix(float[] values) {
        mMatrix.setValues(values);
    }

    public float getmScale() {
        return mScale;
    }

    public void setmScale(float mScale) {
        this.mScale = mScale;
    }


    public float getmRealScale() {
        return mRealScale;
    }

    public void setmRealScale(float mRealScale) {
        this.mRealScale = mRealScale;
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

    public float getmBitmapHeight() {
        return mBitmapHeight;
    }

    public void setmBitmapHeight(float mBitmapHeight) {
        this.mBitmapHeight = mBitmapHeight;
    }

    public float getmBitmapWidth() {
        return mBitmapWidth;
    }

    public void setmBitmapWidth(float mBitmapWidth) {
        this.mBitmapWidth = mBitmapWidth;
    }

    public float getmMiddleOfTwoPointX() {
        return mMiddleOfTwoPointX;
    }

    public void setmMiddleOfTwoPointX(float mMiddleOfTwoPointX) {
        this.mMiddleOfTwoPointX = mMiddleOfTwoPointX;
    }

    public float getmMiddleOfTwoPointY() {
        return mMiddleOfTwoPointY;
    }

    public void setmMiddleOfTwoPointY(float mMiddleOfTwoPointY) {
        this.mMiddleOfTwoPointY = mMiddleOfTwoPointY;
    }

    public float getmDistanceOfPoint() {
        return mDistanceOfPoint;
    }

    public void setmDistanceOfPoint(float mDistanceOfPoint) {
        this.mDistanceOfPoint = mDistanceOfPoint;
    }

    public float getmDistanceOfPointFirst() {
        return mDistanceOfPointFirst;
    }

    public void setmDistanceOfPointFirst(float mDistanceOfPointFirst) {
        this.mDistanceOfPointFirst = mDistanceOfPointFirst;
    }

    public float getmTouchX() {
        return mTouchX;
    }

    public void setmTouchX(float mTouchX) {
        this.mTouchX = mTouchX;
    }

    public float getmTouchY() {
        return mTouchY;
    }

    public void setmTouchY(float mTouchY) {
        this.mTouchY = mTouchY;
    }

    public float getmTopPoint() {
        return mTopPoint;
    }

    public void setmTopPoint(float mTopPoint) {
        this.mTopPoint = mTopPoint;
    }

    public float getmLeftPoint() {
        return mLeftPoint;
    }

    public void setmLeftPoint(float mLeftPoint) {
        this.mLeftPoint = mLeftPoint;
    }

    public float getmRightPoint() {
        return mRightPoint;
    }

    public void setmRightPoint(float mRightPoint) {
        this.mRightPoint = mRightPoint;
    }

    public float getmBottomPoint() {
        return mBottomPoint;
    }

    public void setmBottomPoint(float mBottomPoint) {
        this.mBottomPoint = mBottomPoint;
    }

    public float getmTopLeft_X() {
        return mTopLeft_X;
    }

    public void setmTopLeft(float mTopLeft_X, float mTopLeft_Y) {
        this.mTopLeft_X = mTopLeft_X;
        this.mTopLeft_Y = mTopLeft_Y;
    }

    public float getmTopLeft_Y() {
        return mTopLeft_Y;
    }

    public float getmTopRight_X() {
        return mTopRight_X;
    }

    public void setmTopRight(float mTopRight_X, float mTopRight_Y) {
        this.mTopRight_X = mTopRight_X;
        this.mTopRight_Y = mTopRight_Y;
    }

    public float getmTopRight_Y() {
        return mTopRight_Y;
    }

    public float getmBottomLeft_X() {
        return mBottomLeft_X;
    }

    public void setmBottomLeft(float mBottomLeft_X, float mBottomLeft_Y) {
        this.mBottomLeft_X = mBottomLeft_X;
        this.mBottomLeft_Y = mBottomLeft_Y;
    }

    public float getmBottomLeft_Y() {
        return mBottomLeft_Y;
    }

    public float getmBottomRight_X() {
        return mBottomRight_X;
    }

    public void setmBottomRight(float mBottomRight_X, float mBottomRight_Y) {
        this.mBottomRight_X = mBottomRight_X;
        this.mBottomRight_Y = mBottomRight_Y;
    }

    public float getmBottomRight_Y() {
        return mBottomRight_Y;
    }

    public float getTempTranslateX() {
        return tempTranslateX;
    }

    public void setTempTranslateX(float tempTranslateX) {
        this.tempTranslateX = tempTranslateX;
    }

    public float getTempTranslateY() {
        return tempTranslateY;
    }

    public void setTempTranslateY(float tempTranslateY) {
        this.tempTranslateY = tempTranslateY;
    }

    public void clearProperty(){
        // TODO: 2020/7/2
        mMatrix.reset();
    }
}
