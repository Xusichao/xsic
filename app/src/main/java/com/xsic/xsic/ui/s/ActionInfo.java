package com.xsic.xsic.ui.s;

import android.graphics.Matrix;

import com.xsic.xsic.utils.ScreenUtil;

public class ActionInfo {
    /**
     * 存放初始化、上一次操作、这次操作的所有信息
     */
    private Matrix mMatrix;
    private float mScale;
    private float mTranslateX;
    private float mTranslateY;
    private float mRotate;
    private float mCenterPointX;            //图片的中心点X
    private float mCenterPointY;            //图片的中心点Y
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
    //图片宽高
    private float mBitmapHeight;
    private float mBitmapWidth;
    //四个角的位置
    private float TopLeft_X;
    private float TopLeft_Y;
    private float TopRight_X;
    private float TopRight_Y;
    private float BottomLeft_X;
    private float BottomLeft_Y;
    private float BottomRight_X;
    private float BottomRight_Y;

    private float mTopLeft_X;
    private float mTopLeft_Y;
    private float mTopRight_X;
    private float mTopRight_Y;
    private float mBottomLeft_X;
    private float mBottomLeft_Y;
    private float mBottomRight_X;
    private float mBottomRight_Y;

    public ActionInfo() {
        mMatrix = new Matrix();
        mScale = 1.0f;
        mTranslateX = 0;
        mTranslateY = 0;
        mRotate = 0;
        mCenterPointX = ScreenUtil.getScreenWidth()/2;
        mCenterPointY = ScreenUtil.getScreenHeight()/2;
        mDistanceOfPoint = 0;
        mDistanceOfPointFirst = 0;
    }

    public float getmTopLeft_X() {
        return mTopLeft_X;
    }
    public float getmTopLeft_Y() {
        return mTopLeft_Y;
    }

    public void setmTopLeft(float mTopLeft_X,float mTopLeft_Y) {
        this.mTopLeft_X = mTopLeft_X;
        this.mTopLeft_Y = mTopLeft_Y;
    }

    public float getmTopRight_X() {
        return mTopRight_X;
    }
    public float getmTopRight_Y() {
        return mTopRight_Y;
    }

    public void setmTopRight(float mTopRight_X,float mTopRight_Y) {
        this.mTopRight_X = mTopRight_X;
        this.mTopRight_Y = mTopRight_Y;
    }

    public float getmBottomLeft_X() {
        return mBottomLeft_X;
    }
    public float getmBottomLeft_Y() {
        return mBottomLeft_Y;
    }

    public void setmBottomLeft(float mBottomLeft_X,float mBottomLeft_Y) {
        this.mBottomLeft_X = mBottomLeft_X;
        this.mBottomLeft_Y = mBottomLeft_Y;
    }

    public float getmBottomRight_X() {
        return mBottomRight_X;
    }
    public float getmBottomRight_Y() {
        return mBottomRight_Y;
    }

    public void setmBottomRight(float mBottomRight_X,float mBottomRight_Y) {
        this.mBottomRight_X = mBottomRight_X;
        this.mBottomRight_Y = mBottomRight_Y;
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

    public float getmScale() {
        return mScale;
    }

    public void setmScale(float mScale) {
        this.mScale = mScale;
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

    public float getmDistanceOfPointFirst() {
        return mDistanceOfPointFirst;
    }

    public void setmDistanceOfPointFirst(float mDistanceOfPointFirst) {
        this.mDistanceOfPointFirst = mDistanceOfPointFirst;
    }

    public float getmDistanceOfPoint() {
        return mDistanceOfPoint;
    }

    public void setmDistanceOfPoint(float mDistanceOfPoint) {
        this.mDistanceOfPoint = mDistanceOfPoint;
    }

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

    public void setmMatrix(Matrix lastMatrix){

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
