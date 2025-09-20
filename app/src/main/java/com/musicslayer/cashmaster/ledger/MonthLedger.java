package com.musicslayer.cashmaster.ledger;

import androidx.annotation.Keep;

import com.musicslayer.cashmaster.data.bridge.DataBridge;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;

public class MonthLedger implements DataBridge.SerializableToJSON {
    public int year;
    public String month;
    public ArrayList<LineItem> lineItems = new ArrayList<>();

    @Override
    public void serializeToJSON(DataBridge.Writer o) throws IOException {
        o.beginObject()
            .serialize("!V!", "1", String.class)
            .serialize("year", year, Integer.class)
            .serialize("month", month, String.class)
            .serializeArrayList("lineItems", lineItems, LineItem.class)
            .endObject();
    }

    @Keep
    public static MonthLedger deserializeFromJSON(DataBridge.Reader o) throws IOException {
        o.beginObject();

        String version = o.deserialize("!V!", String.class);
        MonthLedger monthLedger = new MonthLedger();

        if("1".equals(version)) {
            int year = o.deserialize("year", Integer.class);
            String month = o.deserialize("month", String.class);
            ArrayList<LineItem> lineItems = o.deserializeArrayList("lineItems", LineItem.class);
            o.endObject();

            monthLedger.year = year;
            monthLedger.month = month;
            monthLedger.lineItems = lineItems;
        }
        else {
            throw new IllegalStateException("version = " + version);
        }

        return monthLedger;
    }

    public void addLineItem(int year, String month, String name, BigDecimal amount, boolean isIncome) {
        LineItem lineItem = new LineItem();
        lineItem.year = year;
        lineItem.month = month;
        lineItem.name = name;
        lineItem.amount = amount;
        lineItem.isIncome = isIncome;
        lineItems.add(lineItem);
    }

    public void removeLineItem(String name) {
        for(LineItem lineItem : lineItems) {
            if(name.equals(lineItem.name)) {
                lineItems.remove(lineItem);
                break;
            }
        }
    }

    public boolean hasLineItem(String name) {
        for(LineItem lineItem : lineItems) {
            if(name.equals(lineItem.name)) {
                return true;
            }
        }
        return false;
    }

    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO.setScale(2, RoundingMode.UNNECESSARY);
        for(LineItem lineItem : lineItems) {
            if(lineItem.isIncome) {
                total = total.add(lineItem.amount);
            }
            else {
                total = total.subtract(lineItem.amount);
            }
        }
        return total;
    }

    public ArrayList<LineItem> getSortedIncomes() {
        ArrayList<LineItem> incomes = new ArrayList<>();
        for(LineItem lineItem : lineItems) {
            if(lineItem.isIncome) {
                incomes.add(lineItem);
            }
        }
        incomes.sort(new Comparator<LineItem>() {
            @Override
            public int compare(LineItem a, LineItem b) {
                // Sort in descending order
                return b.amount.compareTo(a.amount);
            }
        });
        return incomes;
    }

    public ArrayList<LineItem> getSortedExpenses() {
        ArrayList<LineItem> expenses = new ArrayList<>();
        for(LineItem lineItem : lineItems) {
            if(!lineItem.isIncome) {
                expenses.add(lineItem);
            }
        }
        expenses.sort(new Comparator<LineItem>() {
            @Override
            public int compare(LineItem a, LineItem b) {
                // Sort in descending order
                return b.amount.compareTo(a.amount);
            }
        });
        return expenses;
    }
}
