package com.musicslayer.cashmaster.util;

import android.content.Context;
import android.util.TypedValue;

import com.musicslayer.cashmaster.R;

// This class helps retrieve the color values of the currently selected theme.
public class ColorUtil {
    public static int getThemeFeature(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.theme_feature, typedValue, true);
        return typedValue.data;
    }

    public static int getThemeRed(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.theme_red, typedValue, true);
        return typedValue.data;
    }
}
