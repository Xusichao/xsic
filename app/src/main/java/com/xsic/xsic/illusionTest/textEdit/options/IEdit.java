package com.xsic.xsic.illusionTest.textEdit.options;

import android.view.MotionEvent;

import com.xsic.xsic.illusionTest.textEdit.TextItem;

public interface IEdit {
    void onDown(TextItem item, MotionEvent event);
    void onMove(TextItem item, MotionEvent event);
}
