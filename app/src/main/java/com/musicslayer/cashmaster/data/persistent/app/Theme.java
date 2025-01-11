package com.musicslayer.cashmaster.data.persistent.app;

import android.content.SharedPreferences;

import com.musicslayer.cashmaster.util.SharedPreferencesUtil;

public class Theme {
    // auto, light, or dark
    public static String theme_setting;

    public String getSharedPreferencesKey() {
        return "theme_data";
    }

    public static void cycleTheme() {
        if("auto".equals(theme_setting)) {
            theme_setting = "light";
        }
        else if("light".equals(theme_setting)) {
            theme_setting = "dark";
        }
        else {
            theme_setting = "auto";
        }

        new Theme().saveAllData();
    }

    public void saveAllData() {
        SharedPreferences sharedPreferences = SharedPreferencesUtil.getSharedPreferences(getSharedPreferencesKey());
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();
        editor.putString("theme_data", theme_setting);
        editor.apply();
    }

    public void loadAllData() {
        SharedPreferences sharedPreferences = SharedPreferencesUtil.getSharedPreferences(getSharedPreferencesKey());
        theme_setting = sharedPreferences.getString("theme_data", "auto");
    }
}
