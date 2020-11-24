package com.xsic.xsic.ui.test;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.xsic.xsic.utils.LogUtil;

public class TestView extends View {
    public TestView(Context context) {
        super(context);
    }

    public TestView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

//    @Override
//    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        int width = getMeasuredWidth();
//        int height = getMeasuredHeight();
//
//        int mLeft = (right-width)/2;
//        int mTop = (bottom-height)/2;
//        int mRight = width + mLeft;
//        int mBottom = height + mTop;
//
//        layout(mLeft,mTop,mRight,mBottom);
//    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        LogUtil.d("EventDispatchTest","View ---- dispatchTouchEvent");
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LogUtil.d("EventDispatchTest","View ---- onTouchEvent");
        return super.onTouchEvent(event);
    }
}
