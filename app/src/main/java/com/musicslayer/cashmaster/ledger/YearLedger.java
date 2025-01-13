package com.musicslayer.cashmaster.ledger;

import com.musicslayer.cashmaster.data.bridge.DataBridge;
import com.musicslayer.cashmaster.data.persistent.app.YearLedgerList;
import com.musicslayer.cashmaster.util.HashMapUtil;

import java.io.IOException;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class YearLedger implements DataBridge.SerializableToJSON {
    public static HashMap<Integer, YearLedger> map_yearLedgers = new HashMap<>();
    public static YearLedger currentYearLedger;

    public int year;
    public ArrayList<MonthLedger> monthLedgers = new ArrayList<>();

    @Override
    public void serializeToJSON(DataBridge.Writer o) throws IOException {
        o.beginObject()
                .serialize("!V!", "1", String.class)
                .serialize("year", year, Integer.class)
                .serializeArrayList("monthLedgers", monthLedgers, MonthLedger.class)
                .endObject();
    }

    public static YearLedger deserializeFromJSON(DataBridge.Reader o) throws IOException {
        o.beginObject();

        String version = o.deserialize("!V!", String.class);
        YearLedger yearLedger = new YearLedger();

        if("1".equals(version)) {
            int year = o.deserialize("year", Integer.class);
            ArrayList<MonthLedger> monthLedgers = o.deserializeArrayList("monthLedgers", MonthLedger.class);
            o.endObject();

            yearLedger.year = year;
            yearLedger.monthLedgers = monthLedgers;
        }
        else {
            throw new IllegalStateException("version = " + version);
        }

        return yearLedger;
    }

    public static void createDefaultIfNeeded() {
        if(map_yearLedgers.isEmpty()) {
            // Use the current year.
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            addYearLedgerNoSave(currentYear);

            YearLedger.currentYearLedger = getYearLedger(currentYear);

            new YearLedgerList().saveAllData();
        }
    }

    public static void addYearLedgerNoSave(int year) {
        YearLedger yearLedger = new YearLedger();
        yearLedger.year = year;

        // Add all 12 months up front.
        ArrayList<String> ALL_MONTHS = new ArrayList<>();
        ALL_MONTHS.add("January");
        ALL_MONTHS.add("February");
        ALL_MONTHS.add("March");
        ALL_MONTHS.add("April");
        ALL_MONTHS.add("May");
        ALL_MONTHS.add("June");
        ALL_MONTHS.add("July");
        ALL_MONTHS.add("August");
        ALL_MONTHS.add("September");
        ALL_MONTHS.add("October");
        ALL_MONTHS.add("November");
        ALL_MONTHS.add("December");

        for(String month : ALL_MONTHS) {
            MonthLedger monthLedger = new MonthLedger();
            monthLedger.year = year;
            monthLedger.month = month;

            // TODO To debug, just add some values here.
            monthLedger.addLineItem("First", 100, true);
            monthLedger.addLineItem("Second", 50, false);

            yearLedger.monthLedgers.add(monthLedger);
        }

        HashMapUtil.putValueInMap(map_yearLedgers, year, yearLedger);
    }

    public static YearLedger getYearLedger(int year) {
        return HashMapUtil.getValueFromMap(map_yearLedgers, year);
    }

    public static void addYear(int year) {
        addYearLedgerNoSave(year);

        new YearLedgerList().saveAllData();
    }

    public static void removeYear(int year) {
        HashMapUtil.removeValueFromMap(map_yearLedgers, year);

        new YearLedgerList().saveAllData();
    }

    public void addMonthNoSave(int year, String month) {
        MonthLedger monthLedger = new MonthLedger();
        monthLedger.year = year;
        monthLedger.month = month;
        monthLedgers.add(monthLedger);
    }

    public void addMonth(String month) {
        addMonthNoSave(year, month);

        new YearLedgerList().saveAllData();
    }

    public void removeMonth(String month) {
        for(MonthLedger monthLedger : monthLedgers) {
            if(month.equals(monthLedger.month)) {
                monthLedgers.remove(monthLedger);
                break;
            }
        }

        new YearLedgerList().saveAllData();
    }

    public boolean hasMonth(String month) {
        for(MonthLedger monthLedger : monthLedgers) {
            if(month.equals(monthLedger.month)) {
                return true;
            }
        }
        return false;
    }

    public MonthLedger getMonthLedger(String month) {
        for(MonthLedger monthLedger : monthLedgers) {
            if(month.equals(monthLedger.month)) {
                return monthLedger;
            }
        }
        return null;
    }

    public int getTotal() {
        int total = 0;
        for(MonthLedger monthLedger : monthLedgers) {
            total += monthLedger.getTotal();
        }
        return total;
    }
}
