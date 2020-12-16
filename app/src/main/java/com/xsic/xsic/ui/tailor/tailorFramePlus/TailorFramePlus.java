package com.xsic.xsic.ui.tailor.tailorFramePlus;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.xsic.xsic.utils.ScreenUtil;

public class TailorFramePlus extends View {

    private Paint mPaint;
    private Paint mControllerPaint;
    private Paint mLinePaint;

    private Path mController_1 = new Path();    //左上
    private Path mController_2 = new Path();    //右上
    private Path mController_3 = new Path();    //右下
    private Path mController_4 = new Path();    //左下

    private Path mLine_1 = new Path();
    private Path mLine_2 = new Path();
    private Path mLine_3 = new Path();
    private Path mLine_4 = new Path();

    private final int mColor = Color.WHITE;
    private RectF mRectF = new RectF();
    private ViewSupportPlus mViewSupport = new ViewSupportPlus();
    private final float mControllerLength = 100;      //控制点的长度

    private Matrix mMatrix = new Matrix();

    public TailorFramePlus(Context context) {
        this(context,null,0);
    }

    public TailorFramePlus(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TailorFramePlus(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
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
        mController_1.moveTo(mViewSupport.mRectF.left,mViewSupport.mRectF.top);
        mController_1.lineTo(mViewSupport.mRectF.left,mViewSupport.mRectF.top + mControllerLength);
        mController_1.moveTo(mViewSupport.mRectF.left,mViewSupport.mRectF.top);
        mController_1.lineTo(mViewSupport.mRectF.left + mControllerLength,mViewSupport.mRectF.top);
        canvas.drawPath(mController_1,mControllerPaint);

        mController_2.moveTo(mViewSupport.mRectF.right,mViewSupport.mRectF.top);
        mController_2.lineTo(mViewSupport.mRectF.right,mViewSupport.mRectF.top + mControllerLength);
        mController_2.moveTo(mViewSupport.mRectF.right,mViewSupport.mRectF.top);
        mController_2.lineTo(mViewSupport.mRectF.right - mControllerLength,mViewSupport.mRectF.top);
        canvas.drawPath(mController_2,mControllerPaint);

        mController_3.moveTo(mViewSupport.mRectF.right,mViewSupport.mRectF.bottom);
        mController_3.lineTo(mViewSupport.mRectF.right,mViewSupport.mRectF.bottom - mControllerLength);
        mController_3.moveTo(mViewSupport.mRectF.right,mViewSupport.mRectF.bottom);
        mController_3.lineTo(mViewSupport.mRectF.right - mControllerLength,mViewSupport.mRectF.bottom);
        canvas.drawPath(mController_3,mControllerPaint);

        mController_4.moveTo(mViewSupport.mRectF.left,mViewSupport.mRectF.bottom);
        mController_4.lineTo(mViewSupport.mRectF.left,mViewSupport.mRectF.bottom - mControllerLength);
        mController_4.moveTo(mViewSupport.mRectF.left,mViewSupport.mRectF.bottom);
        mController_4.lineTo(mViewSupport.mRectF.left + mControllerLength,mViewSupport.mRectF.bottom);
        canvas.drawPath(mController_4,mControllerPaint);
    }

    private void drawLine(Canvas canvas){
        mLine_1.moveTo((mViewSupport.mRectF.right - mViewSupport.mRectF.left) * (1f/3f) + mViewSupport.mRectF.left,mViewSupport.mRectF.top);
        mLine_1.lineTo((mViewSupport.mRectF.right - mViewSupport.mRectF.left) * (1f/3f) + mViewSupport.mRectF.left,mViewSupport.mRectF.bottom);
        canvas.drawPath(mLine_1,mLinePaint);

        mLine_2.moveTo((mViewSupport.mRectF.right - mViewSupport.mRectF.left) * (2f/3f) + mViewSupport.mRectF.left,mViewSupport.mRectF.top);
        mLine_2.lineTo((mViewSupport.mRectF.right - mViewSupport.mRectF.left) * (2f/3f) + mViewSupport.mRectF.left,mViewSupport.mRectF.bottom);
        canvas.drawPath(mLine_2,mLinePaint);

        mLine_3.moveTo(mViewSupport.mRectF.left,(mViewSupport.mRectF.bottom - mViewSupport.mRectF.top) * (1f/3f) + mViewSupport.mRectF.top);
        mLine_3.lineTo(mViewSupport.mRectF.right,(mViewSupport.mRectF.bottom - mViewSupport.mRectF.top) * (1f/3f) + mViewSupport.mRectF.top);
        canvas.drawPath(mLine_3,mLinePaint);

        mLine_4.moveTo(mViewSupport.mRectF.left,(mViewSupport.mRectF.bottom - mViewSupport.mRectF.top) * (2f/3f) + mViewSupport.mRectF.top);
        mLine_4.lineTo(mViewSupport.mRectF.right,(mViewSupport.mRectF.bottom - mViewSupport.mRectF.top) * (2f/3f) + mViewSupport.mRectF.top);
        canvas.drawPath(mLine_4,mLinePaint);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        clearPath();
        canvas.drawColor(Color.BLACK);
        mRectF.set(mViewSupport.mRectF);
        canvas.drawRect(mRectF,mPaint);
        drawController(canvas);
        drawLine(canvas);
    }

    private void clearPath(){
        mController_1.reset();
        mController_2.reset();
        mController_3.reset();
        mController_4.reset();
        mLine_1.reset();
        mLine_2.reset();
        mLine_3.reset();
        mLine_4.reset();
    }


}
