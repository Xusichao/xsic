package com.xsic.xsic.utils;

import android.graphics.Color;

public class LinearGradientUtil {

    /**
     * 获取两个颜色区间的渐变色
     * @param startColor
     * @param endColor
     * @param radio
     * @return
     */
    public static int getColor(int startColor, int endColor, double radio) {
        int redStart = Color.red(startColor);
        int blueStart = Color.blue(startColor);
        int greenStart = Color.green(startColor);
        int redEnd = Color.red(endColor);
        int blueEnd = Color.blue(endColor);
        int greenEnd = Color.green(endColor);

        int red = (int) (redStart + ((redEnd - redStart) * radio + 0.5));
        int greed = (int) (greenStart + ((greenEnd - greenStart) * radio + 0.5));
        int blue = (int) (blueStart + ((blueEnd - blueStart) * radio + 0.5));
        return Color.argb(255,red, greed, blue);
    }
}
