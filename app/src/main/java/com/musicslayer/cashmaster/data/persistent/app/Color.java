package com.musicslayer.cashmaster.data.persistent.app;

import android.content.SharedPreferences;

import com.musicslayer.cashmaster.R;
import com.musicslayer.cashmaster.util.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Color {
    private final static HashMap<String, Integer> colorMap = new HashMap<>();
    static {
        colorMap.put("Earth", R.style.Theme_CashMaster_Earth);
        colorMap.put("Forest", R.style.Theme_CashMaster_Forest);
        colorMap.put("Mint", R.style.Theme_CashMaster_Mint);
        colorMap.put("Monochrome", R.style.Theme_CashMaster_Monochrome);
        colorMap.put("Neon", R.style.Theme_CashMaster_Neon);
        colorMap.put("Sky", R.style.Theme_CashMaster_Sky);
        colorMap.put("Sunset", R.style.Theme_CashMaster_Sunset);
    }

    public static ArrayList<String> getAllColors() {
        ArrayList<String> colors = new ArrayList<>(colorMap.keySet());
        Collections.sort(colors);
        return colors;
    }

    public static int getCurrentColorID() {
        Integer id = colorMap.get(color_setting);
        if(id == null) {
            throw new IllegalStateException("color_setting = " + color_setting);
        }
        return id;
    }

    // Monochrome, etc...
    public static String color_setting;

    public String getSharedPreferencesKey() {
        return "color_data";
    }

    public static void setColor(String name) {
        color_setting = name;

        new Color().saveAllData();
    }

    public void saveAllData() {
        SharedPreferences sharedPreferences = SharedPreferencesUtil.getSharedPreferences(getSharedPreferencesKey());
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();
        editor.putString("color_data", color_setting);
        editor.apply();
    }

    public void loadAllData() {
        SharedPreferences sharedPreferences = SharedPreferencesUtil.getSharedPreferences(getSharedPreferencesKey());
        color_setting = sharedPreferences.getString("color_data", "Monochrome");
    }
}
