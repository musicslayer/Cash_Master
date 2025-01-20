package com.musicslayer.cashmaster.data.persistent.app;

import android.content.SharedPreferences;

import com.musicslayer.cashmaster.R;
import com.musicslayer.cashmaster.util.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class Appearance {
    private final static HashMap<String, Integer> colorMap = new HashMap<>();
    static {
        colorMap.put("Earth", R.style.Theme_AppTheme_Earth);
        colorMap.put("Forest", R.style.Theme_AppTheme_Forest);
        colorMap.put("Mint", R.style.Theme_AppTheme_Mint);
        colorMap.put("Monochrome", R.style.Theme_AppTheme_Monochrome);
        colorMap.put("Neon", R.style.Theme_AppTheme_Neon);
        colorMap.put("Sky", R.style.Theme_AppTheme_Sky);
        colorMap.put("Sunset", R.style.Theme_AppTheme_Sunset);
    }

    public static String color_setting; // Monochrome, etc...
    public static String mode_setting; // auto, light, or dark

    public static ArrayList<String> getAllColors() {
        ArrayList<String> colors = new ArrayList<>(colorMap.keySet());
        colors.sort(null);
        return colors;
    }

    public static int getCurrentColorID() {
        Integer id = colorMap.get(color_setting);
        if(id == null) {
            throw new IllegalStateException("color_setting = " + color_setting);
        }
        return id;
    }

    public String getSharedPreferencesKey() {
        return "appearance_data";
    }

    public static void setColor(String name) {
        color_setting = name;

        new Appearance().saveAllData();
    }

    public static void cycleMode() {
        if("auto".equals(mode_setting)) {
            setMode("light");
        }
        else if("light".equals(mode_setting)) {
            setMode("dark");
        }
        else if("dark".equals(mode_setting)) {
            setMode("auto");
        }
        else {
            throw new IllegalStateException("mode_setting = " + mode_setting);
        }
    }

    public static void setMode(String mode) {
        mode_setting = mode;

        new Appearance().saveAllData();
    }

    public void saveAllData() {
        SharedPreferences sharedPreferences = SharedPreferencesUtil.getSharedPreferences(getSharedPreferencesKey());
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();
        editor.putString("color_data", color_setting);
        editor.putString("mode_data", mode_setting);
        editor.apply();
    }

    public void loadAllData() {
        SharedPreferences sharedPreferences = SharedPreferencesUtil.getSharedPreferences(getSharedPreferencesKey());
        color_setting = sharedPreferences.getString("color_data", "Monochrome");
        mode_setting = sharedPreferences.getString("mode_data", "auto");
    }
}
