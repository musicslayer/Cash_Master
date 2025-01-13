package com.musicslayer.cashmaster.ledger;

import com.musicslayer.cashmaster.data.bridge.DataBridge;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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

    public void addLineItem(String name, int amount, boolean isIncome) {
        LineItem lineItem = new LineItem();
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

    public int getTotal() {
        int total = 0;
        for(LineItem lineItem : lineItems) {
            if(lineItem.isIncome) {
                total += lineItem.amount;
            }
            else {
                total -= lineItem.amount;
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
        Collections.sort(incomes, new Comparator<LineItem>() {
            @Override
            public int compare(LineItem a, LineItem b) {
                // Sort in descending order
                return b.amount - a.amount;
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
        Collections.sort(expenses, new Comparator<LineItem>() {
            @Override
            public int compare(LineItem a, LineItem b) {
                // Sort in descending order
                return b.amount - a.amount;
            }
        });
        return expenses;
    }
}
