package com.musicslayer.cashmaster.data.persistent.app;

import android.content.SharedPreferences;

import com.musicslayer.cashmaster.data.bridge.DataBridge;
import com.musicslayer.cashmaster.ledger.Ledger;
import com.musicslayer.cashmaster.ledger.YearLedger;
import com.musicslayer.cashmaster.util.SharedPreferencesUtil;

import java.util.Collection;
import java.util.HashMap;

public class LedgerData {
    public static Ledger ledger;

    public static String getSharedPreferencesKey() {
        return "yearLedger_data";
    }

    public static void saveAllData() {
        SharedPreferences sharedPreferences = SharedPreferencesUtil.getSharedPreferences(getSharedPreferencesKey());
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();

        Collection<YearLedger> yearLedgers = ledger.map_yearLedgers.values();

        int size = yearLedgers.size();
        editor.putInt("yearLedgers_size", size);

        int i = 0;
        for(YearLedger yearLedger : yearLedgers) {
            editor.putString("yearLedger_" + i, DataBridge.serialize(yearLedger, YearLedger.SERIALIZER));
            i++;
        }

        editor.putInt("currentYearLedger_year", ledger.currentYearLedger.year);

        editor.apply();
    }

    public static void loadAllData() {
        ledger = new Ledger();
        ledger.map_yearLedgers = new HashMap<>();

        SharedPreferences sharedPreferences = SharedPreferencesUtil.getSharedPreferences(getSharedPreferencesKey());

        int size = sharedPreferences.getInt("yearLedgers_size", 0);

        for(int i = 0; i < size; i++) {
            YearLedger yearLedger = DataBridge.deserialize(sharedPreferences.getString("yearLedger_" + i, "?"), YearLedger.DESERIALIZER);
            ledger.map_yearLedgers.put(yearLedger.year, yearLedger);
        }

        int currentYearLedgerYear = sharedPreferences.getInt("currentYearLedger_year", 0);
        ledger.currentYearLedger = ledger.map_yearLedgers.get(currentYearLedgerYear);
    }
}
