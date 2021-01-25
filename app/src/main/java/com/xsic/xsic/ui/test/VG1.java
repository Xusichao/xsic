package com.xsic.xsic.ui.test;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.xsic.xsic.utils.LogUtil;

public class VG1 extends ViewGroup {
    public VG1(Context context) {
        super(context);
    }

    public VG1(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i=0;i<getChildCount();i++){
            View child = getChildAt(i);

            int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();

            int mLeft = (r-width)/2;
            int mTop = (b-height)/2;
            int mRight = width + mLeft;
            int mBottom = height + mTop;

            child.layout(mLeft,mTop,mRight,mBottom);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        LogUtil.d("EventDispatchTest","VG1 ---- dispatchTouchEvent");
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        LogUtil.d("EventDispatchTest","VG1 ---- onInterceptTouchEvent");
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LogUtil.d("EventDispatchTest","VG1 ---- onTouchEvent");
        return super.onTouchEvent(event);
    }
}
