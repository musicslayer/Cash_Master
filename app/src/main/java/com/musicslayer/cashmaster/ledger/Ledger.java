package com.musicslayer.cashmaster.ledger;

import androidx.annotation.NonNull;

import com.musicslayer.cashmaster.R;
import com.musicslayer.cashmaster.app.App;
import com.musicslayer.cashmaster.data.bridge.DataBridge;
import com.musicslayer.cashmaster.data.persistent.app.LedgerData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class Ledger {
    public HashMap<Integer, YearLedger> map_yearLedgers = new HashMap<>();
    public YearLedger currentYearLedger;

    public void createDefaultIfNeeded() {
        if(map_yearLedgers.isEmpty()) {
            // Use the current year.
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            addYearLedgerNoSave(currentYear);
            currentYearLedger = getYearLedger(currentYear);

            LedgerData.saveAllData();
        }
    }

    public void addYearLedgerNoSave(int year) {
        YearLedger yearLedger = new YearLedger();
        yearLedger.year = year;

        // Add all 12 months up front.
        String[] months = App.applicationContext.getResources().getStringArray(R.array.month_names);
        for(String month : months) {
            MonthLedger monthLedger = new MonthLedger();
            monthLedger.year = year;
            monthLedger.month = month;

            yearLedger.monthLedgers.add(monthLedger);
        }

        map_yearLedgers.put(year, yearLedger);
    }

    public YearLedger getYearLedger(int year) {
        return map_yearLedgers.get(year);
    }

    public void setCurrentYear(int year) {
        currentYearLedger = getYearLedger(year);
        LedgerData.saveAllData();
    }

    public void addYear(int year) {
        addYearLedgerNoSave(year);

        LedgerData.saveAllData();
    }

    public void removeYear(int year) {
        map_yearLedgers.remove(year);

        LedgerData.saveAllData();
    }

    public int getNearestYear(int year) {
        // If the year is the smallest then return the next largest, else return the next smallest.
        // It is assumed that A) year is in the list, and B) it is not the only year there.
        ArrayList<Integer> years = getAllYears();
        int index = years.indexOf(year);

        if(index == 0) {
            return years.get(index + 1);
        }
        else {
            return years.get(index - 1);
        }
    }

    public ArrayList<Integer> getAllYears() {
        // Returns all the available years in order.
        ArrayList<Integer> years = new ArrayList<>(map_yearLedgers.keySet());
        years.sort(null);
        return years;
    }

    public boolean hasYear(int year) {
        return map_yearLedgers.get(year) != null;
    }

    public static final DataBridge.Serializer<Ledger> SERIALIZER = new DataBridge.Serializer<Ledger>() {
        @Override
        public void serialize(DataBridge.Writer writer, @NonNull Ledger obj) throws IOException {
            writer.beginObject()
                .serialize("!V!", "2", DataBridge.StringSerializable.SERIALIZER)
                .serializeHashMap("yearLedgers", obj.map_yearLedgers, DataBridge.IntegerSerializable.SERIALIZER, YearLedger.SERIALIZER)
                .serialize("currentYear", obj.currentYearLedger.year, DataBridge.IntegerSerializable.SERIALIZER)
                .endObject();
        }
    };

    public static final DataBridge.Deserializer<Ledger> DESERIALIZER = new DataBridge.Deserializer<Ledger>() {
        @NonNull
        @Override
        public Ledger deserialize(DataBridge.Reader reader) throws IOException {
            reader.beginObject();

            String version = reader.deserialize("!V!", DataBridge.StringSerializable.DESERIALIZER);
            Ledger ledger = new Ledger();

            if("1".equals(version)) {
                int size = reader.deserialize("yearLedgers_size", DataBridge.IntegerSerializable.DESERIALIZER);
                for(int i = 0; i < size; i++) {
                    String key = "yearLedger_" + i;
                    String value = reader.deserialize(key, DataBridge.StringSerializable.DESERIALIZER);
                    YearLedger yearLedger = DataBridge.deserialize(value, YearLedger.DESERIALIZER);
                    ledger.map_yearLedgers.put(yearLedger.year, yearLedger);
                }

                int currentYear = reader.deserialize("currentYearLedger_year", DataBridge.IntegerSerializable.DESERIALIZER);
                reader.endObject();

                ledger.currentYearLedger = ledger.map_yearLedgers.get(currentYear);
            }
            else if("2".equals(version)) {
                HashMap<Integer, YearLedger> yearLedgers = reader.deserializeHashMap("yearLedgers", DataBridge.IntegerSerializable.DESERIALIZER, YearLedger.DESERIALIZER);
                int currentYear = reader.deserialize("currentYear", DataBridge.IntegerSerializable.DESERIALIZER);
                reader.endObject();

                ledger.map_yearLedgers = yearLedgers;
                ledger.currentYearLedger = yearLedgers.get(currentYear);
            }
            else {
                throw new IllegalStateException("version = " + version);
            }

            return ledger;
        }
    };
}
