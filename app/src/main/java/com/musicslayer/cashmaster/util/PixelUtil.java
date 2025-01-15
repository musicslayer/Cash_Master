package com.musicslayer.cashmaster.util;

import android.content.Context;

public class PixelUtil {
    public static int dpToPx(int dp, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}