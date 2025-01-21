package com.musicslayer.cashmaster.data.persistent.app;

import android.content.SharedPreferences;

import com.musicslayer.cashmaster.data.bridge.DataBridge;
import com.musicslayer.cashmaster.ledger.YearLedger;
import com.musicslayer.cashmaster.util.SharedPreferencesUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class YearLedgerList implements DataBridge.ExportableToJSON {
    public String doExport() { return DataBridge.exportData(this, YearLedgerList.class); }
    public void doImport(String s) { DataBridge.importData(this, s, YearLedgerList.class); }

    // Just pick something that would never actually be saved.
    public final static String DEFAULT = "!UNKNOWN!";

    public static String getSharedPreferencesKey() {
        return "yearLedger_data";
    }

    public static void saveAllData() {
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

    public static void loadAllData() {
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

    @Override
    public void exportDataToJSON(DataBridge.Writer o) throws IOException {
        SharedPreferences sharedPreferences = SharedPreferencesUtil.getSharedPreferences(getSharedPreferencesKey());

        o.beginObject();
        o.serialize("!V!", "1", String.class);

        String sizeKey = "yearLedgers_size";
        int size = sharedPreferences.getInt(sizeKey, 0);
        o.serialize(sizeKey, size, Integer.class);

        for(int i = 0; i < size; i++) {
            String key = "yearLedger_" + i;
            String serialString = sharedPreferences.getString(key, DEFAULT);
            o.serialize(key, serialString, String.class);
        }

        String currentYearKey = "currentYearLedger_year";
        int currentYear = sharedPreferences.getInt(currentYearKey, 0);
        o.serialize(currentYearKey, currentYear, Integer.class);

        o.endObject();
    }

    @Override
    public void importDataFromJSON(DataBridge.Reader o) throws IOException {
        o.beginObject();

        String version = o.deserialize("!V!", String.class);
        if(!"1".equals(version)) {
            throw new IllegalStateException("version = " + version);
        }

        SharedPreferences sharedPreferences = SharedPreferencesUtil.getSharedPreferences(getSharedPreferencesKey());
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();

        String sizeKey = "yearLedgers_size";
        int size = o.deserialize(sizeKey, Integer.class);
        editor.putInt(sizeKey, size);

        for(int i = 0; i < size; i++) {
            String key = "yearLedger_" + i;
            String value = o.deserialize(key, String.class);
            editor.putString(key, DataBridge.cycleSerialization(value, YearLedger.class));
        }

        String currentYearKey = "currentYearLedger_year";
        int currentYear = o.deserialize(currentYearKey, Integer.class);
        editor.putInt(currentYearKey, currentYear);

        editor.apply();

        o.endObject();

        // Reinitialize data.
        loadAllData();
    }
}
