package com.xsic.xsic.ui.tailor.tailorFrame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.xsic.xsic.utils.LogUtil;
import com.xsic.xsic.utils.ScreenUtil;

public class TailorFrameView extends View {
    private static final int STATE_CONTROL = 1;
    private static final int STATE_MOVE = 2;
    private static final int DEFAULT_STATE = 2;

    private Paint mPaint;
    private Paint mControllerPaint;
    private Paint mLinePaint;

    private final int mColor = Color.WHITE;
    private RectF mRectF = new RectF();
    private ViewSupport mViewSupport = new ViewSupport();
    private final float mControllerLength = 100;      //控制点的长度
    private float mScreenWidth = ScreenUtil.getScreenWidth();
    private float mScreenHeight = ScreenUtil.getScreenHeight();

    private Path mController_1 = new Path();    //左上
    private Path mController_2 = new Path();    //右上
    private Path mController_3 = new Path();    //右下
    private Path mController_4 = new Path();    //左下

    private Matrix mControllerMatrix_1 = new Matrix();
    private Matrix mControllerMatrix_2 = new Matrix();
    private Matrix mControllerMatrix_3 = new Matrix();
    private Matrix mControllerMatrix_4 = new Matrix();

    private Path mLine_1 = new Path();
    private Path mLine_2 = new Path();
    private Path mLine_3 = new Path();
    private Path mLine_4 = new Path();

    private float mDown_1_X;
    private float mDown_1_Y;
    private float mDown_2_X;
    private float mDown_2_Y;

    /**
     * 界限值，超过该值不响应缩放操作
     * 有可能由：
     *          1、图片大小所限制
     *          2、框的比例
     */
    private float LIMIT_TOP_LEFT_X;
    private float LIMIT_TOP_LEFT_Y;
    private float LIMIT_BOTTOM_RIGHT_X;
    private float LIMIT_BOTTOM_RIGHT_Y;

    //当前操作状态
    private int mSate = DEFAULT_STATE;

    /* 模式选择 */
    //是否自由模式
    private boolean mIsFree = true;


    public TailorFrameView(Context context) {
        this(context,null,0);
    }

    public TailorFrameView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TailorFrameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mPaint = new Paint();
        mPaint.setColor(mColor);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);

        mControllerPaint = new Paint();
        mControllerPaint.setColor(mColor);
        mControllerPaint.setAntiAlias(true);
        mControllerPaint.setStyle(Paint.Style.STROKE);
        mControllerPaint.setStrokeWidth(5);

        mLinePaint = new Paint();
        mLinePaint.setColor(mColor);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setPathEffect(new DashPathEffect(new float[]{5,5},0));
    }

    /**
     * 画四个角的控制点
     */
    private void drawController(Canvas canvas){
        mController_1.moveTo(mViewSupport.mTopLeft_X,mViewSupport.mTopLeft_Y);
        mController_1.lineTo(mViewSupport.mTopLeft_X,mViewSupport.mTopLeft_Y + mControllerLength);
        mController_1.moveTo(mViewSupport.mTopLeft_X,mViewSupport.mTopLeft_Y);
        mController_1.lineTo(mViewSupport.mTopLeft_X + mControllerLength,mViewSupport.mTopLeft_Y);
        canvas.drawPath(mController_1,mControllerPaint);

        mController_2.moveTo(mViewSupport.mTopRight_X,mViewSupport.mTopRight_Y);
        mController_2.lineTo(mViewSupport.mTopRight_X,mViewSupport.mTopRight_Y + mControllerLength);
        mController_2.moveTo(mViewSupport.mTopRight_X,mViewSupport.mTopRight_Y);
        mController_2.lineTo(mViewSupport.mTopRight_X - mControllerLength,mViewSupport.mTopRight_Y);
        canvas.drawPath(mController_2,mControllerPaint);

        mController_3.moveTo(mViewSupport.mBottomRight_X,mViewSupport.mBottomRight_Y);
        mController_3.lineTo(mViewSupport.mBottomRight_X,mViewSupport.mBottomRight_Y - mControllerLength);
        mController_3.moveTo(mViewSupport.mBottomRight_X,mViewSupport.mBottomRight_Y);
        mController_3.lineTo(mViewSupport.mBottomRight_X - mControllerLength,mViewSupport.mBottomRight_Y);
        canvas.drawPath(mController_3,mControllerPaint);

        mController_4.moveTo(mViewSupport.mBottomLeft_X,mViewSupport.mBottomLeft_Y);
        mController_4.lineTo(mViewSupport.mBottomLeft_X,mViewSupport.mBottomLeft_Y - mControllerLength);
        mController_4.moveTo(mViewSupport.mBottomLeft_X,mViewSupport.mBottomLeft_Y);
        mController_4.lineTo(mViewSupport.mBottomLeft_X + mControllerLength,mViewSupport.mBottomLeft_Y);
        canvas.drawPath(mController_4,mControllerPaint);
    }

    private void drawLine(Canvas canvas){
        mLine_1.moveTo(mViewSupport.mLine_1_start_X,mViewSupport.mLine_1_start_Y);
        mLine_1.lineTo(mViewSupport.mLine_1_end_X,mViewSupport.mLine_1_end_Y);
        canvas.drawPath(mLine_1,mLinePaint);

        mLine_2.moveTo(mViewSupport.mLine_2_start_X,mViewSupport.mLine_2_start_Y);
        mLine_2.lineTo(mViewSupport.mLine_2_end_X,mViewSupport.mLine_2_end_Y);
        canvas.drawPath(mLine_2,mLinePaint);

        mLine_3.moveTo(mViewSupport.mLine_3_start_X,mViewSupport.mLine_3_start_Y);
        mLine_3.lineTo(mViewSupport.mLine_3_end_X,mViewSupport.mLine_3_end_Y);
        canvas.drawPath(mLine_3,mLinePaint);

        mLine_4.moveTo(mViewSupport.mLine_4_start_X,mViewSupport.mLine_4_start_Y);
        mLine_4.lineTo(mViewSupport.mLine_4_end_X,mViewSupport.mLine_4_end_Y);
        canvas.drawPath(mLine_4,mLinePaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
        setmRectFSize(mViewSupport.mTop,mViewSupport.mRight,mViewSupport.mBottom,mViewSupport.mLeft);
        canvas.drawRect(mRectF,mPaint);
        drawController(canvas);
        drawLine(canvas);
    }

    private void setmRectFSize(float t, float r, float b, float l){
        mRectF.left = l;
        mRectF.top = t;
        mRectF.right = r;
        mRectF.bottom = b;
    }

    /**
     * 判断触点是否在矩形区域内
     * 依据：
     *      判断 x 是否在（right - left）中，是则说明横坐标点在同一列中
     *      判断 y 是否在（bottom - top）中，是则说明纵坐标在同一行中
     */
    private boolean isPointInRect(float touchX, float touchY){
        boolean row = false;
        boolean column = false;
        if (touchX > mViewSupport.mLeft && touchX < mViewSupport.mRight){
            column = true;
        }
        if (touchY > mViewSupport.mTop && touchY < mViewSupport.mBottom){
            row = true;
        }
        return row & column;
    }

    /**
     * 判断触点是否在控制点区域内，并非取具体的形状（直角∟），而是取具体形状形成的一个矩形
     * 依据：
     *      判断 x 是否落在（右下角x -/+ 左上角x）中
     *      判断 y 是否落在（右下角y -/+ 左上角y）中
     */
    private boolean isPointInController(float touchX, float touchY){
        //左上角控制点
        if (touchX >= mViewSupport.mTopLeft_X && touchX <= mViewSupport.mTopLeft_X + mControllerLength &&
                touchY >= mViewSupport.mTopLeft_Y && touchY <= mViewSupport.mTopLeft_Y + mControllerLength){
            return true;
        }
        //右上角控制点
        if (touchX >= mViewSupport.mTopRight_X - mControllerLength && touchX <= mViewSupport.mTopRight_X &&
                touchY >= mViewSupport.mTopLeft_Y && touchY <= mViewSupport.mTopLeft_Y + mControllerLength){
            return true;
        }
        //右下角控制点
        if (touchX >= mViewSupport.mBottomRight_X - mControllerLength && touchX <= mViewSupport.mBottomRight_X &&
                touchY >= mViewSupport.mBottomRight_Y - mControllerLength && touchY <= mViewSupport.mBottomRight_Y){
            return true;
        }
        //左下角控制点
        if (touchX >= mViewSupport.mBottomLeft_X && touchX <= mViewSupport.mBottomLeft_X + mControllerLength &&
                touchY >= mViewSupport.mBottomLeft_Y - mControllerLength && touchY <= mViewSupport.mBottomLeft_Y){
            return true;
        }
        return false;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isPointInRect(event.getX(),event.getY())){
            //接触点在操作框外
            return false;
        }
        // TODO: 2020/11/27
        if (!isPointInController(event.getX(),event.getY())){
            mSate = STATE_MOVE;
        }else {
            mSate = STATE_CONTROL;
        }
        switch (event.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
                break;

            case MotionEvent.ACTION_UP:
                break;

            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() > 1){
                    mDown_1_X = event.getX(0);
                    mDown_1_Y = event.getY(0);
                    mDown_2_X = event.getX(1);
                    mDown_2_Y = event.getY(1);
                }else {
                    mDown_1_X = event.getX();
                    mDown_1_Y = event.getY();
                    //LogUtil.d("usahdfhaf",mDown_1_X+" ， "+mDown_1_Y);
                }
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                break;

            case MotionEvent.ACTION_POINTER_UP:
                break;
        }
        return true;
    }

    private void doAction(int state){
        if (state == STATE_MOVE){

        }else if (state == STATE_CONTROL){

        }
    }

    private void move(float touchX, float touchY){

    }

    private void zoom(){

    }


}
