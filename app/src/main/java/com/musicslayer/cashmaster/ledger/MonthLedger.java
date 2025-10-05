package com.musicslayer.cashmaster.ledger;

import androidx.annotation.NonNull;

import com.musicslayer.cashmaster.data.bridge.DataBridge;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;

public class MonthLedger {
    public int year;
    public String month;
    public ArrayList<LineItem> lineItems = new ArrayList<>();

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

    public static final DataBridge.Serializer<MonthLedger> SERIALIZER = new DataBridge.Serializer<MonthLedger>() {
        @Override
        public void serialize(DataBridge.Writer writer, @NonNull MonthLedger obj) throws IOException {
            writer.beginObject()
                .serialize("!V!", "1", DataBridge.StringSerializable.SERIALIZER)
                .serialize("year", obj.year, DataBridge.IntegerSerializable.SERIALIZER)
                .serialize("month", obj.month, DataBridge.StringSerializable.SERIALIZER)
                .serializeArrayList("lineItems", obj.lineItems, LineItem.SERIALIZER)
                .endObject();
        }
    };

    public static final DataBridge.Deserializer<MonthLedger> DESERIALIZER = new DataBridge.Deserializer<MonthLedger>() {
        @NonNull
        @Override
        public MonthLedger deserialize(DataBridge.Reader reader) throws IOException {
            reader.beginObject();

            String version = reader.deserialize("!V!", DataBridge.StringSerializable.DESERIALIZER);
            MonthLedger monthLedger = new MonthLedger();

            if("1".equals(version)) {
                int year = reader.deserialize("year", DataBridge.IntegerSerializable.DESERIALIZER);
                String month = reader.deserialize("month", DataBridge.StringSerializable.DESERIALIZER);
                ArrayList<LineItem> lineItems = reader.deserializeArrayList("lineItems", LineItem.DESERIALIZER);
                reader.endObject();

                monthLedger.year = year;
                monthLedger.month = month;
                monthLedger.lineItems = lineItems;
            }
            else {
                throw new IllegalStateException("version = " + version);
            }

            return monthLedger;
        }
    };
}
