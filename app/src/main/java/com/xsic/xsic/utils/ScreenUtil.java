package com.xsic.xsic.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.xsic.xsic.app.BaseApplication;

public class ScreenUtil {
    /**
     * 获取屏幕高
     */
    public static int getScreenWidth() {
        WindowManager windowManager = (WindowManager)  BaseApplication.getBaseApplication().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metric = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metric);

        return metric.widthPixels; // 屏幕宽度（像素）
    }

    /**
     * 获取屏幕宽度
     */
    public static int getScreenHeight() {
        WindowManager windowManager = (WindowManager)  BaseApplication.getBaseApplication().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metric = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metric);

        return metric.heightPixels; // 屏幕高度（像素）
    }
}
