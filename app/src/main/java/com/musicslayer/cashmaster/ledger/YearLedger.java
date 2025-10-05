package com.musicslayer.cashmaster.ledger;

import androidx.annotation.NonNull;

import com.musicslayer.cashmaster.data.bridge.DataBridge;
import com.musicslayer.cashmaster.data.persistent.app.LedgerData;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;

public class YearLedger {
    public int year;
    public ArrayList<MonthLedger> monthLedgers = new ArrayList<>();

    public void addLineItem(String month, String name, BigDecimal amount, boolean isIncome) {
        MonthLedger monthLedger = getMonthLedger(month);
        if(monthLedger == null) {
            throw new IllegalStateException("Month does not exist. month = " + month);
        }
        monthLedger.addLineItem(year, month, name, amount, isIncome);

        LedgerData.saveAllData();
    }

    public void removeLineItem(String month, String name) {
        MonthLedger monthLedger = getMonthLedger(month);
        if(monthLedger == null) {
            throw new IllegalStateException("Month does not exist. month = " + month);
        }
        monthLedger.removeLineItem(name);

        LedgerData.saveAllData();
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

    public static final DataBridge.Serializer<YearLedger> SERIALIZER = new DataBridge.Serializer<YearLedger>() {
        @Override
        public void serialize(DataBridge.Writer writer, @NonNull YearLedger obj) throws IOException {
            writer.beginObject();
            writer.serialize("!V!", "1", DataBridge.StringSerializable.SERIALIZER);
            writer.serialize("year", obj.year, DataBridge.IntegerSerializable.SERIALIZER);
            writer.serializeArrayList("monthLedgers", obj.monthLedgers, MonthLedger.SERIALIZER);
            writer.endObject();
        }
    };

    public static final DataBridge.Deserializer<YearLedger> DESERIALIZER = new DataBridge.Deserializer<YearLedger>() {
        @NonNull
        @Override
        public YearLedger deserialize(DataBridge.Reader reader) throws IOException {
            reader.beginObject();

            String version = reader.deserialize("!V!", DataBridge.StringSerializable.DESERIALIZER);
            YearLedger yearLedger = new YearLedger();

            if("1".equals(version)) {
                int year = reader.deserialize("year", DataBridge.IntegerSerializable.DESERIALIZER);
                ArrayList<MonthLedger> monthLedgers = reader.deserializeArrayList("monthLedgers", MonthLedger.DESERIALIZER);
                reader.endObject();

                yearLedger.year = year;
                yearLedger.monthLedgers = monthLedgers;
            }
            else {
                throw new IllegalStateException("version = " + version);
            }

            return yearLedger;
        }
    };
}
