package com.musicslayer.cashmaster.util;

import android.app.Activity;
import android.content.pm.ActivityInfo;

import androidx.appcompat.app.AppCompatDelegate;

import com.musicslayer.cashmaster.data.persistent.app.Appearance;

public class AppearanceUtil {
    // Needs activity, not Context
    public static void applyAppearance(Activity activity) {
        applyColor(activity);
        applyMode();
        applyOrientation(activity);
    }

    public static void applyColor(Activity activity) {
        activity.setTheme(Appearance.getCurrentColorID());
    }

    public static void applyMode() {
        int desiredMode;
        if("auto".equals(Appearance.mode_setting)) {
            desiredMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        }
        else if("light".equals(Appearance.mode_setting)) {
            desiredMode = AppCompatDelegate.MODE_NIGHT_NO;
        }
        else if("dark".equals(Appearance.mode_setting)) {
            desiredMode = AppCompatDelegate.MODE_NIGHT_YES;
        }
        else {
            throw new IllegalStateException("mode_setting = " + Appearance.mode_setting);
        }

        AppCompatDelegate.setDefaultNightMode(desiredMode);
    }

    public static void applyOrientation(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }
}
