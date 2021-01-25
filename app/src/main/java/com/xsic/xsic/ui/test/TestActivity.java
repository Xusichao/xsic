package com.xsic.xsic.ui.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.xsic.xsic.R;
import com.xsic.xsic.utils.LogUtil;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        LogUtil.d("EventDispatchTest","Activity ---- dispatchTouchEvent");
        return false;//super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LogUtil.d("EventDispatchTest","Activity ---- onTouchEvent");
        return super.onTouchEvent(event);
    }
}
