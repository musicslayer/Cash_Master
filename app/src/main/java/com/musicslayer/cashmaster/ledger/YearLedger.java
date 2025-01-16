package com.musicslayer.cashmaster.ledger;

import com.musicslayer.cashmaster.data.bridge.DataBridge;
import com.musicslayer.cashmaster.data.persistent.app.YearLedgerList;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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
            currentYearLedger = getYearLedger(currentYear);

            new YearLedgerList().saveAllData();
        }
    }

    public static void addYearLedgerNoSave(int year) {
        YearLedger yearLedger = new YearLedger();
        yearLedger.year = year;

        // Add all 12 months up front.
        for(String month : Month.ALL_MONTHS) {
            MonthLedger monthLedger = new MonthLedger();
            monthLedger.year = year;
            monthLedger.month = month;

            yearLedger.monthLedgers.add(monthLedger);
        }

        map_yearLedgers.put(year, yearLedger);
    }

    public static YearLedger getYearLedger(int year) {
        return map_yearLedgers.get(year);
    }

    public static void setCurrentYear(int year) {
        currentYearLedger = getYearLedger(year);
        new YearLedgerList().saveAllData();
    }

    public static void addYear(int year) {
        addYearLedgerNoSave(year);

        new YearLedgerList().saveAllData();
    }

    public static void removeYear(int year) {
        map_yearLedgers.remove(year);

        new YearLedgerList().saveAllData();
    }

    public static int getNearestYear(int year) {
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

    public static ArrayList<Integer> getAllYears() {
        // Returns all the available years in order.
        ArrayList<Integer> years = new ArrayList<>(YearLedger.map_yearLedgers.keySet());
        Collections.sort(years);
        return years;
    }

    public static boolean hasYear(int year) {
        return map_yearLedgers.get(year) != null;
    }

    public void addLineItem(String month, String name, BigDecimal amount, boolean isIncome) {
        MonthLedger monthLedger = getMonthLedger(month);
        if(monthLedger == null) {
            throw new IllegalStateException("Month does not exist. month = " + month);
        }
        monthLedger.addLineItem(year, month, name, amount, isIncome);

        new YearLedgerList().saveAllData();
    }

    public void removeLineItem(String month, String name) {
        MonthLedger monthLedger = getMonthLedger(month);
        if(monthLedger == null) {
            throw new IllegalStateException("Month does not exist. month = " + month);
        }
        monthLedger.removeLineItem(name);

        new YearLedgerList().saveAllData();
    }

    public boolean hasLineItem(String month, String name) {
        MonthLedger monthLedger = getMonthLedger(month);
        if(monthLedger == null) {
            return false;
        }
        return monthLedger.hasLineItem(name);
    }

    public MonthLedger getMonthLedger(String month) {
        for(MonthLedger monthLedger : monthLedgers) {
            if(month.equals(monthLedger.month)) {
                return monthLedger;
            }
        }
        return null;
    }

    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO.setScale(2, RoundingMode.UNNECESSARY);
        for(MonthLedger monthLedger : monthLedgers) {
            total = total.add(monthLedger.getTotal());
        }
        return total;
    }

    public HashMap<String, BigDecimal> getSums() {
        HashMap<String, BigDecimal> sums = new HashMap<>();

        for(MonthLedger monthLedger : monthLedgers) {
            for(LineItem lineItem : monthLedger.lineItems) {
                BigDecimal total = sums.get(lineItem.name);
                if(total == null) {
                    total = BigDecimal.ZERO.setScale(2, RoundingMode.UNNECESSARY);
                }

                if(lineItem.isIncome) {
                    total = total.add(lineItem.amount);
                }
                else {
                    total = total.subtract(lineItem.amount);
                }

                sums.put(lineItem.name, total);
            }
        }

        return sums;
    }
}
