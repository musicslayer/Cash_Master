package com.musicslayer.cashmaster.util;

import android.app.Activity;
import android.content.pm.ActivityInfo;

import androidx.appcompat.app.AppCompatDelegate;

import com.musicslayer.cashmaster.data.persistent.app.Theme;

public class AppearanceUtil {
    // Needs activity, not Context
    public static void setAppearance(Activity activity) {
        setTheme();
        setOrientation(activity);
    }

    public static void setTheme() {
        int desiredMode;
        if("auto".equals(Theme.theme_setting)) {
            desiredMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        }
        else if("light".equals(Theme.theme_setting)) {
            desiredMode = AppCompatDelegate.MODE_NIGHT_NO;
        }
        else {
            desiredMode = AppCompatDelegate.MODE_NIGHT_YES;
        }

        if(desiredMode != AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.setDefaultNightMode(desiredMode);
        }
    }

    // Needs activity, not Context
    public static void setOrientation(Activity activity) {
        if(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED != activity.getResources().getConfiguration().orientation) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }
}
