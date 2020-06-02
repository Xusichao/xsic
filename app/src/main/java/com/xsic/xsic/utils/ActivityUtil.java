package com.xsic.xsic.utils;

public class ActivityUtil {
    private static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 1000) {

            return true;
        }
        lastClickTime = time;
        return false;

    }
}
