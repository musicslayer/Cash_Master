package com.musicslayer.cashmaster.data.persistent.app;

import android.content.SharedPreferences;

import com.musicslayer.cashmaster.data.bridge.DataBridge;
import com.musicslayer.cashmaster.ledger.YearLedger;
import com.musicslayer.cashmaster.util.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class YearLedgerList {
    public String getSharedPreferencesKey() {
        return "yearLedger_data";
    }

    public void saveAllData() {
        SharedPreferences sharedPreferences = SharedPreferencesUtil.getSharedPreferences(getSharedPreferencesKey());
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();

        ArrayList<YearLedger> yearLedgers = new ArrayList<>(YearLedger.map_yearLedgers.values());

        int size = yearLedgers.size();
        editor.putInt("yearLedgers_size", size);

        for(int i = 0; i < size; i++) {
            YearLedger yearLedger = yearLedgers.get(i);
            editor.putString("yearLedger_" + i, DataBridge.serialize(yearLedger, YearLedger.class));
        }

        editor.putInt("currentYearLedger_year", YearLedger.currentYearLedger.year);

        editor.apply();
    }

    public void loadAllData() {
        YearLedger.map_yearLedgers = new HashMap<>();

        SharedPreferences sharedPreferences = SharedPreferencesUtil.getSharedPreferences(getSharedPreferencesKey());

        int size = sharedPreferences.getInt("yearLedgers_size", 0);

        for(int i = 0; i < size; i++) {
            YearLedger yearLedger = DataBridge.deserialize(sharedPreferences.getString("yearLedger_" + i, "?"), YearLedger.class);
            YearLedger.map_yearLedgers.put(yearLedger.year, yearLedger);
        }

        int currentYearLedgerYear = sharedPreferences.getInt("currentYearLedger_year", 0);
        YearLedger.currentYearLedger = YearLedger.map_yearLedgers.get(currentYearLedgerYear);
    }
}
